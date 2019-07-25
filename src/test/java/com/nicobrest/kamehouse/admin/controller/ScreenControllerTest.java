package com.nicobrest.kamehouse.admin.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.testutils.JsonUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the ScreenController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ScreenControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private ScreenController screenController;

  @Mock
  private AdminCommandService adminCommandService;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(adminCommandService);
    mockMvc = MockMvcBuilders.standaloneSetup(screenController).build();
  }

  /**
   * Lock screen successful test.
   */
  @Test
  public void lockScreenSuccessfulTest() {
    List<SystemCommandOutput> mockCommandOutputs = mockLockScreenCommandOutputs();
    AdminCommand adminCommand = new AdminCommand();
    adminCommand.setCommand(AdminCommand.SCREEN_LOCK);
    when(adminCommandService.execute(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(post("/api/v1/admin/screen/lock").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(adminCommand)))
          .andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
      requestResult.andExpect(jsonPath("$", hasSize(1)));
      requestResult.andExpect(jsonPath("$[0].command", equalTo(mockCommandOutputs.get(0)
          .getCommand())));
      requestResult.andExpect(jsonPath("$[0].exitCode", equalTo(mockCommandOutputs.get(0)
          .getExitCode())));
      requestResult.andExpect(jsonPath("$[0].pid", equalTo(mockCommandOutputs.get(0).getPid())));
      requestResult.andExpect(jsonPath("$[0].standardOutput", equalTo(mockCommandOutputs.get(0)
          .getStandardOutput())));
      requestResult.andExpect(jsonPath("$[0].standardError", equalTo(mockCommandOutputs.get(0)
          .getStandardError())));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminCommandService, times(1)).execute(Mockito.any());
    verifyNoMoreInteractions(adminCommandService);
  }

  /**
   * Lock screen successful test.
   */
  @Test
  public void unlockScreenSuccessfulTest() {
    List<SystemCommandOutput> mockCommandOutputs = mockUnlockScreenCommandOutputs();
    AdminCommand adminCommand = new AdminCommand();
    adminCommand.setCommand(AdminCommand.SCREEN_UNLOCK);
    when(adminCommandService.execute(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(post("/api/v1/admin/screen/unlock")
          .contentType(MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              adminCommand))).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
      requestResult.andExpect(jsonPath("$", hasSize(1)));
      requestResult.andExpect(jsonPath("$[0].command", equalTo(mockCommandOutputs.get(0)
          .getCommand())));
      requestResult.andExpect(jsonPath("$[0].exitCode", equalTo(mockCommandOutputs.get(0)
          .getExitCode())));
      requestResult.andExpect(jsonPath("$[0].pid", equalTo(mockCommandOutputs.get(0).getPid())));
      requestResult.andExpect(jsonPath("$[0].standardOutput", equalTo(mockCommandOutputs.get(0)
          .getStandardOutput())));
      requestResult.andExpect(jsonPath("$[0].standardError", equalTo(mockCommandOutputs.get(0)
          .getStandardError())));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminCommandService, times(1)).execute(Mockito.any());
    verifyNoMoreInteractions(adminCommandService);
  }

  /**
   * Mock Lock Screen command outputs.
   */
  private List<SystemCommandOutput> mockLockScreenCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, rundll32.exe, user32.dll,LockWorkStation]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(null);
    commandOutput.setStandardError(null);
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }

  /**
   * Mock Unlock Screen command outputs.
   */
  private List<SystemCommandOutput> mockUnlockScreenCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[vncdo (hidden from logs as it contains passwords)]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(null);
    commandOutput.setStandardError(null);
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }
}