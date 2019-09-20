package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory DAO for the Application Users.
 * 
 * @author nbrest
 *
 */
@Repository
public class ApplicationUserDaoInMemory implements ApplicationUserDao {

  private static Map<String, ApplicationUser> applicationUsers = new HashMap<>();

  public ApplicationUserDaoInMemory() {
    initRepository();
  }

  @Override
  public ApplicationUser loadUserByUsername(final String username) {
    ApplicationUser applicationUser = applicationUsers.get(username);
    if (applicationUser == null) {
      throw new UsernameNotFoundException("User with username " + username + " not found.");
    }
    return applicationUser;
  }

  @Override
  public Long createUser(ApplicationUser applicationUser) {
    applicationUser.setId(IdGenerator.getId());
    applicationUsers.put(applicationUser.getUsername(), applicationUser);
    return applicationUser.getId();
  }

  @Override
  public void updateUser(ApplicationUser applicationUser) {
    ApplicationUser applicationUserToUpdate = loadUserByUsername(applicationUser.getUsername());
    applicationUser.setId(applicationUserToUpdate.getId());
    applicationUsers.remove(applicationUserToUpdate.getUsername());
    applicationUsers.put(applicationUser.getUsername(), applicationUser);
  }

  @Override
  public ApplicationUser deleteUser(Long id) {
    for (Map.Entry<String, ApplicationUser> applicationUserEntry : applicationUsers.entrySet()) {
      ApplicationUser userToDelete = applicationUserEntry.getValue();
      if (userToDelete.getId().equals(id)) {
        applicationUsers.remove(applicationUserEntry.getKey());
        return userToDelete;
      }
    }
    throw new UsernameNotFoundException("User with id " + id + " not found.");
  }

  @Override
  public List<ApplicationUser> getAllUsers() {
    return new ArrayList<>(applicationUsers.values());
  }

  /**
   * Initialize in-memory repository with test users.
   */
  private static void initRepository() {
    ApplicationRole userRole = new ApplicationRole();
    userRole.setName("ROLE_USER");
    ApplicationRole adminRole = new ApplicationRole();
    adminRole.setName("ROLE_ADMIN");
    ApplicationRole guestRole = new ApplicationRole();
    guestRole.setName("ROLE_GUEST");

    ApplicationUser user = new ApplicationUser();
    user.setId(IdGenerator.getId());
    user.setFirstName("userFirst");
    user.setLastName("userLast");
    user.setEmail("user@nicobrest.com");
    user.setUsername("user");
    user.setPassword("user");
    Set<ApplicationRole> userRoles = new HashSet<>();
    userRoles.add(userRole);
    user.setAuthorities(userRoles);
    applicationUsers.put(user.getUsername(), user);

    ApplicationUser admin = new ApplicationUser();
    admin.setId(IdGenerator.getId());
    admin.setFirstName("adminFirst");
    admin.setLastName("adminLast");
    admin.setEmail("admin@nicobrest.com");
    admin.setUsername("admin");
    admin.setPassword("admin");
    Set<ApplicationRole> adminRoles = new HashSet<>();
    adminRoles.add(adminRole);
    admin.setAuthorities(adminRoles);
    applicationUsers.put(admin.getUsername(), admin);

    ApplicationUser superUser = new ApplicationUser();
    superUser.setId(IdGenerator.getId());
    superUser.setFirstName("superUserFirst");
    superUser.setLastName("superUserLast");
    superUser.setEmail("super.user@nicobrest.com");
    superUser.setUsername("super-user");
    superUser.setPassword("super-user");
    Set<ApplicationRole> superUserRoles = new HashSet<>();
    superUserRoles.add(adminRole);
    superUserRoles.add(userRole);
    superUserRoles.add(guestRole);
    superUser.setAuthorities(superUserRoles);
    applicationUsers.put(superUser.getUsername(), superUser);

    ApplicationUser guest = new ApplicationUser();
    guest.setId(IdGenerator.getId());
    guest.setFirstName("guestFirst");
    guest.setLastName("guestLast");
    guest.setEmail("guest@nicobrest.com");
    guest.setUsername("guest");
    guest.setPassword("guest");
    Set<ApplicationRole> guestRoles = new HashSet<>();
    guestRoles.add(guestRole);
    guest.setAuthorities(guestRoles);
    applicationUsers.put(guest.getUsername(), guest);
  }

  /**
   * Static inner class that generates Ids.
   */
  private static class IdGenerator {

    private static final AtomicInteger sequence = new AtomicInteger(1);

    private IdGenerator() {
    }

    /**
     * Return next number in the sequence.
     */
    public static Long getId() {
      return Long.valueOf(sequence.getAndIncrement());
    }
  }

  /**
   * Get User by id is not supported for this InMemory repository.
   */
  @Override
  public ApplicationUser getUser(Long id) {
    throw new UnsupportedOperationException(
        "This method is not supported. Use loadUserByUsername() for this repository.");
  }
}