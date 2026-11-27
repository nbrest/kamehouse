package com.nicobrest.kamehouse.ztvrc.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.VlcPlayer;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvPlayerTestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the ZtvPlayerDaoJpa class.
 *
 * @author nbrest
 */
class ZtvPlayerDaoJpaTest extends AbstractCrudDaoJpaTest<ZtvPlayer, ZtvPlayerDto> {

  private ZtvPlayerDao ztvPlayerDaoJpa;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ZtvPlayerDaoJpaTest(ZtvPlayerDao ztvPlayerDaoJpa,
      EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
    this.ztvPlayerDaoJpa = ztvPlayerDaoJpa;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<ZtvPlayer> getEntityClass() {
    return (Class<ZtvPlayer>) (Class<?>) VlcPlayer.class;
  }

  @Override
  public CrudDao<ZtvPlayer> getCrudDao() {
    return ztvPlayerDaoJpa;
  }

  @Override
  public TestUtils<ZtvPlayer, ZtvPlayerDto> getTestUtils() {
    return new ZtvPlayerTestUtils(VlcPlayer::new);
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"VLC_PLAYER"};
  }

  @Override
  public void updateEntity(ZtvPlayer entity) {
    String username = RandomStringUtils.randomAlphabetic(12);
    entity.setUsername(username);
    entity.setHostname(username);
  }

  @Override
  public void updateEntityServerError(ZtvPlayer entity) {
    entity.setUsername(getInvalidString());
    entity.setHostname(getInvalidString());
  }

  /**
   * Tests getting a single ZTV Player in the repository by hostname.
   */
  @Test
  void getByHostnameTest() {
    ZtvPlayer ztvPlayer = testUtils.getSingleTestData();
    persistEntityInRepository(ztvPlayer);

    ZtvPlayer returnedEntity = ztvPlayerDaoJpa.getByHostname(ztvPlayer.getHostname());

    testUtils.assertEqualsAllAttributes(ztvPlayer, returnedEntity);
  }

  /**
   * Tests getting a single ZTV Player in the repository Exception flows.
   */
  @Test
  void getByHostnameNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          ztvPlayerDaoJpa.getByHostname(ZtvPlayerTestUtils.INVALID_HOSTNAME);
        });
  }
}
