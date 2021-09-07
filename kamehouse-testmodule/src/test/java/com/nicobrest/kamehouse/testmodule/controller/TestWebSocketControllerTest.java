package com.nicobrest.kamehouse.testmodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketRequestMessage;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketResponseMessage;
import com.nicobrest.kamehouse.testmodule.service.TestWebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test class for the TestWebSocketController.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TestWebSocketControllerTest {

  @InjectMocks
  private TestWebSocketController testWebSocketController;

  @Mock(name = "testWebSocketService")
  private TestWebSocketService testWebSocketServiceMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void beforeTest() {
    MockitoAnnotations.openMocks(this);
    Mockito.reset(testWebSocketServiceMock);
  }

  /**
   * Tests getting a TestWebSocketResponseMessage.
   */
  @Test
  public void generateTestWebSocketResponseMessageTest() {
    TestWebSocketRequestMessage request = new TestWebSocketRequestMessage();
    TestWebSocketResponseMessage response = new TestWebSocketResponseMessage();
    response.setMessage("mada mada dane");
    response.setDate(DateUtils.getCurrentDate());

    when(testWebSocketServiceMock.generateTestWebSocketResponseMessage(any())).thenReturn(response);

    TestWebSocketResponseMessage returnedMessage =
        testWebSocketController.testWebSocketProcess(request);

    verify(testWebSocketServiceMock, times(1))
        .generateTestWebSocketResponseMessage(any());
    assertEquals(response.getMessage(), returnedMessage.getMessage());
  }
}
