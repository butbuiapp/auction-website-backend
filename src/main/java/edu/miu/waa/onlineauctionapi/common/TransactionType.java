package edu.miu.waa.onlineauctionapi.common;

public enum TransactionType {
  DEBIT("Debit"),
  CREDIT("Credit");

  private String name;

  TransactionType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
