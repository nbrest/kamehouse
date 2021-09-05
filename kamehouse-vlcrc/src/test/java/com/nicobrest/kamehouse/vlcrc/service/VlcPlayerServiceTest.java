package com.nicobrest.kamehouse.vlcrc.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the VlcPlayerService class.
 *
 * @author nbrest
 */
public class VlcPlayerServiceTest extends AbstractCrudServiceTest<VlcPlayer, VlcPlayerDto> {

  @InjectMocks
  private VlcPlayerService vlcPlayerService;

  @Mock(name = "vlcPlayerDao")
  private VlcPlayerDao vlcPlayerDaoMock;

  @Override
  public CrudService<VlcPlayer, VlcPlayerDto> getCrudService() {
    return vlcPlayerService;
  }

  @Override
  public CrudDao<VlcPlayer> getCrudDao() {
    return vlcPlayerDaoMock;
  }

  @Override
  public TestUtils<VlcPlayer, VlcPlayerDto> getTestUtils() {
    return new VlcPlayerTestUtils();
  }

  /**
   * Tests calling the service to get a single VlcPlayer in the repository by hostname.
   */
  @Test
  public void getByHostnameTest() {
    VlcPlayer vlcPlayer = testUtils.getSingleTestData();
    when(vlcPlayerDaoMock.getByHostname(vlcPlayer.getHostname())).thenReturn(vlcPlayer);

    VlcPlayer returnedEntity = vlcPlayerService.getByHostname(vlcPlayer.getHostname());

    testUtils.assertEqualsAllAttributes(vlcPlayer, returnedEntity);
    verify(vlcPlayerDaoMock, times(1)).getByHostname(vlcPlayer.getHostname());
  }
}
