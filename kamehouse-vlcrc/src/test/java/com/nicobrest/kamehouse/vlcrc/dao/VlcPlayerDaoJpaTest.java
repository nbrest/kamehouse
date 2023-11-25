package com.nicobrest.kamehouse.vlcrc.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the VlcPlayerDaoJpa class.
 *
 * @author nbrest
 */
class VlcPlayerDaoJpaTest extends AbstractCrudDaoJpaTest<VlcPlayer, VlcPlayerDto> {

  @Autowired
  private VlcPlayerDao vlcPlayerDaoJpa;

  @Override
  public Class<VlcPlayer> getEntityClass() {
    return VlcPlayer.class;
  }

  @Override
  public CrudDao<VlcPlayer> getCrudDao() {
    return vlcPlayerDaoJpa;
  }

  @Override
  public TestUtils<VlcPlayer, VlcPlayerDto> getTestUtils() {
    return new VlcPlayerTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"VLC_PLAYER"};
  }

  @Override
  public void updateEntity(VlcPlayer entity) {
    String username = RandomStringUtils.randomAlphabetic(12);
    entity.setUsername(username);
    entity.setHostname(username);
  }

  @Override
  public void updateEntityServerError(VlcPlayer entity) {
    entity.setUsername(getInvalidString());
    entity.setHostname(getInvalidString());
  }

  /**
   * Tests getting a single VlcPlayer in the repository by hostname.
   */
  @Test
  void getByHostnameTest() {
    VlcPlayer vlcPlayer = testUtils.getSingleTestData();
    persistEntityInRepository(vlcPlayer);

    VlcPlayer returnedEntity = vlcPlayerDaoJpa.getByHostname(vlcPlayer.getHostname());

    testUtils.assertEqualsAllAttributes(vlcPlayer, returnedEntity);
  }

  /**
   * Tests getting a single VlcPlayer in the repository Exception flows.
   */
  @Test
  void getByHostnameNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          vlcPlayerDaoJpa.getByHostname(VlcPlayerTestUtils.INVALID_HOSTNAME);
        });
  }
}
