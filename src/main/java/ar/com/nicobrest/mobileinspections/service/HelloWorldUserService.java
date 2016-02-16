package ar.com.nicobrest.mobileinspections.service;

import ar.com.nicobrest.mobileinspections.dao.HelloWorldUserDao;
import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @since v0.03
 * @author nbrest
 * 
 *      Service layer for the Example endpoints
 */
public class HelloWorldUserService {

  @Autowired
  private HelloWorldUserDao helloWorldUserDao;

  /**
   * @since v0.03
   * @author nbrest
   * @param helloWorldUserDao
   * 
   *      Getters and setters
   */
  public void setHelloWorldUserDao(HelloWorldUserDao helloWorldUserDao) {
    
    this.helloWorldUserDao = helloWorldUserDao;
  }
 
  /**
   * @since v0.03
   * @author nbrest
   * @return HelloWorldUserDao
   * 
   *      Getters and setters
   */
  public HelloWorldUserDao getHelloWorldUserDao() {
    
    return this.helloWorldUserDao;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns all the HelloWorldUsers in the repository
   */
  public List<HelloWorldUser> getAllHelloWorldUsers() {
    
    return helloWorldUserDao.getAllHelloWorldUsers();
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns a single instance of a HelloWorldUser
   */
  public HelloWorldUser getHelloWorldUser(String username) {
    
    return helloWorldUserDao.getHelloWorldUser(username);
  }
}
