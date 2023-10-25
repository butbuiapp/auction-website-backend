package edu.miu.waa.onlineauctionapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Entity
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NaturalId private String email;

  private String password;

  private String name;

  @Column(unique = true)
  private String licenseNumber;

  private double currentBalance;

  @JsonManagedReference
  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private List<UserRole> userRoles = new ArrayList<>();

  public boolean addUserRole(UserRole ur) {
    if (userRoles.add(ur)) {
      ur.setUser(this);
      return true;
    }
    return false;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userRoles.stream()
        .map(
            ur -> {
              return new SimpleGrantedAuthority(ur.getRole().getRole().name());
              // make sure GA has no ROLE_PREFIX because we config AUTHORITY_PREFIX as 'ROLE_'
            })
        .toList();
    // return user role name in subject, i.e. ROLE_ADMIN/ROLE_SELLER
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
