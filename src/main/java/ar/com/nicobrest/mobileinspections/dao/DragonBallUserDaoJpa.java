package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * JPA DAO for the DragonBallUser test entities.
 * 
 * @author nbrest
 */
public class DragonBallUserDaoJpa implements DragonBallUserDao {

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public EntityManagerFactory getEntityManagerFactory() {

    return entityManagerFactory;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {

    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Get the EntityManager.
   * 
   * @author nbrest
   */
  public EntityManager getEntityManager() {

    return entityManagerFactory.createEntityManager();
  }

  /**
   * Inserts a DragonBallUser to the repository.
   * 
   * @author nbrest
   */
  public Long createDragonBallUser(DragonBallUser dragonBallUser)
      throws DragonBallUserAlreadyExistsException {

    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    em.persist(dragonBallUser);
    em.getTransaction().commit();
    em.close();
    return dragonBallUser.getId();
  }

  /**
   * Gets a DragonBallUser from the repository.
   * 
   * @author nbrest
   */
  public DragonBallUser getDragonBallUser(String username)
      throws DragonBallUserNotFoundException {

    return null;
  }

  /**
   * Updates a DragonBallUser on the repository.
   * 
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser)
      throws DragonBallUserNotFoundException {

  }

  /**
   * Deletes a DragonBallUser from the repository.
   * 
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser deleteDragonBallUser(Long id)
      throws DragonBallUserNotFoundException {

    return null;
  }

  /**
   * Gets all the DragonBallUsers from the repository.
   * 
   * @author nbrest
   */
  public List<DragonBallUser> getAllDragonBallUsers() {

    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    List<DragonBallUser> dragonBallUsers = em.createQuery(
        "from DragonBallUser", DragonBallUser.class).getResultList();
    em.getTransaction().commit();
    em.close();
    return dragonBallUsers;
  }
}
