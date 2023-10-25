package edu.miu.waa.onlineauctionapi.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    if (authException instanceof InvalidBearerTokenException) {
      response.setContentType("application/json;charset=UTF-8");
      response
          .getWriter()
          //          .print("{ \"status\": 0, \"message\": \" Insufficient permissions \" }");
          .print("{ \"status\": 0, \"message\": \"" + authException.getMessage() + "\" }");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    } else {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
  }
}
