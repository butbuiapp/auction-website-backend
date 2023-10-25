package edu.miu.waa.onlineauctionapi.common;

public enum ProductStatus {
  DRAFT,
  RELEASE,
  SOLD, // sold with bid
  EXPIRED, // expired without any bid
  CANCELLED; // buyer have not fully paid
}
