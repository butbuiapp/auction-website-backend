package edu.miu.waa.onlineauctionapi.security;

import static edu.miu.waa.onlineauctionapi.common.Constants.NAME_CLAIM;
import static edu.miu.waa.onlineauctionapi.common.Constants.ROLE_CLAIM;
import static java.util.stream.Collectors.toList;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import edu.miu.waa.onlineauctionapi.model.User;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenManager {

  private final RSAPrivateKey privateKey;
  private final RSAPublicKey publicKey;

  public JwtTokenManager(@Lazy RSAPrivateKey privateKey, @Lazy RSAPublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  public String create(User principal, Date issueDate, Date expiryDate) {
    return JWT.create()
        .withIssuer("Online Action API")
        .withSubject(principal.getUsername())
        .withClaim(
            ROLE_CLAIM,
            principal.getAuthorities().stream()
                .map(
                    e -> {
                      return e.getAuthority();
                    })
                .collect(toList())) // ADMIN/SELLER/CUSTOMER
        .withClaim(NAME_CLAIM, principal.getName())
        .withIssuedAt(issueDate)
        .withExpiresAt(expiryDate)
        // .sign(Algorithm.HMAC512(SECRET_KEY.getBytes(StandardCharsets.UTF_8)));
        .sign(Algorithm.RSA256(publicKey, privateKey));
  }
}
