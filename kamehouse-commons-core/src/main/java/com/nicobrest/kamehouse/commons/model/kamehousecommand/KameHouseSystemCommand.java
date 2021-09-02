package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for KameHouse System Commands in the application. KameHouse system commands are
 * translated to one or more System Commands specific to the operating system running the
 * application to be executed through the command line. These System Commands together, executed one
 * after the other, achieve the goal of the KameHouseSystemCommand.
 *
 * @author nbrest
 */
public abstract class KameHouseSystemCommand {

  @Masked
  protected List<SystemCommand> systemCommands = new ArrayList<>();

  /**
   * Gets the list of system commands required to execute to perform this admin command.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public List<SystemCommand> getSystemCommands() {
    return systemCommands;
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString(), true);
  }
}
