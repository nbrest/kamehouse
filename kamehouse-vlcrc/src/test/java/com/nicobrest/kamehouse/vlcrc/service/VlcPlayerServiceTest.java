package com.nicobrest.kamehouse.vlcrc.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the VlcPlayerService class.
 *
 * @author nbrest
 */
public class VlcPlayerServiceTest extends AbstractCrudServiceTest<VlcPlayer, VlcPlayerDto> {

  private VlcPlayer vlcPlayer;

  @InjectMocks private VlcPlayerService vlcPlayerService;

  @Mock(name = "vlcPlayerDao")
  private VlcPlayerDao vlcPlayerDaoMock;

  /** Resets mock objects and initializes test repository. */
  @BeforeEach
  public void beforeTest() {
    testUtils = new VlcPlayerTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    vlcPlayer = testUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(vlcPlayerDaoMock);
  }

  /** Tests calling the service to create a VlcPlayer in the repository. */
  @Test
  public void createTest() {
    createTest(vlcPlayerService, vlcPlayerDaoMock);
  }

  /** Reads an entity test. */
  @Test
  public void readTest() {
    readTest(vlcPlayerService, vlcPlayerDaoMock);
  }

  /** Tests calling the service to get all the VlcPlayers in the repository. */
  @Test
  public void readAllTest() {
    readAllTest(vlcPlayerService, vlcPlayerDaoMock);
  }

  /** Tests calling the service to update an existing VlcPlayer in the repository. */
  @Test
  public void updateTest() {
    updateTest(vlcPlayerService, vlcPlayerDaoMock);
  }

  /** Tests calling the service to delete an existing entity in the repository. */
  @Test
  public void deleteTest() {
    deleteTest(vlcPlayerService, vlcPlayerDaoMock);
  }

  /** Tests calling the service to get a single VlcPlayer in the repository by hostname. */
  @Test
  public void getByHostnameTest() {
    when(vlcPlayerDaoMock.getByHostname(vlcPlayer.getHostname())).thenReturn(vlcPlayer);

    VlcPlayer returnedEntity = vlcPlayerService.getByHostname(vlcPlayer.getHostname());

    testUtils.assertEqualsAllAttributes(vlcPlayer, returnedEntity);
    verify(vlcPlayerDaoMock, times(1)).getByHostname(vlcPlayer.getHostname());
  }
}
