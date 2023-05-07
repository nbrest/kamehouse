package com.nicobrest.kamehouse.ui.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the session status information that can be returned by the status API.
 *
 * @author nbrest
 */
public class SessionStatus {

  private String username;
  private String firstName;
  private String lastName;
  private String server;
  private String sessionId;
  private String buildVersion;
  private String buildDate;
  private List<String> roles;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public void setBuildVersion(String buildVersion) {
    this.buildVersion = buildVersion;
  }

  public String getBuildDate() {
    return buildDate;
  }

  public void setBuildDate(String buildDate) {
    this.buildDate = buildDate;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public List<String> getRoles() {
    return roles;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(username).append(firstName).append(lastName).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof SessionStatus) {
      final SessionStatus other = (SessionStatus) obj;
      return new EqualsBuilder().append(username, other.getUsername()).append(firstName, other
          .getFirstName()).append(lastName, other.getLastName()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
