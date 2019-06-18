package com.nicobrest.kamehouse.systemcommand.model;

/**
 * Command line to execute for all the system commands that are to be
 * executed through the application.
 * For security purposes, I prefer to have them listed explicit in the code
 * rather than variable.
 * 
 * @author nbrest
 *
 */
public enum CommandLine {
  
  SHUTDOWN_LINUX("/bin/bash", "-c", "sudo /sbin/shutdown -P "),
  SHUTDOWN_WINDOWS("cmd.exe", "/c", "start", "shutdown", "/s", "/t "),
  SHUTDOWN_CANCEL_LINUX("/bin/bash", "-c", "sudo /sbin/shutdown -c"),
  SHUTDOWN_CANCEL_WINDOWS("cmd.exe", "/c", "start", "shutdown", "/a"),
  SHUTDOWN_STATUS_LINUX("/bin/bash", "-c", 
      "ps aux | grep -e \"shutdown\\|COMMAND\" | grep -v grep"),
  SHUTDOWN_STATUS_WINDOWS ("tasklist", "/FI", "IMAGENAME eq shutdown.exe"),
  VLC_START_LINUX("vlc"),
  VLC_START_WINDOWS("cmd.exe", "/c", "start", "vlc"),
  VLC_STATUS_LINUX("/bin/bash", "-c", "ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep"),
  VLC_STATUS_WINDOWS("tasklist", "/FI", "IMAGENAME eq vlc.exe"),
  VLC_STOP_LINUX("skill", "-9", "vlc"),
  VLC_STOP_WINDOWS("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe")
  ;
  
  private final String[] commandLine;
  
  private CommandLine(String... commandLine) {
    this.commandLine = commandLine;
  }
  
  /**
   * Returns the command line to execute.
   */
  public String[] get() {
    return commandLine.clone();
  }
}
