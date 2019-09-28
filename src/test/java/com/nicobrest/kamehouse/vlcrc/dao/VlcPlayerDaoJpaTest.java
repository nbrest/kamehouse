package com.nicobrest.kamehouse.vlcrc.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.nicobrest.kamehouse.main.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the VlcPlayerDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class VlcPlayerDaoJpaTest extends AbstractCrudDaoJpaTest<VlcPlayer, VlcPlayerDto> {

  private VlcPlayer vlcPlayer;

  @Autowired
  private VlcPlayerDao vlcPlayerDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new VlcPlayerTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    vlcPlayer = testUtils.getSingleTestData();

    clearTable("VLC_PLAYER");
  }

  /**
   * Test for creating a VlcPlayer in the repository.
   */
  @Test
  public void createTest() {
    createTest(vlcPlayerDaoJpa, VlcPlayer.class);
  }

  /**
   * Test for creating a VlcPlayer in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(vlcPlayerDaoJpa);
  }

  /**
   * Test for getting a single entity from the repository by id.
   */
  @Test
  public void readTest() {
    readTest(vlcPlayerDaoJpa);
  }

  /**
   * Test for getting all the VlcPlayers in the repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(vlcPlayerDaoJpa);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateTest() throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
    VlcPlayer updatedEntity = (VlcPlayer) BeanUtils.cloneBean(vlcPlayer);
    updatedEntity.setHostname("kamehameha-updated-hostname");
    updateTest(vlcPlayerDaoJpa, VlcPlayer.class, updatedEntity);
  }

  /**
   * Test for updating an existing entity in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(vlcPlayerDaoJpa, VlcPlayer.class);
  }

  /**
   * Test for deleting an existing entity from the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(vlcPlayerDaoJpa);
  }

  /**
   * Test for deleting an existing entity from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(vlcPlayerDaoJpa, VlcPlayer.class);
  }

  /**
   * Test for getting a single VlcPlayer in the repository by hostname.
   */
  @Test
  public void getByHostnameTest() {
    persistEntityInRepository(vlcPlayer);

    VlcPlayer returnedEntity = vlcPlayerDaoJpa.getByHostname(vlcPlayer.getHostname());

    assertNotNull(returnedEntity);
    assertEquals(vlcPlayer, returnedEntity);
    testUtils.assertEqualsAllAttributes(vlcPlayer, returnedEntity);
  }

  /**
   * Test for getting a single VlcPlayer in the repository Exception flows.
   */
  @Test
  public void getByHostnameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");
    
    vlcPlayerDaoJpa.getByHostname(VlcPlayerTestUtils.INVALID_HOSTNAME);
  }
}
