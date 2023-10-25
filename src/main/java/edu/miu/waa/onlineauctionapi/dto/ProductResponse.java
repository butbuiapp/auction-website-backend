package edu.miu.waa.onlineauctionapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponse {
  private boolean success;
  private Object data;
  private int totalPages;
  private long totalElements;
}
