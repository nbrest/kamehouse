package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service to execute and manage system commands.
 *
 * @author nbrest
 */
@Service
public class SystemCommandService {

  /**
   * Executes an KameHouseSystemCommand. Translates it to system commands and executes them.
   */
  public List<SystemCommand.Output> execute(KameHouseSystemCommand kameHouseSystemCommand) {
    return execute(kameHouseSystemCommand.getSystemCommands());
  }

  /**
   * Executes the specified SystemCommand.
   */
  public SystemCommand.Output execute(SystemCommand systemCommand) {
    if (DockerUtils.shouldExecuteOnDockerHost(systemCommand)) {
      return DockerUtils.executeOnDockerHost(systemCommand);
    } else {
      return executeLocalProcess(systemCommand);
    }
  }

  /**
   * Executes the specified list of system commands.
   */
  public List<SystemCommand.Output> execute(List<SystemCommand> systemCommands) {
    List<SystemCommand.Output> systemCommandOutputs = new ArrayList<>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommand.Output systemCommandOutput = execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Execute the system command locally.
   */
  private SystemCommand.Output executeLocalProcess(SystemCommand systemCommand) {
    return systemCommand.execute();
  }
}
