package com.nicobrest.kamehouse.commons.model.kamehousecommand;

/**
 * Represents a command executed in KameHouse. The most common commands are kamehouse-shell script
 * commands.
 */
public interface KameHouseCommand {

  /**
   * Execute the kamehouse command and return the result.
   */
  KameHouseCommandResult execute();

  /**
   * Perform initialization tasks that require the command constructor finalized before executing
   * the command.
   */
  void init();

  /**
   * Get the command to execute as a string.
   */
  String getCommand();

  /**
   * Return true to hide the executed command from the output and logs when the command arguments
   * contain sensitive information.
   */
  boolean hasSensitiveInformation();

  /**
   * Return true if the command should execute in the docker container host when kamehouse is
   * running inside a docker container with host control enabled. Some commands need to execute
   * remotely on the host while others need to run inside the container. For example starting vlc
   * player needs to happen in the remote host, while restarting httpd would happen inside the
   * container.
   */
  boolean executeOnDockerHost();
}
