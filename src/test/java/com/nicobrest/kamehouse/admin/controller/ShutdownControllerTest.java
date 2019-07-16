package com.nicobrest.kamehouse.admin.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for ShutdownController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ShutdownControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private ShutdownController adminShutdownController;

  @Mock
  private AdminCommandService adminCommandService;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(adminCommandService);
    mockMvc = MockMvcBuilders.standaloneSetup(adminShutdownController).build();
  }

  /**
   * Set shutdown successful test.
   */
  @Test
  public void setShutdownTest() {
    List<SystemCommandOutput> mockCommandOutputs = mockSetShutdownCommandOutputs();
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_SET);
    adminShutdownCommand.setTime(5400);
    when(adminCommandService.execute(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(post("/api/v1/admin/shutdown").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              adminShutdownCommand))).andDo(print());
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
   * Cancel shutdown successful test.
   */
  @Test
  public void cancelShutdownTest() {
    List<SystemCommandOutput> mockCommandOutputs = mockCancelShutdownCommandOutputs();
    when(adminCommandService.execute(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(delete("/api/v1/admin/shutdown")).andDo(
          print());
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
   * Cancel shutdown server error test.
   */
  @Test
  public void cancelShutdownServerErrorTest() {
    List<SystemCommandOutput> mockCommandOutputs = mockCancelShutdownCommandOutputs();
    mockCommandOutputs.get(0).setExitCode(1);
    when(adminCommandService.execute(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(delete("/api/v1/admin/shutdown")).andDo(
          print());
      requestResult.andExpect(status().is5xxServerError());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminCommandService, times(1)).execute(Mockito.any());
    verifyNoMoreInteractions(adminCommandService);
  }

  /**
   * Shutdown status successful test.
   */
  @Test
  public void statusShutdownTest() {
    List<SystemCommandOutput> mockCommandOutputs = mockStatusShutdownCommandOutputs();
    when(adminCommandService.execute(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(get("/api/v1/admin/shutdown")).andDo(print());
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
   * Mock set shutdown command outputs.
   */
  private List<SystemCommandOutput> mockSetShutdownCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, shutdown, /s, /t , 5400]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(new ArrayList<String>());
    commandOutput.setStandardError(new ArrayList<String>());
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }

  /**
   * Mock cancel shutdown command outputs.
   */
  private List<SystemCommandOutput> mockCancelShutdownCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, shutdown, /a]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(new ArrayList<String>());
    commandOutput.setStandardError(new ArrayList<String>());
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }

  /**
   * Mock shutdown status command outputs.
   */
  private List<SystemCommandOutput> mockStatusShutdownCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[tasklist, /FI, IMAGENAME eq shutdown.exe]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(Arrays.asList(
        "INFO: No tasks are running which match the specified criteria."));
    commandOutput.setStandardError(new ArrayList<String>());
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }
}
