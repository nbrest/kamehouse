package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpa;
import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import jakarta.persistence.EntityManagerFactory;
import java.util.Iterator;
import java.util.Set;
import org.springframework.stereotype.Repository;

/**
 * JPA DAO for the KameHouseUser entities.
 *
 * @author nbrest
 */
@Repository
public class KameHouseUserDaoJpa extends AbstractCrudDaoJpa<KameHouseUser>
    implements KameHouseUserDao {

  public KameHouseUserDaoJpa(EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
  }

  @Override
  public Class<KameHouseUser> getEntityClass() {
    return KameHouseUser.class;
  }

  @Override
  protected void updateEntityValues(KameHouseUser persistedEntity, KameHouseUser entity) {
    persistedEntity.setAccountNonExpired(entity.isAccountNonExpired());
    persistedEntity.setAccountNonLocked(entity.isAccountNonLocked());
    persistedEntity.setCredentialsNonExpired(entity.isCredentialsNonExpired());
    persistedEntity.setEmail(entity.getEmail());
    persistedEntity.setEnabled(entity.isEnabled());
    persistedEntity.setFirstName(entity.getFirstName());
    persistedEntity.setLastLogin(entity.getLastLogin());
    persistedEntity.setLastName(entity.getLastName());
    persistedEntity.setPassword(entity.getPassword());
    persistedEntity.setUsername(entity.getUsername());
    Set<KameHouseRole> persistedKameHouseRoles = persistedEntity.getAuthorities();
    Set<KameHouseRole> updatedKameHouseRoles = entity.getAuthorities();
    Iterator<KameHouseRole> persistedApplicationRolesIterator = persistedKameHouseRoles.iterator();
    while (persistedApplicationRolesIterator.hasNext()) {
      KameHouseRole persistedRole = persistedApplicationRolesIterator.next();
      if (!updatedKameHouseRoles.contains(persistedRole)) {
        persistedApplicationRolesIterator.remove();
      }
    }
    persistedKameHouseRoles.addAll(updatedKameHouseRoles);
  }

  @Override
  public KameHouseUser loadUserByUsername(String username) {
    logger.trace("loadUserByUsername {}", username);
    KameHouseUser kameHouseUser = findByUsername(KameHouseUser.class, username);
    logger.trace("loadUserByUsername {} response {}", username, kameHouseUser);
    return kameHouseUser;
  }
}
