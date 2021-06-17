package com.nicobrest.kamehouse.commons.dao;

import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-Memory DAO for the KameHouse Users.
 * 
 * @author nbrest
 *
 */
@Repository
public class KameHouseUserAuthenticationDaoInMemory implements KameHouseUserAuthenticationDao {

  private static Map<String, KameHouseUser> repository = new HashMap<>();

  public KameHouseUserAuthenticationDaoInMemory() {
    initRepository();
  }
 
  @Override
  public KameHouseUser loadUserByUsername(final String username) {
    KameHouseUser kameHouseUser = repository.get(username);
    if (kameHouseUser == null) {
      throw new UsernameNotFoundException("User with username " + username + " not found.");
    }
    return kameHouseUser;
  }

  /**
   * Initializes in-memory repository with test users.
   */
  private static void initRepository() {
    KameHouseRole userRole = new KameHouseRole();
    userRole.setName("ROLE_SAIYAJIN");
    KameHouseRole adminRole = new KameHouseRole();
    adminRole.setName("ROLE_KAMISAMA");
    KameHouseRole guestRole = new KameHouseRole();
    guestRole.setName("ROLE_NAMEKIAN");

    KameHouseUser user = new KameHouseUser();
    user.setId(IdGenerator.getId());
    user.setFirstName("userFirst");
    user.setLastName("userLast");
    user.setEmail("user@nicobrest.com");
    user.setUsername("user");
    user.setPassword("user");
    Set<KameHouseRole> userRoles = new HashSet<>();
    userRoles.add(userRole);
    user.setAuthorities(userRoles);
    repository.put(user.getUsername(), user);

    KameHouseUser admin = new KameHouseUser();
    admin.setId(IdGenerator.getId());
    admin.setFirstName("adminFirst");
    admin.setLastName("adminLast");
    admin.setEmail("admin@nicobrest.com");
    admin.setUsername("admin");
    admin.setPassword("admin");
    Set<KameHouseRole> adminRoles = new HashSet<>();
    adminRoles.add(adminRole);
    admin.setAuthorities(adminRoles);
    repository.put(admin.getUsername(), admin);

    KameHouseUser superUser = new KameHouseUser();
    superUser.setId(IdGenerator.getId());
    superUser.setFirstName("superUserFirst");
    superUser.setLastName("superUserLast");
    superUser.setEmail("super.user@nicobrest.com");
    superUser.setUsername("super-user");
    superUser.setPassword("super-user");
    Set<KameHouseRole> superUserRoles = new HashSet<>();
    superUserRoles.add(adminRole);
    superUserRoles.add(userRole);
    superUserRoles.add(guestRole);
    superUser.setAuthorities(superUserRoles);
    repository.put(superUser.getUsername(), superUser);

    KameHouseUser guest = new KameHouseUser();
    guest.setId(IdGenerator.getId());
    guest.setFirstName("guestFirst");
    guest.setLastName("guestLast");
    guest.setEmail("guest@nicobrest.com");
    guest.setUsername("guest");
    guest.setPassword("guest");
    Set<KameHouseRole> guestRoles = new HashSet<>();
    guestRoles.add(guestRole);
    guest.setAuthorities(guestRoles);
    repository.put(guest.getUsername(), guest);
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
}