package edu.miu.waa.onlineauctionapi.service;

import edu.miu.waa.onlineauctionapi.dto.LoginRequest;
import edu.miu.waa.onlineauctionapi.dto.RegistrationRequest;
import edu.miu.waa.onlineauctionapi.dto.TokenResponse;
import edu.miu.waa.onlineauctionapi.exception.InvalidInputException;
import edu.miu.waa.onlineauctionapi.exception.RecordAlreadyExistsException;
import edu.miu.waa.onlineauctionapi.model.Role;
import edu.miu.waa.onlineauctionapi.model.User;
import edu.miu.waa.onlineauctionapi.model.UserRole;
import edu.miu.waa.onlineauctionapi.repository.RoleRepository;
import edu.miu.waa.onlineauctionapi.repository.UserRepository;
import edu.miu.waa.onlineauctionapi.security.JwtTokenManager;
import edu.miu.waa.onlineauctionapi.security.RoleEnum;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private static final String EMAIL_REGEX_PATTERN =
      "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
          + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final JwtTokenManager jwtTokenManager;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.security.jwt.expire.duration}")
  private long EXPIRE_DURATION;

  public TokenResponse login(LoginRequest req) {
    User user = userRepository.findByEmail(req.getEmail());
    if (user != null && passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      return createTokenResponse(user);
    }
    throw new InvalidInputException("Incorrect email or password :)");
  }

  public User findUser(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional
  public TokenResponse registerNormalUser(RegistrationRequest reg) {
    // check if valid email
    if (!isValidEmail(reg.getEmail())) {
      throw new InvalidInputException("The provided email is invalid!");
    }

    // check existing user & license number
    Integer count = userRepository.countUserByEmail(reg.getEmail());
    Integer count2 = userRepository.countUserByLicenseNumber(reg.getLicenseNumber());
    if (count > 0) {
      throw new RecordAlreadyExistsException("This email or license has been used");
    }
    if (count2 > 0) {
      throw new RecordAlreadyExistsException(
          "This license number has been used, we only allow one account per license number");
    }

    User user = toEntity(reg); // this can only copy basic props
    user.setCurrentBalance(10000); // give initial balance for demo

    // validate input role first, accept ROLE_SELLER, ROLE_CUSTOMER only
    if (reg.getRole() != RoleEnum.SELLER && reg.getRole() != RoleEnum.CUSTOMER) {
      throw new InvalidInputException("Invalid registration!");
    }

    Role role = roleRepository.findByRole(reg.getRole());
    user.addUserRole(new UserRole(user, role));
    User newUser = userRepository.save(user);

    // temporarily create token, have to send email with verification code first
    return this.createTokenResponse(newUser);
  }

  @Transactional
  public TokenResponse registerAdmin(RegistrationRequest reg) {
    Integer count = userRepository.countUserByEmail(reg.getEmail());
    if (count > 0) {
      throw new RecordAlreadyExistsException("This email has been used");
    }

    User user = toEntity(reg); // this can only copy basic props
    if (reg.getRole() != RoleEnum.ADMIN) {
      throw new InvalidInputException("Invalid admin registration!");
    }
    // accept ROLE_ADMIN only
    Role role = new Role(RoleEnum.ADMIN);
    roleRepository.save(role);
    user.addUserRole(new UserRole(user, role));
    User newUser = userRepository.save(user);

    return this.createTokenResponse(newUser);
  }

  private User toEntity(RegistrationRequest reg) {
    User user = new User();
    BeanUtils.copyProperties(reg, user);
    user.setPassword(passwordEncoder.encode(reg.getPassword()));
    return user;
  }

  private boolean isValidEmail(String email) {
    return Pattern.compile(EMAIL_REGEX_PATTERN).matcher(email).matches();
  }

  private TokenResponse createTokenResponse(User user) {
    final long now = System.currentTimeMillis();
    String token = jwtTokenManager.create(user, new Date(now), new Date(now + EXPIRE_DURATION));
    return TokenResponse.builder().accessToken(token).expiresIn(EXPIRE_DURATION).build();
  }
}
