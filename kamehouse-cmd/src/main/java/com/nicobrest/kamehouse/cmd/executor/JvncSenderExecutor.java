package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.jvncsender.VncSender;
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
      VncSender vncSender = new VncSender(host, port, password);
      if (StringUtils.isEmpty(text)) {
        String[] mouseClickParams = mouseClick.split(",");
        int positionX = Integer.parseInt(mouseClickParams[0]);
        int positionY = Integer.parseInt(mouseClickParams[1]);
        int clickCount = Integer.parseInt(mouseClickParams[2]);
        boolean isLeftClick = Boolean.parseBoolean(mouseClickParams[3]);
        if (isLeftClick) {
          vncSender.sendMouseLeftClick(positionX, positionY, clickCount);
        } else {
          vncSender.sendMouseRightClick(positionX, positionY, clickCount);
        }
      } else {
        vncSender.sendText(text);
      }
    } catch (Exception e) {
      throw new KameHouseException(e);
    }
  }
}
