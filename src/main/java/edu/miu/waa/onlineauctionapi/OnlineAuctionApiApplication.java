package edu.miu.waa.onlineauctionapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnlineAuctionApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(OnlineAuctionApiApplication.class, args);
  }
}
