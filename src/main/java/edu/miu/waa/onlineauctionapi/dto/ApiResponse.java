package edu.miu.waa.onlineauctionapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
  private boolean success;
  private T data;
  private String message;
}
