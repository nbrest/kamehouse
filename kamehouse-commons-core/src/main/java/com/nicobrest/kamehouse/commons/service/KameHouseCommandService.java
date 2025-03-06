package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service to execute and manage kamehouse commands.
 *
 * @author nbrest
 */
@Service
public class KameHouseCommandService {

  /**
   * Executes the specified KameHouseCommand.
   */
  public KameHouseCommandResult execute(KameHouseCommand kameHouseCommand) {
    if (DockerUtils.shouldExecuteOnDockerHost(kameHouseCommand)) {
      return DockerUtils.executeOnDockerHost(kameHouseCommand);
    } else {
      return executeLocalProcess(kameHouseCommand);
    }
  }

  /**
   * Executes the specified list of kamehouse commands.
   */
  public List<KameHouseCommandResult> execute(List<KameHouseCommand> kameHouseCommands) {
    List<KameHouseCommandResult> kameHouseCommandResults = new ArrayList<>();
    for (KameHouseCommand kameHouseCommand : kameHouseCommands) {
      KameHouseCommandResult kameHouseCommandResult = execute(kameHouseCommand);
      kameHouseCommandResults.add(kameHouseCommandResult);
    }
    return kameHouseCommandResults;
  }

  /**
   * Execute the kamehouse command locally.
   */
  private KameHouseCommandResult executeLocalProcess(KameHouseCommand kameHouseCommand) {
    return kameHouseCommand.execute();
  }
}
