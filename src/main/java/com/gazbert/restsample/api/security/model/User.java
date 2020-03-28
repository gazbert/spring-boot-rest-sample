package com.gazbert.restsample.api.security.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.util.Assert;

/**
 * Represents a User.
 *
 * @author gazbert
 */
@Entity
@Table(name = "USER")
public class User {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
  private Long id;

  @Column(name = "USERNAME", length = 50, unique = true)
  @NotNull
  @Size(min = 5, max = 50)
  private String username;

  @Column(name = "PASSWORD", length = 100)
  @NotNull
  @Size(min = 5, max = 100)
  private String password;

  @Column(name = "FIRSTNAME", length = 50)
  @NotNull
  @Size(min = 5, max = 50)
  private String firstname;

  @Column(name = "LASTNAME", length = 50)
  @NotNull
  @Size(min = 5, max = 50)
  private String lastname;

  @Column(name = "EMAIL", length = 100)
  @NotNull
  @Size(min = 5, max = 100)
  private String email;

  @Column(name = "ENABLED")
  @NotNull
  private Boolean enabled;

  @Column(name = "LASTPASSWORDRESETDATE")
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date lastPasswordResetDate;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "USER_ROLE",
      joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
      inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
  private List<Role> roles;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  /**
   * Returns the last password reset date.
   *
   * @return last password reset date.
   */
  public Date getLastPasswordResetDate() {
    if (lastPasswordResetDate != null) {
      return new Date(lastPasswordResetDate.getTime());
    } else {
      return null;
    }
  }

  public void setLastPasswordResetDate(Date lastPasswordResetDate) {
    Assert.notNull(lastPasswordResetDate, "lastPasswordResetDate cannot be null!");
    this.lastPasswordResetDate = new Date(lastPasswordResetDate.getTime());
  }
}
