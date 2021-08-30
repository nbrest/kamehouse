package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.model.ApplicationCache;
import com.nicobrest.kamehouse.commons.service.EhCacheService;
import com.nicobrest.kamehouse.commons.testutils.ApplicationCacheTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * Unit tests for the EhCacheController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class EhCacheControllerTest extends AbstractControllerTest<ApplicationCache, Object> {

  private ApplicationCache applicationCache;
  private List<ApplicationCache> applicationCacheList;

  @InjectMocks
  private EhCacheController ehCacheController;

  @Mock(name = "ehCacheService")
  private EhCacheService ehCacheServiceMock;

  /**
   * Resets mock objects.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new ApplicationCacheTestUtils();
    testUtils.initTestData();
    applicationCache = testUtils.getSingleTestData();
    applicationCacheList = testUtils.getTestDataList();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(ehCacheServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(ehCacheController).build();
  }

  /**
   * Tests getting all caches.
   */
  @Test
  public void readAllTest() throws Exception {
    when(ehCacheServiceMock.getAll()).thenReturn(applicationCacheList);

    MockHttpServletResponse response = doGet("/api/v1/commons/ehcache");
    List<ApplicationCache> responseBody = getResponseBodyList(response, ApplicationCache.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(applicationCacheList, responseBody);
    verify(ehCacheServiceMock, times(1)).getAll();
    verifyNoMoreInteractions(ehCacheServiceMock);
  }

  /**
   * Tests getting a single cache.
   */
  @Test
  public void readSingleCacheTest() throws Exception {
    when(ehCacheServiceMock.get("dragonBallUsers")).thenReturn(applicationCache);

    MockHttpServletResponse response = doGet("/api/v1/commons/ehcache?name=dragonBallUsers");
    List<ApplicationCache> responseBody = getResponseBodyList(response, ApplicationCache.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(1, responseBody.size());
    testUtils.assertEqualsAllAttributes(applicationCache, responseBody.get(0));
    verify(ehCacheServiceMock, times(1)).get("dragonBallUsers");
    verifyNoMoreInteractions(ehCacheServiceMock);
  }

  /**
   * Tests clearing all caches.
   */
  @Test
  public void clearAllTest() throws Exception {
    MockHttpServletResponse response = doDelete("/api/v1/commons/ehcache");

    verifyResponseStatus(response, HttpStatus.OK);
    verify(ehCacheServiceMock, times(1)).clearAll();
    verifyNoMoreInteractions(ehCacheServiceMock);
  }

  /**
   * Tests clearing a single cache.
   */
  @Test
  public void clearSingleCacheTest() throws Exception {
    MockHttpServletResponse response = doDelete("/api/v1/commons/ehcache?name=dragonBallUsers");

    verifyResponseStatus(response, HttpStatus.OK);
    verify(ehCacheServiceMock, times(1)).clear("dragonBallUsers");
    verifyNoMoreInteractions(ehCacheServiceMock);
  }
}
