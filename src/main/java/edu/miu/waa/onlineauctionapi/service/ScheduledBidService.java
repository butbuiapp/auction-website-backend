package edu.miu.waa.onlineauctionapi.service;

import edu.miu.waa.onlineauctionapi.exception.BidProcessingException;
import edu.miu.waa.onlineauctionapi.model.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledBidService {
  private static final Logger LOG = LoggerFactory.getLogger(ScheduledBidService.class);

  private final ProductService productService;
  private final BidService bidService;

  @Scheduled(
      fixedRateString = "${app.scheduling.task.interval}",
      initialDelayString = "${app.scheduling.task.initialDelay}")
  public void settleBids() {
    LOG.info("Settling bids for all active products that have exceeded the bidding time...");
    List<Product> activeProducts = productService.findAllActiveProductsForSettlement();

    if (!activeProducts.isEmpty()) {
      activeProducts.forEach(this::settleBidsForProduct);
    } else {
      LOG.info("No active products for settlement found");
    }
  }

  private void settleBidsForProduct(Product product) {
    try {
      bidService.settleProductBids(product);
    } catch (BidProcessingException e) {
      LOG.error("Error while settling bids for product {}", product.getId());
      LOG.error(e.getMessage(), e);
    }
  }
}
