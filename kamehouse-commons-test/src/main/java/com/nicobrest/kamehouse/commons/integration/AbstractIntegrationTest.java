package com.nicobrest.kamehouse.commons.integration;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to group common integration tests functionality.
 *
 * @author nbrest
 */
public abstract class AbstractIntegrationTest {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private String protocol;
  private String hostname;
  private String port;

  /**
   * Get the webapp to connect to on the integration tests. Ej. "kame-house-admin".
   */
  public abstract String getWebapp();

  /**
   * Init integration tests.
   */
  protected AbstractIntegrationTest() {
    protocol = PropertiesUtils.getProperty("integration.tests.protocol", "http://");
    hostname = PropertiesUtils.getProperty("integration.tests.hostname", "localhost");
    port = PropertiesUtils.getProperty("integration.tests.port", "9980");
    logger.info("Base url for integration tests: {}", getWebappUrl());
  }

  /**
   * Set the protocol.
   */
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  /**
   * Get the base url for all requests.
   */
  public String getBaseUrl() {
    return protocol + hostname + ":" + port;
  }

  /**
   * Get the url for the specified webapp.
   */
  public String getWebappUrl() {
    return getBaseUrl() + "/" + getWebapp();
  }
}
