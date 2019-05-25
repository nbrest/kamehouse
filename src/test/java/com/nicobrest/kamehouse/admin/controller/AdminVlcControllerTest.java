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

import com.nicobrest.kamehouse.admin.model.AdminVlcCommand;
import com.nicobrest.kamehouse.admin.service.AdminVlcService;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class AdminVlcControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private AdminVlcController adminVlcController;

  @Mock
  private AdminVlcService adminVlcService;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(adminVlcService);
    mockMvc = MockMvcBuilders.standaloneSetup(adminVlcController).build();
  }

  @Test
  public void startVlcPlayerTest() {
    List<SystemCommandOutput> mockCommandOutputs = createStartVlcCommandOutputs();
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.START);
    adminVlcCommand.setFile("marvel.m3u");
    when(adminVlcService.startVlcPlayer(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(post("/api/v1/admin/vlc").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              adminVlcCommand))).andDo(print());
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
    verify(adminVlcService, times(1)).startVlcPlayer(Mockito.any());
    verifyNoMoreInteractions(adminVlcService);
  }

  @Test
  public void stopVlcPlayerTest() {
    List<SystemCommandOutput> mockCommandOutputs = createStopVlcCommandOutputs();
    when(adminVlcService.stopVlcPlayer()).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(delete("/api/v1/admin/vlc")).andDo(
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
    verify(adminVlcService, times(1)).stopVlcPlayer();
    verifyNoMoreInteractions(adminVlcService);
  }
  
  @Test
  public void stopVlcPlayerServerErrorTest() {
    List<SystemCommandOutput> mockCommandOutputs = createStopVlcCommandOutputs();
    mockCommandOutputs.get(0).setExitCode(1);
    when(adminVlcService.stopVlcPlayer()).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(delete("/api/v1/admin/vlc")).andDo(
          print());
      requestResult.andExpect(status().is5xxServerError());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminVlcService, times(1)).stopVlcPlayer();
    verifyNoMoreInteractions(adminVlcService);
  }

  @Test
  public void statusVlcPlayerTest() {
    List<SystemCommandOutput> mockCommandOutputs = createStatusVlcPlayerCommandOutputs();
    when(adminVlcService.statusVlcPlayer()).thenReturn(mockCommandOutputs);
    try {
      ResultActions requestResult = mockMvc.perform(get("/api/v1/admin/vlc")).andDo(print());
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
    verify(adminVlcService, times(1)).statusVlcPlayer();
    verifyNoMoreInteractions(adminVlcService);
  }

  private List<SystemCommandOutput> createStartVlcCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, vlc, D:\\Series\\game_of_thrones\\GameOfThrones.m3u]");
    commandOutput.setExitCode(-1);
    commandOutput.setPid(-1);
    commandOutput.setStatus("running");
    commandOutput.setStandardOutput(null);
    commandOutput.setStandardError(null);
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }

  private List<SystemCommandOutput> createStopVlcCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, taskkill, /im, vlc.exe]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(new ArrayList<String>());
    commandOutput.setStandardError(new ArrayList<String>());
    commandOutputs.add(commandOutput);
    return commandOutputs;
  }

  private List<SystemCommandOutput> createStatusVlcPlayerCommandOutputs() {
    List<SystemCommandOutput> commandOutputs = new ArrayList<SystemCommandOutput>();
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[tasklist, /FI, IMAGENAME eq vlc.exe]");
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