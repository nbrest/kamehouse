package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

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

    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    Query query = em.createQuery("SELECT dbu from DragonBallUser dbu where dbu.username=:pUsername");
    query.setParameter("pUsername", username);
    DragonBallUser dragonBallUser = (DragonBallUser) query.getSingleResult();
    return dragonBallUser;
  }

  /**
   * Updates a DragonBallUser on the repository.
   * 
   * @author nbrest
   */
  public void updateDragonBallUser(DragonBallUser dragonBallUser)
      throws DragonBallUserNotFoundException {

    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    DragonBallUser updatedDbUser = em.find(DragonBallUser.class, dragonBallUser.getId());
    if (updatedDbUser != null) {
      updatedDbUser.setAge(dragonBallUser.getAge());
      updatedDbUser.setEmail(dragonBallUser.getEmail());
      updatedDbUser.setPowerLevel(dragonBallUser.getPowerLevel());
      updatedDbUser.setStamina(dragonBallUser.getStamina());
      updatedDbUser.setUsername(dragonBallUser.getUsername());
    } 
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Deletes a DragonBallUser from the repository.
   * 
   * @author nbrest
   * @return DragonBallUser
   */
  public DragonBallUser deleteDragonBallUser(Long id)
      throws DragonBallUserNotFoundException {
    
    EntityManager em = getEntityManager();
    em.getTransaction().begin();
    // find(): returns the entity from the EntityManager if its already in memory. Otherwise it goes
    // to the database to find it.
    // getReference(): Returns a proxy to the real entity. Useful if you need to access the primary
    // key used to look up the entity but not the other data of the object.
    DragonBallUser dbUserToRemove = em.find(DragonBallUser.class, id);
    if (dbUserToRemove != null) {
      em.remove(dbUserToRemove);
    } 
    em.getTransaction().commit();
    em.close();
    return dbUserToRemove;
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
