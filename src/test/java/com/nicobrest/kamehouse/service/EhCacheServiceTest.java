package com.nicobrest.kamehouse.service;

import static org.junit.Assert.*;

import com.nicobrest.kamehouse.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.model.DragonBallUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * Unit tests for the EhCacheService class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class EhCacheServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(EhCacheServiceTest.class);

  @Autowired
  private EhCacheService ehCacheService;

  @Autowired
  private DragonBallUserDao dragonBallUserDaoJpa;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  /**
   * Clear data from the repository before each test.
   *
   * @author nbrest
   */
  @Before
  public void setUp() {

    logger.info("***** setUp");

    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery("DELETE FROM DRAGONBALLUSER");
    query.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  @Test
  public void getAllCachesTest() {

    DragonBallUser dragonBallUser = new DragonBallUser(null, "vegeta", "vegeta@dbz.com", 49, 40,
        1000);
    try {
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
      dragonBallUserDaoJpa.getAllDragonBallUsers();
      List<Map<String, Object>> cacheList = ehCacheService.getAllCaches();
      assertEquals(4, cacheList.size());
      for (Map<String, Object> cacheMap : ehCacheService.getAllCaches()) {
        if (cacheMap.get("name").equals("getAllDragonBallUsersCache")) {
          assertEquals("[SimpleKey []]", cacheMap.get("keys"));
        }
      }
    } catch (KameHouseBadRequestException | KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  @Test
  public void clearAllCachesTest() {

    DragonBallUser dragonBallUser = new DragonBallUser(null, "vegeta", "vegeta@dbz.com", 49, 40,
        1000);
    try {
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
      dragonBallUserDaoJpa.getAllDragonBallUsers();
      ehCacheService.clearAllCaches();
      for (Map<String, Object> cacheMap : ehCacheService.getAllCaches()) {
        assertEquals("[]", cacheMap.get("keys"));
      }
    } catch (KameHouseBadRequestException | KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

}
