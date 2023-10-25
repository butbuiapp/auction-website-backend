package edu.miu.waa.onlineauctionapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BidDto {
  private int totalBids;
  private double currentBid;
  private double bidStartPrice;
  private double deposit;
  private boolean productOwner;
}
