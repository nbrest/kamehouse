package com.nicobrest.kamehouse.ztvrc.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.dao.ZtvPlayerDao;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvPlayerTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the ZtvPlayerService class.
 *
 * @author nbrest
 */
class ZtvPlayerServiceTest extends AbstractCrudServiceTest<ZtvPlayer, ZtvPlayerDto> {

  @InjectMocks
  private ZtvPlayerService ztvPlayerService;

  @Mock(name = "ztvPlayerDao")
  private ZtvPlayerDao ztvPlayerDaoMock;

  @Override
  public CrudService<ZtvPlayer, ZtvPlayerDto> getCrudService() {
    return ztvPlayerService;
  }

  @Override
  public CrudDao<ZtvPlayer> getCrudDao() {
    return ztvPlayerDaoMock;
  }

  @Override
  public TestUtils<ZtvPlayer, ZtvPlayerDto> getTestUtils() {
    return new ZtvPlayerTestUtils();
  }

  /**
   * Tests calling the service to get a single ZTV Player in the repository by hostname.
   */
  @Test
  void getByHostnameTest() {
    ZtvPlayer ztvPlayer = testUtils.getSingleTestData();
    when(ztvPlayerDaoMock.getByHostname(ztvPlayer.getHostname())).thenReturn(ztvPlayer);

    ZtvPlayer returnedEntity = ztvPlayerService.getByHostname(ztvPlayer.getHostname());

    testUtils.assertEqualsAllAttributes(ztvPlayer, returnedEntity);
    verify(ztvPlayerDaoMock, times(1)).getByHostname(ztvPlayer.getHostname());
  }
}
