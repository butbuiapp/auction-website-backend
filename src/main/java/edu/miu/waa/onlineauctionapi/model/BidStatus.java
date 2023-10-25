package edu.miu.waa.onlineauctionapi.model;

public enum BidStatus {
  ACTIVE,
  EXPIRED, // only highest bid was settled while the order bids expired
  SETTLED, // sold with bid
  CANCELLED; // buyer with highest bid have not fully paid before due date
}
