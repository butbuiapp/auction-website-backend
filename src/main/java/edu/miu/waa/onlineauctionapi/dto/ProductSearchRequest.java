package edu.miu.waa.onlineauctionapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchRequest {
  private String name;
  private int pageNumber;
  private int pageSize;
}
