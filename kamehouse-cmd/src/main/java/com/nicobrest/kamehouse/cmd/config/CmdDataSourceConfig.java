package com.nicobrest.kamehouse.cmd.config;

import com.nicobrest.kamehouse.commons.config.DataSourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Enable datasource config for kamehouse-cmd.
 *
 * @author nbrest
 */
@Configuration
public class CmdDataSourceConfig extends DataSourceConfig {

  public CmdDataSourceConfig(Environment env) {
    super(env);
  }
}
