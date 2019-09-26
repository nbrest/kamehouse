package com.nicobrest.kamehouse.vlcrc.testutils;

import static org.junit.Assert.assertEquals;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.service.dto.VlcPlayerDto;

import java.util.LinkedList;

/**
 * Test data and common test methods to test DragonBallUsers in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class VlcPlayerTestUtils extends AbstractTestUtils<VlcPlayer, VlcPlayerDto>
    implements TestUtils<VlcPlayer, VlcPlayerDto> {

  public static final String API_V1_VLCPLAYERS = "/api/v1/vlc-rc/players/";
  public static final Long INVALID_ID = 987987L;

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(VlcPlayer expectedEntity, VlcPlayer returnedEntity) {
    assertEquals(expectedEntity.getId(), returnedEntity.getId());
    assertEquals(expectedEntity.getUsername(), returnedEntity.getUsername()); 
    assertEquals(expectedEntity.getPassword(), returnedEntity.getPassword()); 
    assertEquals(expectedEntity.getHostname(), returnedEntity.getHostname()); 
    assertEquals(expectedEntity.getPort(), returnedEntity.getPort());
  }

  private void initSingleTestData() {
    singleTestData =  new VlcPlayer();
    singleTestData.setId(null);
    singleTestData.setHostname("localhost");
    singleTestData.setPort(8080);
    singleTestData.setUsername("user");
    singleTestData.setPassword("pass");
  }

  private void initTestDataDto() {
    testDataDto = new VlcPlayerDto();
    testDataDto.setId(null);
    testDataDto.setHostname("localhost");
    testDataDto.setPort(8080);
    testDataDto.setUsername("user");
    testDataDto.setPassword("pass");
  }

  private void initTestDataList() {
    VlcPlayer vlcPlayer2 =  new VlcPlayer();
    vlcPlayer2.setId(null);
    vlcPlayer2.setHostname("kamehouse");
    vlcPlayer2.setPort(9000);
    vlcPlayer2.setUsername("user-kame");
    vlcPlayer2.setPassword("pass-kame");
    
    VlcPlayer vlcPlayer3 =  new VlcPlayer();
    vlcPlayer3.setId(null);
    vlcPlayer3.setHostname("namek");
    vlcPlayer3.setPort(9999);
    vlcPlayer3.setUsername("user-namke");
    vlcPlayer3.setPassword("pass-namke");

    testDataList = new LinkedList<VlcPlayer>();
    testDataList.add(singleTestData);
    testDataList.add(vlcPlayer2);
    testDataList.add(vlcPlayer3);
  }
}
