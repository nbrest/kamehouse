package com.nicobrest.kamehouse.cmd.executor;

import be.jedi.jvncsender.VncSender;
import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executor for the jvncsender operation.
 *
 * @author nbrest
 */
@Component
public class JvncSenderExecutor implements Executor {

  private final Logger logger = LoggerFactory.getLogger(JvncSenderExecutor.class);

  /**
   * Execute the operation.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String host = cmdArgumentHandler.getArgument("host");
    Integer port = Integer.valueOf(cmdArgumentHandler.getArgument("port"));
    String password = cmdArgumentHandler.getArgument("password");
    String text = cmdArgumentHandler.getArgument("text");
    logger.info("Sending text to vnc server {}:{}", host, port);
    VncSender vncSender = new VncSender(host, port, password);
    vncSender.sendText(text);
  }
}
