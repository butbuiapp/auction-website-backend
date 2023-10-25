package edu.miu.waa.onlineauctionapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BidResponse {
  private boolean success;
  private Object data;
  private String message;
  private boolean requiredDeposit = false;
}
