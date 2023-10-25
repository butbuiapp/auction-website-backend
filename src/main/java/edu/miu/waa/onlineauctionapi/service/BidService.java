package edu.miu.waa.onlineauctionapi.service;

import edu.miu.waa.onlineauctionapi.dto.BidResponse;
import edu.miu.waa.onlineauctionapi.exception.BidProcessingException;
import edu.miu.waa.onlineauctionapi.model.Bid;
import edu.miu.waa.onlineauctionapi.model.Product;
import java.util.List;

public interface BidService {
  public Bid addBid(Bid bid);

  public int countTotalBidsByProductId(long productId);

  public boolean hasDeposit(long userId, long productId);

  public Bid getCurrentBidByProductId(long productId);

  public BidResponse makeDeposit(Bid bid);

  public void settleProductBids(Product product) throws BidProcessingException;

  public void settleProductBidsById(long productId) throws BidProcessingException;

  public List<Bid> findByUserIdOrderByProductIdAscBidDateDesc(String userId);
}
