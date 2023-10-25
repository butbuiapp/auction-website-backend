package edu.miu.waa.onlineauctionapi.model;

import edu.miu.waa.onlineauctionapi.security.RoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter // do not use @Data for entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @Enumerated(EnumType.STRING)
  private RoleEnum role;

  public Role(RoleEnum r) {
    this.role = r;
  }

  @Override
  public String toString() {
    return this.role.getAuthority();
  }

  @Override
  public int hashCode() {
    return Objects.hash(role);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Role other = (Role) obj;
    return role == other.role;
  }
}
