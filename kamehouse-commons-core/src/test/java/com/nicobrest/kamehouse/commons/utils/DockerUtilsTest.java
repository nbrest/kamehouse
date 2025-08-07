package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.KameHouseCommandStatus;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.TestDaemonCommand;
import com.nicobrest.kamehouse.commons.testutils.KameHouseCommandResultCoreTestUtils;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * DockerUtils tests.
 *
 * @author nbrest
 */
class DockerUtilsTest {

  private static final String DOCKER_HOST_AUTH = "c2VpeWE6aWtraQ==";

  private KameHouseCommandResultCoreTestUtils testUtils = new KameHouseCommandResultCoreTestUtils();
  private MockedStatic<PropertiesUtils> propertiesUtils;
  private MockedStatic<SshClientUtils> sshClientUtils;
  private MockedStatic<HttpClientUtils> httpClientUtils;

  /**
   * Tests setup.
   */
  @BeforeEach
  void before() {
    testUtils.initTestData();
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    sshClientUtils = Mockito.mockStatic(SshClientUtils.class);
    httpClientUtils = Mockito.mockStatic(HttpClientUtils.class);
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  void close() {
    propertiesUtils.close();
    sshClientUtils.close();
    httpClientUtils.close();
  }

  /**
   * Execute command on docker windows host successful test.
   */
  @Test
  void executeOnDockerWindowsHostTest() throws IOException {
    when(DockerUtils.getDockerHostAuth()).thenReturn(DOCKER_HOST_AUTH);
    when(HttpClientUtils.getInputStream(any())).thenReturn(
        getInputStream("docker-utils/groot-execute-response-success-win.json"));

    var result = DockerUtils.executeOnDockerHost(new TestDaemonCommand());

    assertEquals(KameHouseCommandStatus.COMPLETED.getStatus(), result.getStatus());
    assertEquals(-1, result.getExitCode());
    assertEquals(-1, result.getPid());
    assertEquals(17, result.getStandardOutput().size());
    assertEquals(17, result.getStandardOutputHtml().size());
    assertEquals(0, result.getStandardError().size());
    assertEquals(0, result.getStandardErrorHtml().size());
  }

  /**
   * Execute command on docker linux host successful test.
   */
  @Test
  void executeOnDockerLinuxHostTest() throws IOException {
    when(DockerUtils.getDockerHostAuth()).thenReturn(DOCKER_HOST_AUTH);
    when(HttpClientUtils.getInputStream(any())).thenReturn(
        getInputStream("docker-utils/groot-execute-response-success-lin.json"));

    var result = DockerUtils.executeOnDockerHost(new TestDaemonCommand());

    assertEquals(KameHouseCommandStatus.COMPLETED.getStatus(), result.getStatus());
    assertEquals(-1, result.getExitCode());
    assertEquals(-1, result.getPid());
    assertEquals(19, result.getStandardOutput().size());
    assertEquals(19, result.getStandardOutputHtml().size());
    assertEquals(0, result.getStandardError().size());
    assertEquals(0, result.getStandardErrorHtml().size());
  }

  /**
   * shouldExecuteOnDockerHost test.
   */
  @Test
  void shouldExecuteOnDockerHostTest() {
    when(PropertiesUtils.getBooleanProperty("IS_DOCKER_CONTAINER")).thenReturn(true);
    when(PropertiesUtils.getBooleanProperty("DOCKER_CONTROL_HOST")).thenReturn(true);

    assertTrue(DockerUtils.shouldExecuteOnDockerHost(new TestDaemonCommand()));
  }

  /**
   * isWindowsDockerHost test.
   */
  @Test
  void isWindowsDockerHostTest() {
    when(PropertiesUtils.getProperty("DOCKER_HOST_OS")).thenReturn("windows");

    assertTrue(DockerUtils.isWindowsDockerHost());
  }

  /**
   * isDockerContainer test.
   */
  @Test
  void isDockerContainerTest() {
    when(PropertiesUtils.getBooleanProperty("IS_DOCKER_CONTAINER")).thenReturn(true);

    assertTrue(DockerUtils.isDockerContainer());
  }

  /**
   * isDockerControlHostEnabled test.
   */
  @Test
  void isDockerControlHostEnabledTest() {
    when(PropertiesUtils.getBooleanProperty("DOCKER_CONTROL_HOST")).thenReturn(true);

    assertTrue(DockerUtils.isDockerControlHostEnabled());
  }

  /**
   * getDockerHostIp test.
   */
  @Test
  void getDockerHostIpTest() {
    when(PropertiesUtils.getProperty("DOCKER_HOST_IP")).thenReturn("192.168.99.99");

    assertEquals("192.168.99.99", DockerUtils.getDockerHostIp());
  }

  /**
   * getDockerHostUsername test.
   */
  @Test
  void getDockerHostUsernameTest() {
    when(PropertiesUtils.getProperty("DOCKER_HOST_USERNAME")).thenReturn("goku");

    assertEquals("goku", DockerUtils.getDockerHostUsername());
  }

  /**
   * isWindowsHostOrWindowsDockerHost test.
   */
  @Test
  void isWindowsHostOrWindowsDockerHostTest() {
    Assertions.assertDoesNotThrow(DockerUtils::isWindowsHostOrWindowsDockerHost);
  }

  /**
   * getDockerHostHostname test.
   */
  @Test
  void getDockerHostHostnameTest() {
    Assertions.assertDoesNotThrow(DockerUtils::getDockerHostHostname);
  }

  /**
   * getDockerHostUserHome test.
   */
  @Test
  void getDockerHostUserHomeTest() {
    Assertions.assertDoesNotThrow(DockerUtils::getDockerHostUserHome);
  }

  /**
   * getUserHome test.
   */
  @Test
  void getUserHomeTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          DockerUtils.getUserHome();
          DockerUtils.getUserHome(false);
        });
  }

  /**
   * Gets the input stream of the specified resource.
   */
  public static InputStream getInputStream(String resourceName) {
    ClassLoader classLoader = DockerUtilsTest.class.getClassLoader();
    return classLoader.getResourceAsStream(resourceName);
  }
}
