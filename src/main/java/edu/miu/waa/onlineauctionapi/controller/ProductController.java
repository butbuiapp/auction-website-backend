package edu.miu.waa.onlineauctionapi.controller;

import edu.miu.waa.onlineauctionapi.common.Constants;
import edu.miu.waa.onlineauctionapi.dto.BidDto;
import edu.miu.waa.onlineauctionapi.dto.BidResponse;
import edu.miu.waa.onlineauctionapi.dto.ProductResponse;
import edu.miu.waa.onlineauctionapi.dto.ProductSearchRequest;
import edu.miu.waa.onlineauctionapi.model.Bid;
import edu.miu.waa.onlineauctionapi.model.Product;
import edu.miu.waa.onlineauctionapi.service.BidService;
import edu.miu.waa.onlineauctionapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.PRODUCTS_URL_PREFIX)
@RequiredArgsConstructor
public class ProductController {
  private final ProductService productService;
  private final BidService bidService;

  @PostMapping("/search")
  public ProductResponse searchProduct(@RequestBody ProductSearchRequest searchRequest) {
    PageRequest pageRequest =
        PageRequest.of(searchRequest.getPageNumber() - 1, searchRequest.getPageSize());

    Page<Product> list =
        productService.findActiveProductByStatusAndName(searchRequest.getName(), pageRequest);
    ProductResponse res =
        ProductResponse.builder()
            .success(true)
            .data(list.getContent())
            .totalPages(list.getTotalPages())
            .totalElements(list.getTotalElements())
            .build();
    return res;
  }

  @GetMapping("/{productId}")
  public ProductResponse getProductDetails(@PathVariable long productId) {
    Product product = productService.getProduct(productId);

    ProductResponse res = ProductResponse.builder().success(true).data(product).build();
    return res;
  }

  @GetMapping("/{productId}/bid")
  public BidResponse getCurrentBidByProductId(
      @PathVariable long productId, Authentication authentication) {
    var userEmail =
        ((org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal()).getSubject();

    Product product = productService.getProduct(productId);
    Bid currentBid = bidService.getCurrentBidByProductId(productId);

    BidDto bidDto =
        BidDto.builder()
            .totalBids(bidService.countTotalBidsByProductId(productId))
            .currentBid(currentBid == null ? 0 : currentBid.getBidPrice())
            .bidStartPrice(product.getBidStartPrice())
            .deposit(product.getDeposit())
            .productOwner(userEmail.equals(product.getOwner()))
            .build();
    BidResponse res = BidResponse.builder().success(true).data(bidDto).build();
    return res;
  }
}
