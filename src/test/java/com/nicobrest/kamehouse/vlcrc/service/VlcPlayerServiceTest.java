package com.nicobrest.kamehouse.vlcrc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
import com.nicobrest.kamehouse.vlcrc.service.dto.VlcPlayerDto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for the VlcPlayerService class.
 *
 * @author nbrest
 */
public class VlcPlayerServiceTest {

  private static List<VlcPlayer> vlcPlayersList;

  @InjectMocks
  private VlcPlayerService vlcPlayerService;

  @Mock(name = "vlcPlayerDao")
  private VlcPlayerDao vlcPlayerDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    VlcPlayer vlcPlayer1 = new VlcPlayer();
    vlcPlayer1.setId(1000L);
    vlcPlayer1.setUsername("player1");
    vlcPlayer1.setHostname("player1.localhost");
    vlcPlayer1.setPort(8080);
    vlcPlayer1.setPassword("1");

    VlcPlayer vlcPlayer2 = new VlcPlayer();
    vlcPlayer2.setId(1001L);
    vlcPlayer2.setUsername("player2");
    vlcPlayer2.setHostname("player2.localhost");
    vlcPlayer2.setPort(8080);
    vlcPlayer2.setPassword("1");

    VlcPlayer vlcPlayer3 = new VlcPlayer();
    vlcPlayer3.setId(1002L);
    vlcPlayer3.setUsername("player3");
    vlcPlayer3.setHostname("player3.localhost");
    vlcPlayer3.setPort(8080);
    vlcPlayer3.setPassword("1");

    vlcPlayersList = new LinkedList<VlcPlayer>();
    vlcPlayersList.add(vlcPlayer1);
    vlcPlayersList.add(vlcPlayer2);
    vlcPlayersList.add(vlcPlayer3);

    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcPlayerDaoMock);
  }

  /**
   * Test for calling the service to create a VlcPlayer in the repository.
   */
  @Test
  public void createVlcPlayerTest() {

    try {
      VlcPlayer playerToAdd = new VlcPlayer();
      playerToAdd.setUsername("playerAdded");
      playerToAdd.setPassword("1");
      playerToAdd.setHostname("playerAdded.localhost");
      playerToAdd.setPort(8080);

      VlcPlayerDto vlcPlayerToAddDto = new VlcPlayerDto();
      vlcPlayerToAddDto.setUsername("playerAdded");
      vlcPlayerToAddDto.setPassword("1");
      vlcPlayerToAddDto.setHostname("playerAdded.localhost");
      vlcPlayerToAddDto.setPort(8080);
      
      Mockito.doReturn(1L).when(vlcPlayerDaoMock).createVlcPlayer(playerToAdd);

      vlcPlayerService.createVlcPlayer(vlcPlayerToAddDto);

      verify(vlcPlayerDaoMock, times(1)).createVlcPlayer(playerToAdd);
    } catch (KameHouseBadRequestException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to get a single VlcPlayer in the repository by
   * hostname.
   */
  @Test
  public void getVlcPlayerTest() {

    try {
      when(vlcPlayerDaoMock.getVlcPlayer("player1")).thenReturn(vlcPlayersList.get(0));

      VlcPlayer vlcPlayerReturned = vlcPlayerService.getVlcPlayer("player1");

      assertNotNull(vlcPlayerReturned);
      assertEquals("1000", vlcPlayerReturned.getId().toString());
      verify(vlcPlayerDaoMock, times(1)).getVlcPlayer("player1");
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to update an existing VlcPlayer in the
   * repository.
   */
  @Test
  public void updateVlcPlayerTest() {

    try {
      VlcPlayer vlcPlayerToUpdate = new VlcPlayer();
      vlcPlayerToUpdate.setHostname("player1.localhost");
      vlcPlayerToUpdate.setPort(9999);
      vlcPlayerToUpdate.setId(1000L);
      vlcPlayerToUpdate.setUsername("player1user");
      vlcPlayerToUpdate.setPassword("1pass");

      VlcPlayerDto vlcPlayerToUpdateDto = new VlcPlayerDto();
      vlcPlayerToUpdateDto.setUsername("player1user");
      vlcPlayerToUpdateDto.setPassword("1pass");
      vlcPlayerToUpdateDto.setHostname("player1.localhost");
      vlcPlayerToUpdateDto.setPort(9999);
      vlcPlayerToUpdateDto.setId(1000L);
      
      Mockito.doNothing().when(vlcPlayerDaoMock).updateVlcPlayer(vlcPlayerToUpdate);

      vlcPlayerService.updateVlcPlayer(vlcPlayerToUpdateDto);

      verify(vlcPlayerDaoMock, times(1)).updateVlcPlayer(vlcPlayerToUpdate);
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteVlcPlayerTest() {

    try {
      when(vlcPlayerDaoMock.deleteVlcPlayer(1000L)).thenReturn(vlcPlayersList.get(0));

      vlcPlayerService.deleteVlcPlayer(1000L);

      verify(vlcPlayerDaoMock, times(1)).deleteVlcPlayer(1000L);
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to get all the VlcPlayers in the repository.
   */
  @Test
  public void getAllVlcPlayersTest() {

    when(vlcPlayerDaoMock.getAllVlcPlayers()).thenReturn(vlcPlayersList);

    List<VlcPlayer> vlcPlayersReturned = vlcPlayerService.getAllVlcPlayers();

    assertEquals("player1", vlcPlayersReturned.get(0).getUsername());
    assertEquals("1000", vlcPlayersReturned.get(0).getId().toString());

    assertEquals("player2", vlcPlayersReturned.get(1).getUsername());
    assertEquals("1001", vlcPlayersReturned.get(1).getId().toString());

    assertEquals("player3", vlcPlayersReturned.get(2).getUsername());
    assertEquals("1002", vlcPlayersReturned.get(2).getId().toString());

    verify(vlcPlayerDaoMock, times(1)).getAllVlcPlayers();
  }
}
