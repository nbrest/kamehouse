package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.MouseButton;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.jvncsender.VncServer;
import org.springframework.stereotype.Component;

/**
 * Executor for the jvncsender operation.
 *
 * @author nbrest
 */
@Component
public class JvncSenderExecutor implements Executor {

  @Override
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String host = cmdArgumentHandler.getArgument("host");
    Integer port = Integer.parseInt(cmdArgumentHandler.getArgument("port"));
    String password = cmdArgumentHandler.getArgument("password");
    String text = cmdArgumentHandler.getArgument("text");
    String mouseClick = cmdArgumentHandler.getArgument("mouseClick");
    if (StringUtils.isEmpty(text) && StringUtils.isEmpty(mouseClick)) {
      throw new KameHouseInvalidCommandException("Both text and mouseClick are empty");
    }
    try {
      VncServer vncServer = new VncServer(host, port, password);
      if (StringUtils.isEmpty(text)) {
        String[] mouseClickParams = mouseClick.split(",");
        MouseButton mouseButton = MouseButton.valueOf(mouseClickParams[0]);
        int positionX = Integer.parseInt(mouseClickParams[1]);
        int positionY = Integer.parseInt(mouseClickParams[2]);
        int clickCount = Integer.parseInt(mouseClickParams[3]);
        vncServer.sendMouseClick(mouseButton.getJvncSenderButton(), positionX, positionY,
            clickCount);
      } else {
        vncServer.sendText(text);
      }
    } catch (Exception e) {
      throw new KameHouseException(e);
    }
  }
}
