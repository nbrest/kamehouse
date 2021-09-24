package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;

/**
 * JvncSender kamehouse-cmd command.
 *
 * @author nbrest
 */
public class JvncSenderSystemCommand extends KameHouseCmdSystemCommand {

  private String text;

  public JvncSenderSystemCommand(String text) {
    this.text = text;
    setKameHouseCmdCommands();
  }

  @Override
  protected String getKameHouseCmdArguments() {
    String host = PropertiesUtils.getHostname();
    String password = getVncServerPassword();
    return "-o jvncsender -host \"" + host + "\" -port 5900 -password \"" + password + "\" -text \""
        + text + "\"";
  }

  /**
   * Gets the vnc server password from a file.
   */
  protected String getVncServerPassword() {
    String vncServerPwdFile =
        PropertiesUtils.getUserHome() + "/" + PropertiesUtils.getProperty("vnc.server.pwd.file");
    try {
      String decryptedFile = EncryptionUtils.decryptKameHouseFileToString(vncServerPwdFile);
      if (StringUtils.isEmpty(decryptedFile)) {
        decryptedFile = FileUtils.EMPTY_FILE_CONTENT;
      }
      return decryptedFile;
    } catch (KameHouseInvalidDataException e) {
      return FileUtils.EMPTY_FILE_CONTENT;
    }
  }

  /**
   * Hide the output of jvncsender commands, as it contains passwords. Call this method in the
   * constructor of <b>EVERY</b> concrete subclass, after initializing the command lists.
   */
  @Override
  protected void setOutputCommand() {
    output.setCommand("[jvncsender (hidden as it contains passwords)]");
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
