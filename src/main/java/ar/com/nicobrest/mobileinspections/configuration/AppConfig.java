package ar.com.nicobrest.mobileinspections.configuration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 *        Class that configures the application contexts. It is used to replace
 *        the web.xml file. Using this configuration makes it easier to unit
 *        test the controllers and web application using a testContext.xml file
 *        in the test classes
 *         
 * @since v0.02
 * @author nbrest
 */
public class AppConfig implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {

    XmlWebApplicationContext appContext = new XmlWebApplicationContext();
    appContext.setConfigLocation("classpath:applicationContext.xml");

    ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
        "dispatcher", new DispatcherServlet(appContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");

    servletContext.addListener(new ContextLoaderListener(appContext));
  }
}