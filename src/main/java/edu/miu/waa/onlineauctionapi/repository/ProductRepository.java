package edu.miu.waa.onlineauctionapi.repository;

import edu.miu.waa.onlineauctionapi.common.ProductStatus;
import edu.miu.waa.onlineauctionapi.model.Product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Page<Product> findByStatusAndNameContainsAndBidDueDateAfterOrderByIdAsc(
      ProductStatus status, String name, LocalDateTime bidDueDate, Pageable pageable);

  @Query(
      "SELECT p, COUNT(b) FROM Product p LEFT JOIN Bid b ON p.id = b.product.id "
          + "WHERE p.owner = :owner GROUP BY p")
  List<Object[]> findProductsByOwnerWithBidCount(@Param("owner") String owner);

  List<Product> findByOwner(String owner);

  Optional<List<Product>> findByStatus(ProductStatus status);

  List<Product> findByStatusAndBidDueDateBeforeOrderByIdAsc(
      ProductStatus status, LocalDateTime bidDueDate);
}
