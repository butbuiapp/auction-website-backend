package edu.miu.waa.onlineauctionapi.config;

import static edu.miu.waa.onlineauctionapi.common.Constants.ADMIN_URLs;
import static edu.miu.waa.onlineauctionapi.common.Constants.AUTHORITY_PREFIX;
import static edu.miu.waa.onlineauctionapi.common.Constants.PRODUCTS_URLs;
import static edu.miu.waa.onlineauctionapi.common.Constants.ROLE_CLAIM;
import static edu.miu.waa.onlineauctionapi.common.Constants.SELLER_PRODUCTS_STATICS_URLs;
import static edu.miu.waa.onlineauctionapi.common.Constants.SELLER_PRODUCTS_URLs;
import static edu.miu.waa.onlineauctionapi.common.Constants.SIGNUP_ADMIN_URL;
import static edu.miu.waa.onlineauctionapi.common.Constants.SIGNUP_URL;
import static edu.miu.waa.onlineauctionapi.common.Constants.TOKEN_URL;

import edu.miu.waa.onlineauctionapi.security.RoleEnum;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Value("${app.security.jwt.keystore-location}")
  private String keyStorePath;

  @Value("${app.security.jwt.keystore-password}")
  private String keyStorePassword;

  @Value("${app.security.jwt.key-alias}")
  private String keyAlias;

  @Value("${app.security.jwt.private-key-passphrase}")
  private String privateKeyPassphrase;

  private final CorsConfigurationSource corsConfigurationSource;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(formLogin -> formLogin.disable())
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        //        .exceptionHandling(
        //            exceptionHandling ->
        //                exceptionHandling.authenticationEntryPoint(
        //                    (request, response, ex) -> {
        //                      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
        // ex.getMessage());
        //                    }))
        .authorizeHttpRequests(
            req ->
                //        req.anyRequest().permitAll())
                req.requestMatchers(new AntPathRequestMatcher(TOKEN_URL, HttpMethod.POST.name()))
                    .permitAll()
                    .requestMatchers(new AntPathRequestMatcher(SIGNUP_URL, HttpMethod.POST.name()))
                    .permitAll()
                    .requestMatchers(SELLER_PRODUCTS_STATICS_URLs)
                    .permitAll()
                    .requestMatchers(SIGNUP_ADMIN_URL)
                    .hasAuthority(RoleEnum.ADMIN.getAuthority())
                    .requestMatchers(ADMIN_URLs)
                    .hasAuthority(RoleEnum.ADMIN.getAuthority())
                    .requestMatchers(PRODUCTS_URLs)
                    .hasAnyAuthority(
                        RoleEnum.CUSTOMER.getAuthority(),
                        RoleEnum.SELLER.getAuthority()) // allow seller to see other people products
                    .requestMatchers(SELLER_PRODUCTS_URLs)
                    .hasAuthority(RoleEnum.SELLER.getAuthority())
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2ResourceServer ->
                oauth2ResourceServer
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter()))
                    .authenticationEntryPoint(authenticationEntryPoint))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }

  private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter authorityConverter = new JwtGrantedAuthoritiesConverter();
    authorityConverter.setAuthorityPrefix(AUTHORITY_PREFIX);
    authorityConverter.setAuthoritiesClaimName(ROLE_CLAIM);
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(authorityConverter);
    return converter;
  }

  @Bean
  public KeyStore keyStore() {
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      InputStream resourceAsStream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath);
      keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
      return keyStore;
    } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
      LOG.error("Unable to load keystore: {}", keyStorePath, e);
    }

    throw new IllegalArgumentException("Unable to load keystore");
  }

  @Bean
  public RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
    try {
      Key key = keyStore.getKey(keyAlias, privateKeyPassphrase.toCharArray());
      if (key instanceof RSAPrivateKey) {
        return (RSAPrivateKey) key;
      }
    } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
      LOG.error("Unable to load private key from keystore: {}", keyStorePath, e);
    }
    throw new IllegalArgumentException("Unable to load private key");
  }

  @Bean
  public RSAPublicKey jwtValidationKey(KeyStore keyStore) {
    try {
      Certificate certificate = keyStore.getCertificate(keyAlias);
      PublicKey publicKey = certificate.getPublicKey();
      if (publicKey instanceof RSAPublicKey) {
        return (RSAPublicKey) publicKey;
      }
    } catch (KeyStoreException e) {
      LOG.error("Unable to load private key from keystore: {}", keyStorePath, e);
    }
    throw new IllegalArgumentException("Unable to load RSA public key");
  }

  @Bean
  public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey) {
    return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // Supports existing password encoding, uses BCrypt for new passwords, future
    // encoders can be added here too
    //        Map<String, PasswordEncoder> encoders =
    //                Map.of(
    //                        ENCODER_ID,
    //                        new BCryptPasswordEncoder(),
    //                        "pbkdf2",
    //                        Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
    //                        "scrypt",
    //                        SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
    //        return new DelegatingPasswordEncoder(ENCODER_ID, encoders);
    return new BCryptPasswordEncoder();
  }
}
