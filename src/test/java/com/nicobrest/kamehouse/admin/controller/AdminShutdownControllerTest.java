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

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.admin.service.AdminShutdownService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class AdminShutdownControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private AdminShutdownController adminShutdownController;

  @Mock
  private AdminShutdownService adminShutdownService;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(adminShutdownService);
    mockMvc = MockMvcBuilders.standaloneSetup(adminShutdownController).build();
  }

  @Test
  public void setShutdownTest() {
    List<SystemCommandOutput> mockCommandOutputs = createSetShutdownCommandOutputs();
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.SET);
    adminShutdownCommand.setTime(5400);
    when(adminShutdownService.setShutdown(Mockito.any())).thenReturn(mockCommandOutputs);
    try {
      mockMvc.perform(post("/api/v1/admin/shutdown").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              adminShutdownCommand))).andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath(
              "$", hasSize(1))).andExpect(jsonPath("$[0].command", equalTo(mockCommandOutputs.get(
                  0).getCommand()))).andExpect(jsonPath("$[0].exitCode", equalTo(mockCommandOutputs
                      .get(0).getExitCode()))).andExpect(jsonPath("$[0].pid", equalTo(
                          mockCommandOutputs.get(0).getPid())));
      // TODO: Add validation to standardOuput and standardError
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminShutdownService, times(1)).setShutdown(Mockito.any());
    verifyNoMoreInteractions(adminShutdownService);
  }

  @Test
  public void cancelShutdownTest() {
    List<SystemCommandOutput> mockCommandOutputs = createCancelShutdownCommandOutputs();
    when(adminShutdownService.cancelShutdown()).thenReturn(mockCommandOutputs);
    try {
      mockMvc.perform(delete("/api/v1/admin/shutdown")).andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath(
              "$", hasSize(1))).andExpect(jsonPath("$[0].command", equalTo(mockCommandOutputs.get(
                  0).getCommand()))).andExpect(jsonPath("$[0].exitCode", equalTo(mockCommandOutputs
                      .get(0).getExitCode()))).andExpect(jsonPath("$[0].pid", equalTo(
                          mockCommandOutputs.get(0).getPid())));
      // TODO: Add validation to standardOuput and standardError
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminShutdownService, times(1)).cancelShutdown();
    verifyNoMoreInteractions(adminShutdownService);
  }

  @Test
  public void statusShutdownTest() {
    List<SystemCommandOutput> mockCommandOutputs = createStatusShutdownCommandOutputs();
    when(adminShutdownService.statusShutdown()).thenReturn(mockCommandOutputs);
    try {
      mockMvc.perform(get("/api/v1/admin/shutdown")).andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath(
              "$", hasSize(1))).andExpect(jsonPath("$[0].command", equalTo(mockCommandOutputs.get(
                  0).getCommand()))).andExpect(jsonPath("$[0].exitCode", equalTo(mockCommandOutputs
                      .get(0).getExitCode()))).andExpect(jsonPath("$[0].pid", equalTo(
                          mockCommandOutputs.get(0).getPid())));
      // TODO: Add validation to standardOuput and standardError
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(adminShutdownService, times(1)).statusShutdown();
    verifyNoMoreInteractions(adminShutdownService);
  }

  private List<SystemCommandOutput> createSetShutdownCommandOutputs() {
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

  private List<SystemCommandOutput> createCancelShutdownCommandOutputs() {
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
  
  private List<SystemCommandOutput> createStatusShutdownCommandOutputs() {
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
