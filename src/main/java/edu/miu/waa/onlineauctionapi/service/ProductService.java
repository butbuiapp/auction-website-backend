package edu.miu.waa.onlineauctionapi.service;

import edu.miu.waa.onlineauctionapi.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
  public Product saveProduct(Product product);

  public Page<Product> findActiveProductByStatusAndName(String name, Pageable pageable);

  public Product getProduct(long id);

  public Optional<Product> findById(long id);

  public void delete(Product product);

  public List<Product> getSellerProducts(String owner);

  public List<Product> findAllActiveProductsForSettlement();
}
