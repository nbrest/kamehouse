package com.nicobrest.kamehouse.ztvrc.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcFileListItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Test data and common test methods to test ZtvPlayers.
 *
 * @author nbrest
 */
public class ZtvPlayerTestUtils extends AbstractTestUtils<ZtvPlayer, ZtvPlayerDto>
    implements TestUtils<ZtvPlayer, ZtvPlayerDto> {

  public static final String API_V1_ZTVPLAYERS = "/api/v1/ztv-rc/players";
  public static final String INVALID_HOSTNAME = "yukimura";

  private final Supplier<ZtvPlayer> ztvPlayerSupplier;

  public ZtvPlayerTestUtils() {
    this(ZtvPlayerTestData::new);
  }

  public ZtvPlayerTestUtils(Supplier<ZtvPlayer> ztvPlayerSupplier) {
    this.ztvPlayerSupplier = ztvPlayerSupplier;
  }

  @SuppressWarnings("unchecked")
  public static Class<ZtvPlayer> getEntityClass() {
    return (Class<ZtvPlayer>) (Class<?>) ZtvPlayerTestData.class;
  }

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(ZtvPlayer expectedEntity, ZtvPlayer returnedEntity) {
    assertEquals(expectedEntity, returnedEntity);
    assertEquals(expectedEntity.getId(), returnedEntity.getId());
    assertEquals(expectedEntity.getUsername(), returnedEntity.getUsername());
    assertEquals(expectedEntity.getPassword(), returnedEntity.getPassword());
    assertEquals(expectedEntity.getHostname(), returnedEntity.getHostname());
    assertEquals(expectedEntity.getPort(), returnedEntity.getPort());
  }

  private void initSingleTestData() {
    singleTestData = ztvPlayerSupplier.get();
    singleTestData.setId(null);
    singleTestData.setHostname("localhost");
    singleTestData.setPort(8080);
    singleTestData.setUsername("user");
    singleTestData.setPassword("pass");
  }

  private void initTestDataDto() {
    testDataDto = new ZtvPlayerDto();
    testDataDto.setId(null);
    testDataDto.setHostname("localhost");
    testDataDto.setPort(8080);
    testDataDto.setUsername("user");
    testDataDto.setPassword("pass");
  }

  private void initTestDataList() {
    ZtvPlayer ztvPlayer2 = ztvPlayerSupplier.get();
    ztvPlayer2.setId(null);
    ztvPlayer2.setHostname("kamehouse");
    ztvPlayer2.setPort(9000);
    ztvPlayer2.setUsername("user-kame");
    ztvPlayer2.setPassword("pass-kame");

    ZtvPlayer ztvPlayer3 = ztvPlayerSupplier.get();
    ztvPlayer3.setId(null);
    ztvPlayer3.setHostname("namek");
    ztvPlayer3.setPort(9999);
    ztvPlayer3.setUsername("user-namke");
    ztvPlayer3.setPassword("pass-namke");

    testDataList = new LinkedList<ZtvPlayer>();
    testDataList.add(singleTestData);
    testDataList.add(ztvPlayer2);
    testDataList.add(ztvPlayer3);
  }

  /** Test implementation of the ZtvPlayer interface. */
  public static class ZtvPlayerTestData implements ZtvPlayer {

    private Long id;
    private String hostname;
    private int port;
    private String username;
    private String password;

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public void setId(Long id) {
      this.id = id;
    }

    @Override
    public void setHostname(String hostname) {
      this.hostname = hostname;
    }

    @Override
    public String getHostname() {
      return hostname;
    }

    @Override
    public void setPort(int port) {
      this.port = port;
    }

    @Override
    public int getPort() {
      return port;
    }

    @Override
    public void setUsername(String username) {
      this.username = username;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public void setPassword(String password) {
      this.password = password;
    }

    @Override
    public String getPassword() {
      return password;
    }

    @Override
    @JsonIgnore
    public ZtvRcStatus getZtvRcStatus() {
      return null;
    }

    @Override
    public ZtvRcStatus execute(ZtvRcCommand command) {
      return null;
    }

    @Override
    @JsonIgnore
    public List<ZtvRcPlaylistItem> getPlaylist() {
      return null;
    }

    @Override
    public List<ZtvRcFileListItem> browse(String uri) {
      return null;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ZtvPlayer other) {
        return Objects.equals(id, other.getId())
            && Objects.equals(hostname, other.getHostname())
            && port == other.getPort()
            && Objects.equals(username, other.getUsername())
            && Objects.equals(password, other.getPassword());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, hostname, port, username, password);
    }
  }
}
