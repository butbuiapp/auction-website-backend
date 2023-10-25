package edu.miu.waa.onlineauctionapi.controller;

import static edu.miu.waa.onlineauctionapi.common.Constants.SIGNUP_ADMIN_URL;
import static edu.miu.waa.onlineauctionapi.common.Constants.SIGNUP_URL;
import static edu.miu.waa.onlineauctionapi.common.Constants.TOKEN_URL;

import edu.miu.waa.onlineauctionapi.dto.LoginRequest;
import edu.miu.waa.onlineauctionapi.dto.RegistrationRequest;
import edu.miu.waa.onlineauctionapi.dto.TokenResponse;
import edu.miu.waa.onlineauctionapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping(TOKEN_URL)
  public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
    return ResponseEntity.ok(userService.login(req));
  }

  @PostMapping(SIGNUP_URL)
  public ResponseEntity<TokenResponse> register(@RequestBody RegistrationRequest reg) {
    return ResponseEntity.ok(userService.registerNormalUser(reg));
  }

  @PostMapping(SIGNUP_ADMIN_URL)
  public ResponseEntity<TokenResponse> registerAdmin(@RequestBody RegistrationRequest reg) {
    return ResponseEntity.ok(userService.registerAdmin(reg));
  }
}
