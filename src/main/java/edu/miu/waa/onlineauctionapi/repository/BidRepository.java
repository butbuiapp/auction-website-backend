package edu.miu.waa.onlineauctionapi.repository;

import edu.miu.waa.onlineauctionapi.model.Bid;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
  public int countByProductIdAndBidPriceGreaterThan(long id, double price);

  boolean existsByUserIdAndProductIdAndDepositGreaterThan(
      long userId, long productId, double deposit);

  Bid findTop1ByProductIdOrderByBidPriceDesc(long productId);

  Bid findTop1ByProductIdAndUserId(long productId, long userId);

  List<Bid> findAllByProductId(long productId);

  @Query("SELECT COUNT(b) FROM Bid b WHERE b.product.id = ?1")
  long countBidsByProductId(Long productId);

  List<Bid> findByUserEmailOrderByProductIdAscBidDateDesc(String userId);

  List<Bid> findByProductIdAndDepositGreaterThan(long productId, double deposit);
}
