package edu.miu.waa.onlineauctionapi.service;

import edu.miu.waa.onlineauctionapi.dto.LoginRequest;
import edu.miu.waa.onlineauctionapi.dto.RegistrationRequest;
import edu.miu.waa.onlineauctionapi.dto.TokenResponse;
import edu.miu.waa.onlineauctionapi.model.User;

public interface UserService {

  public TokenResponse login(LoginRequest req);

  public TokenResponse registerNormalUser(RegistrationRequest reg);

  public TokenResponse registerAdmin(RegistrationRequest reg);

  public User findUser(String email);
}
