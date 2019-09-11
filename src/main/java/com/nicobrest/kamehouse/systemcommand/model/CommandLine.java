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
    
  LOCK_SCREEN_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, "gnome-screensaver-command -l"),
  LOCK_SCREEN_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.START, "rundll32.exe", 
      "user32.dll,LockWorkStation"),
  
  SHUTDOWN_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, "sudo /sbin/shutdown -P "),
  SHUTDOWN_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.START, "shutdown", "/s", "/t "),
  
  SHUTDOWN_CANCEL_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, "sudo /sbin/shutdown -c"),
  SHUTDOWN_CANCEL_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.START, "shutdown", "/a"),
  
  SHUTDOWN_STATUS_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, 
      "ps aux | grep -e \"shutdown\\|COMMAND\" | grep -v grep"),
  SHUTDOWN_STATUS_WINDOWS("tasklist", "/FI", "IMAGENAME eq shutdown.exe"),
  
  SUSPEND_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, "sudo /bin/systemctl suspend -i"),
  SUSPEND_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.START, "rundll32.exe", 
      "powrprof.dll,SetSuspendState", "0,1,0"),
  
  VLC_START_LINUX(Cmd.VLC),
  VLC_START_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.START, Cmd.VLC),
  
  VLC_STATUS_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, 
      "ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep"),
  VLC_STATUS_WINDOWS("tasklist", "/FI", "IMAGENAME eq vlc.exe"),
  
  VLC_STOP_LINUX("skill", "-9", Cmd.VLC),
  VLC_STOP_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.START, "taskkill", "/im", "vlc.exe"),
  
  VNCDO_CLICK_SINGLE_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, 
      "/usr/local/bin/vncdo --server HOSTNAME --password VNC_SERVER_PASSWORD " 
    + "move HORIZONTAL_POSITION VERTICAL_POSITION click 1"),
  VNCDO_CLICK_SINGLE_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.VNCDO, Cmd.SERVER, Cmd.HOSTNAME,
      Cmd.PASSWORD, Cmd.VNC_SERVER_PWD, "move", "HORIZONTAL_POSITION", "VERTICAL_POSITION", 
      "click", "1"),
  
  VNCDO_KEY_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, 
      "/usr/local/bin/vncdo --server HOSTNAME --password VNC_SERVER_PASSWORD key"),
  VNCDO_KEY_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.VNCDO, Cmd.SERVER, Cmd.HOSTNAME, Cmd.PASSWORD,
      Cmd.VNC_SERVER_PWD,  "key"),
  
  VNCDO_TYPE_LINUX(Cmd.BIN_BASH, Cmd.C_PARAM_LX, 
      "/usr/local/bin/vncdo --server HOSTNAME --password VNC_SERVER_PASSWORD type"),
  VNCDO_TYPE_WINDOWS(Cmd.CMD_EXE, Cmd.C_PARAM_WIN, Cmd.VNCDO, Cmd.SERVER, Cmd.HOSTNAME, 
      Cmd.PASSWORD, Cmd.VNC_SERVER_PWD, "type")
  ;
  
  private final String[] commandLineValue;
  
  private CommandLine(String... commandLineValue) {
    this.commandLineValue = commandLineValue;
  }
  
  /**
   * Returns the command line to execute.
   */
  public String[] get() {
    return commandLineValue.clone();
  }
 
  /**
   * Internal class that contains string literals for repeated commands.
   */
  private static class Cmd {
    private static final String BIN_BASH = "/bin/bash";
    private static final String CMD_EXE = "cmd.exe";
    private static final String C_PARAM_LX = "-c";
    private static final String C_PARAM_WIN = "/c";
    private static final String HOSTNAME = "HOSTNAME";
    private static final String PASSWORD = "--password";
    private static final String SERVER = "--server";
    private static final String START = "start";
    private static final String VLC = "vlc";
    private static final String VNCDO = "vncdo";
    private static final String VNC_SERVER_PWD = "VNC_SERVER_PASSWORD";
  }
}
