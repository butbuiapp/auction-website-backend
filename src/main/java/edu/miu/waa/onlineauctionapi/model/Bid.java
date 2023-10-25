package edu.miu.waa.onlineauctionapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Data;

@Entity
@Data
public class Bid {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private double deposit;
  private Date depositDate;

  private Date bidDate;
  private double bidPrice;
  private boolean winner;

  @Enumerated(EnumType.STRING)
  private BidStatus status;

  @ManyToOne private User user;

  @ManyToOne private Product product;
}
