package edu.miu.waa.onlineauctionapi.service;

import edu.miu.waa.onlineauctionapi.common.ProductStatus;
import edu.miu.waa.onlineauctionapi.common.TransactionType;
import edu.miu.waa.onlineauctionapi.dto.BidResponse;
import edu.miu.waa.onlineauctionapi.exception.BidProcessingException;
import edu.miu.waa.onlineauctionapi.model.Bid;
import edu.miu.waa.onlineauctionapi.model.BidStatus;
import edu.miu.waa.onlineauctionapi.model.Billing;
import edu.miu.waa.onlineauctionapi.model.Product;
import edu.miu.waa.onlineauctionapi.model.User;
import edu.miu.waa.onlineauctionapi.repository.BidRepository;
import edu.miu.waa.onlineauctionapi.repository.BillingRepository;
import edu.miu.waa.onlineauctionapi.repository.ProductRepository;
import edu.miu.waa.onlineauctionapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BidServiceImpl.class);

  private final ProductRepository productRepository;
  private final BidRepository bidRepository;
  private final UserRepository userRepository;
  private final BillingRepository billingRepository;

  @Override
  public Bid addBid(Bid bid) {
    return bidRepository.save(bid);
  }

  @Override
  public int countTotalBidsByProductId(long id) {

    return bidRepository.countByProductIdAndBidPriceGreaterThan(id, 0);
  }

  @Override
  public boolean hasDeposit(long userId, long productId) {
    return bidRepository.existsByUserIdAndProductIdAndDepositGreaterThan(userId, productId, 0);
  }

  @Override
  public Bid getCurrentBidByProductId(long productId) {
    Bid bid = bidRepository.findTop1ByProductIdOrderByBidPriceDesc(productId);
    return bid;
  }

  @Override
  @Transactional
  public BidResponse makeDeposit(Bid bid) {
    String email = bid.getUser().getEmail();

    // check valid user
    User user = userRepository.findByEmail(email);
    if (user == null) {
      return BidResponse.builder().success(false).message("Invalid user").build();
    }

    // check balance
    double currentBalance = user.getCurrentBalance();
    if (currentBalance < bid.getDeposit()) {
      return BidResponse.builder().success(false).message("Insufficient balance to make a deposit").build();
    }

    // update balance in User table
    double balance = currentBalance - bid.getDeposit();
    user.setCurrentBalance(balance);

    // save bid
    bid.setUser(user);
    bidRepository.save(bid);

    // save billing
    Billing billing = new Billing();
    billing.setAmount(bid.getDeposit());
    billing.setType(TransactionType.DEBIT.getName());
    billing.setDetails("Deposit for product " + bid.getProduct().getId());
    billing.setTransactionDate(bid.getDepositDate());
    billing.setBalance(balance);
    billing.setUser(user);
    billingRepository.save(billing);

    return BidResponse.builder().success(true).build();
  }

  @Override
  public List<Bid> findByUserIdOrderByProductIdAscBidDateDesc(String userId) {
    return bidRepository.findByUserEmailOrderByProductIdAscBidDateDesc(userId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void settleProductBidsById(long productId) throws BidProcessingException {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new BidProcessingException("Product not found with ID: " + productId));

    // check product bid due date
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime bidDueDate = product.getBidDueDate();
    if (now.isBefore(bidDueDate)) {
      LOG.info(
          "Skipping this product since bid due date is not reached yet for product id {}",
          product.getId());
      throw new BidProcessingException("This product is not eligible for settlement!");
    }

    settleProductBids(product);
  }

  @Transactional
  public void settleProductBids(Product product) throws BidProcessingException {
    try {
      LOG.info("Settling bids for product id {}", product.getId());

      List<Bid> allBid = bidRepository.findAllByProductId(product.getId());
      if (allBid.isEmpty()) {
        // no bid for this product, mark as expired
        product.setStatus(ProductStatus.EXPIRED);
        productRepository.save(product);
        LOG.info("No bid found for product id {}", product.getId());
        return;
      }

      Bid highestBid = bidRepository.findTop1ByProductIdOrderByBidPriceDesc(product.getId());
      List<Bid> allDeposits =
          bidRepository.findByProductIdAndDepositGreaterThan(product.getId(), 0);
      Bid winnerDeposit =
          bidRepository.findTop1ByProductIdAndUserId(product.getId(), highestBid.getUser().getId());
      User winner = highestBid.getUser();
      long winnerId = winner.getId();
      double winnerCurrentBalanceBeforeDeposit =
          winner.getCurrentBalance() + winnerDeposit.getDeposit();
      Date transactionDate = new Date();

      LOG.info("settling bids...");
      // check if winner's total balance is enough to pay for the product
      if (winnerCurrentBalanceBeforeDeposit < highestBid.getBidPrice()) {
        LOG.info(
            "Winner balance is insufficient to pay for the product {} with name {}",
            product.getId(),
            product.getName());
        cancelBids(product, allBid);
        LOG.info("Processing all refund except winner, who lose their deposit...");
        //        refundAll(product, allDeposits, transactionDate);
        refundLosers(product, allDeposits, transactionDate, winnerId);
        LOG.info("This product bid is cancelled due to above reason");
        return;
      }

      double soldPrice = settleBids(product, allBid, highestBid);
      settleWinnerDeposit(winnerDeposit);

      LOG.info("crediting/debiting to seller/winner...");
      double winnerFinalBalance = winnerCurrentBalanceBeforeDeposit - soldPrice;
      settleBilling(product, soldPrice, winner, winnerFinalBalance, highestBid, transactionDate);

      LOG.info("Refunding to losers...");
      refundLosers(product, allDeposits, transactionDate, winnerId);

      LOG.info("This product is settled successfully!");
    } catch (Exception e) {
      LOG.error("Error settling product bid: " + e.getMessage());
      throw new BidProcessingException("Error settling product bid", e);
    }
  }

  private double settleBids(Product product, List<Bid> allBid, Bid highestBid) {
    LOG.info("updating highest bid records for winner");
    allBid.forEach(
        bid -> {
          if (bid.getId() == highestBid.getId()) {
            bid.setWinner(true);
            bid.setStatus(BidStatus.SETTLED);
          } else {
            bid.setStatus(BidStatus.EXPIRED);
          }
          bidRepository.save(bid);
        });

    // product sold price is the same as bid price, or we can have sold_price column in product tbl
    // product sold date is the same as bid date, or we can have sold_date column in product tbl
    LOG.info("marking product as {}", ProductStatus.SOLD);
    product.setStatus(ProductStatus.SOLD);
    productRepository.save(product);

    return highestBid.getBidPrice();
  }

  private void cancelBids(Product product, List<Bid> allBid) {
    // canceling all the bid for this product, mark product as cancelled too!
    product.setStatus(ProductStatus.CANCELLED);
    allBid.forEach(
        bid -> {
          bid.setStatus(BidStatus.CANCELLED);
          bidRepository.save(bid);
        });
  }

  private void settleWinnerDeposit(Bid winnerDeposit) {
    LOG.info("updating winner's deposit...");
    winnerDeposit.setWinner(true);
    bidRepository.save(winnerDeposit);
  }

  private void refundLosers(
      Product product, List<Bid> allDeposits, Date transactionDate, long winnerId) {
    allDeposits.stream()
        .filter(bidDeposit -> !bidDeposit.getUser().getId().equals(winnerId)) // exclude winner
        .forEach(
            bidDeposit -> {
              User user = bidDeposit.getUser();
              final double finalBalance = user.getCurrentBalance() + bidDeposit.getDeposit();
              user.setCurrentBalance(finalBalance);
              userRepository.save(user);
              // Add new records in billing/transactions table
              // Perform billing operations for each loser here
              Billing billing =
                  Billing.builder()
                      .amount(bidDeposit.getDeposit())
                      .user(user)
                      .balance(finalBalance)
                      .type("Credit")
                      .transactionDate(transactionDate)
                      .details(
                          "Refund deposit for product "
                              + product.getId()
                              + ": "
                              + product.getName())
                      .build();
              billingRepository.save(billing);
            });
  }

  private void refundAll(Product product, List<Bid> allDeposits, Date transactionDate) {
    allDeposits.stream()
        .forEach(
            bidDeposit -> {
              User user = bidDeposit.getUser();
              double finalBalance = user.getCurrentBalance() + bidDeposit.getDeposit();
              user.setCurrentBalance(finalBalance);
              userRepository.save(user);
              // Add new records in billing/transactions table for each bidder here
              Billing billing =
                  Billing.builder()
                      .amount(bidDeposit.getDeposit())
                      .user(user)
                      .balance(finalBalance)
                      .type("Credit")
                      .transactionDate(transactionDate)
                      .details(
                          "Refund deposit for product "
                              + product.getId()
                              + ": "
                              + product.getName())
                      .build();
              billingRepository.save(billing);
            });
  }

  private void settleBilling(
      Product product,
      double soldPrice,
      User winner,
      double winnerFinalBalance,
      Bid highestBid,
      Date transactionDate) {
    Billing buyerBilling =
        Billing.builder()
            .amount(
                soldPrice
                    - highestBid
                        .getDeposit()) //  debit to buyer soldPrice - deposit in transaction tbl
            .user(winner)
            .balance(winnerFinalBalance)
            .type("Debit")
            .transactionDate(transactionDate)
            .details(
                "Payment for product "
                    + product.getId()
                    + ": "
                    + product.getName()
                    + " with bid price "
                    + soldPrice)
            .build();

    User seller = userRepository.findByEmail(product.getOwner());
    double sellerBalance = seller.getCurrentBalance();
    double finalSellerBalance = sellerBalance + soldPrice;
    Billing sellerBilling =
        Billing.builder()
            .amount(soldPrice) // credit to seller full amount
            .user(seller)
            .balance(finalSellerBalance)
            .type("Credit")
            .transactionDate(transactionDate)
            .details(
                "Receive payment for product "
                    + product.getId()
                    + ": "
                    + product.getName()
                    + " with bid price "
                    + soldPrice)
            .build();

    billingRepository.save(buyerBilling);
    billingRepository.save(sellerBilling);

    seller.setCurrentBalance(finalSellerBalance);
    winner.setCurrentBalance(winnerFinalBalance);
    userRepository.save(seller);
    userRepository.save(winner);
  }
}
