package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @since v0.03
 * @author nbrest
 * 
 *      In-Memory DAO for the test endpoint helloWorld
 */
public class HelloWorldUserDaoInMemory implements HelloWorldUserDao {
 
  private static Map<String, HelloWorldUser> helloWorldUsers;
  
  @Autowired
  private HelloWorldUser gohanHelloWorldUser;

  // @AutoWired + @Qualifier("gotenHelloWorldUser")
  @Resource(name = "gotenHelloWorldUser")
  private HelloWorldUser gotenHelloWorldUser;
  
  /**
   * @since v0.03
   * @author nbrest
   */
  public HelloWorldUserDaoInMemory() {
    
    initRepository();
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @param gohanHelloWorldUser
   * 
   *      Getters and setters
   */
  public void setGohanHelloWorldUser(HelloWorldUser gohanHelloWorldUser) {
    
    this.gohanHelloWorldUser = gohanHelloWorldUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @return HelloWorldUser
   * 
   *      Getters and setters
   */
  public HelloWorldUser getGohanHelloWorldUser() {
    
    return this.gohanHelloWorldUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Initialize In-Memory repository
   */
  private static void initRepository() {
    
    HelloWorldUser helloWorldUser1 = new HelloWorldUser();
    helloWorldUser1.setAge(49);
    helloWorldUser1.setEmail("goku@dbz.com");
    helloWorldUser1.setUsername("goku");
    
    HelloWorldUser helloWorldUser2 = new HelloWorldUser();
    helloWorldUser2.setAge(29);
    helloWorldUser2.setEmail("gohan@dbz.com");
    helloWorldUser2.setUsername("gohan");
    
    HelloWorldUser helloWorldUser3 = new HelloWorldUser();
    helloWorldUser3.setAge(19);
    helloWorldUser3.setEmail("goten@dbz.com");
    helloWorldUser3.setUsername("goten");
    
    helloWorldUsers = new HashMap<String, HelloWorldUser>();
    helloWorldUsers.put(helloWorldUser1.getUsername(), helloWorldUser1);
    helloWorldUsers.put(helloWorldUser2.getUsername(), helloWorldUser2);
    helloWorldUsers.put(helloWorldUser3.getUsername(), helloWorldUser3);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @param gotenHelloWorldUser
   * 
   *      Getters and setters
   */
  public void setGotenHelloWorldUser(HelloWorldUser gotenHelloWorldUser) {
    
    this.gotenHelloWorldUser = gotenHelloWorldUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * @return HelloWorldUser
   * 
   *      Getters and setters
   */
  public HelloWorldUser getGotenHelloWorldUser() {
    
    return this.gotenHelloWorldUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns a single instance of a HelloWorldUser
   */
  public HelloWorldUser getHelloWorldUser(String username) {
    
    return helloWorldUsers.get(username);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Returns all the HelloWorldUsers in the repository
   */
  public List<HelloWorldUser> getAllHelloWorldUsers() {
    
    List<HelloWorldUser> usersList = new ArrayList<HelloWorldUser>(helloWorldUsers.values());
    
    return usersList;
  }
}
