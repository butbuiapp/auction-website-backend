package edu.miu.waa.onlineauctionapi.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Billing {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private double amount;
  private String type; // debit/credit
  private String details; // deposit/refund
  private double balance; // previous or last known balance, actual final balance in user tbl
  private Date transactionDate;

  @ManyToOne private User user;
}
