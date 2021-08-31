package com.nicobrest.kamehouse.commons.config;

import com.nicobrest.kamehouse.commons.web.filter.logger.CustomRequestLoggingFilter;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Configure the web application for kamehouse. This class replaces most of the configuration done
 * in web.xml on each module.
 */
public class KameHouseWebAppInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) {

    // Load the application context from xml
    XmlWebApplicationContext context = new XmlWebApplicationContext();
    context.setConfigLocation("classpath:applicationContext.xml");
    servletContext.addListener(new ContextLoaderListener(context));

    // Set dispatcher servlet
    ServletRegistration.Dynamic dispatcher =
        servletContext.addServlet("dispatcher", new DispatcherServlet());
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");

    // Add filters
    context.refresh();
    CustomRequestLoggingFilter customRequestLoggingFilter =
        (CustomRequestLoggingFilter) context.getBean("customRequestLoggingFilter");

    servletContext
        .addFilter("customRequestLoggingFilter", customRequestLoggingFilter)
        .addMappingForServletNames(null, true, "dispatcher");

    servletContext
        .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
        .addMappingForServletNames(null, true, "dispatcher");
  }
}
