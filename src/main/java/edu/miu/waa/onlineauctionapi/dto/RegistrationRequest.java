package edu.miu.waa.onlineauctionapi.dto;

import edu.miu.waa.onlineauctionapi.security.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

  private String email;
  private String name;
  private String licenseNumber;
  private String password;
  private RoleEnum role;
}
