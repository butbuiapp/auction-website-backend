package edu.miu.waa.onlineauctionapi.controller;

import edu.miu.waa.onlineauctionapi.common.Constants;
import edu.miu.waa.onlineauctionapi.model.Product;
import edu.miu.waa.onlineauctionapi.model.ProductImage;
import edu.miu.waa.onlineauctionapi.service.ProductService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Constants.SELLER_PRODUCTS_URL_PREFIX)
public class SellerProductController {

  @Autowired private ProductService productService;

  @GetMapping
  public List<Product> getSellerProducts(Authentication authentication) {
    var userEmail =
        ((org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal()).getSubject();
    return productService.getSellerProducts(userEmail);
  }

  @PostMapping
  public Product createProduct(@RequestBody Product product) {
    return productService.saveProduct(product);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(
      @PathVariable Long id, @RequestBody Product updatedProduct) {
    Optional<Product> optionalProduct = productService.findById(id);

    if (optionalProduct.isPresent()) {
      Product p = optionalProduct.get();

      if (p.getBidCount() > 0) {
        // error cannot edit product
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }

      p.setName(updatedProduct.getName());
      p.setDescription(updatedProduct.getDescription());
      p.setCategories(updatedProduct.getCategories());
      p.setBidStartPrice(updatedProduct.getBidStartPrice());
      p.setDeposit(updatedProduct.getDeposit());
      p.setBidDueDate(updatedProduct.getBidDueDate());
      p.setPaymentDueDate(updatedProduct.getPaymentDueDate());
      p.setStatus(updatedProduct.getStatus());
      p.setImages(updatedProduct.getImages());
      p.setShippingInformation(updatedProduct.getShippingInformation());
      p.setShippingInformation(updatedProduct.getShippingInformation());
      p.setConditionOfSale(updatedProduct.getConditionOfSale());
      return new ResponseEntity<>(productService.saveProduct(p), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    return productService
        .findById(id)
        .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    return productService
        .findById(id)
        .map(
            product -> {
              productService.delete(product);
              return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/images")
  public List<ProductImage> uploadImages(@RequestParam("files") MultipartFile[] files)
      throws IOException {
    List<ProductImage> images = new ArrayList<>();

    for (MultipartFile file : files) {
      // String fileName =
      // StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
      String fileName = createFilename();

      File dir = new File(FILE_PATH_ROOT);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      File upload = new File(FILE_PATH_ROOT + fileName);
      try (InputStream is = file.getInputStream()) {
        Files.copy(is, upload.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }

      ProductImage image = new ProductImage();
      image.setName(fileName);
      images.add(image);
    }

    return images;
  }

  private String createFilename() {
    return "product-" + System.currentTimeMillis();
  }

  // root path for image files
  private String FILE_PATH_ROOT = System.getProperty("user.dir") + "/images/upload/";

  @GetMapping("/statics/images/{filename}")
  public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename) {
    byte[] image = new byte[0];
    try {
      image = FileUtils.readFileToByteArray(new File(FILE_PATH_ROOT + filename));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
  }
}
