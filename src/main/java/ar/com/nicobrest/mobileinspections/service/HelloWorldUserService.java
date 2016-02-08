package ar.com.nicobrest.mobileinspections.service;

import ar.com.nicobrest.mobileinspections.dao.HelloWorldUserDao;
import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @since v0.03
 * @author nbrest
 *      Service layer for the Example endpoints
 */
public class HelloWorldUserService {

  @Autowired
  private HelloWorldUserDao helloWorldUserDao;

  public void setHelloWorldUserDao(HelloWorldUserDao helloWorldUserDao) {
    
    this.helloWorldUserDao = helloWorldUserDao;
  }
  
  public HelloWorldUserDao getHelloWorldUserDao() {
    
    return this.helloWorldUserDao;
  }
  
  public List<HelloWorldUser> getAllHelloWorldUsers() {
    
    return helloWorldUserDao.getAllHelloWorldUsers();
  }
  
  public HelloWorldUser getHelloWorldUser(String username) {
    
    return helloWorldUserDao.getHelloWorldUser(username);
  }
}
