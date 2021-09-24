package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;

/**
 * KameHouse cmd command executor.
 *
 * @author nbrest
 */
public interface Executor {

  /**
   * Execute a kamehouse-cmd command.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler);
}
