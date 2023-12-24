package com.nicobrest.kamehouse.groot.integration;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.io.IOException;

/**
 * Abstract class for GRoot integration tests.
 *
 * @author nbrest
 */
public abstract class AbstractGrootIntegrationTest extends AbstractControllerIntegrationTest {

  protected static final String RESPONSE_DOESNT_CONTAIN = "Response doesn't contain: ";

  private static final String LOGIN_URL = "/kame-house-groot/api/v1/auth/login.php";

  /**
   * Init integration tests.
   */
  public AbstractGrootIntegrationTest() {
    super(false);
    setPort(PropertiesUtils.getProperty("groot.integration.tests.port", "80"));
    logger.info("Base url for httpd integration tests: {}", getWebappUrl());
    try {
      login();
    } catch (IOException e) {
      logger.info("Error logging in to {}", getLoginUrl());
      throw new KameHouseException("Error logging in to " + getLoginUrl());
    }
  }

  @Override
  public String getWebapp() {
    return "kame-house-groot";
  }

  @Override
  protected String getLoginUrl() {
    return getBaseUrl() + LOGIN_URL;
  }
}
