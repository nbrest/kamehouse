package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * @since v0.03
 * @author nbrest
 *      In-Memory DAO for the test endpoint helloWorld
 */
public class HelloWorldUserDaoInMemory implements HelloWorldUserDao {
 
  @Autowired
  private HelloWorldUser gohanHelloWorldUser;

  // @AutoWired + @Qualifier("gotenHelloWorldUser")
  @Resource(name = "gotenHelloWorldUser")
  private HelloWorldUser gotenHelloWorldUser;
  
  public void setGohanHelloWorldUser(HelloWorldUser gohanHelloWorldUser) {
    
    this.gohanHelloWorldUser = gohanHelloWorldUser;
  }
  
  public HelloWorldUser getGohanHelloWorldUser() {
    
    return this.gohanHelloWorldUser;
  }
  
  public void setGotenHelloWorldUser(HelloWorldUser gotenHelloWorldUser) {
    
    this.gotenHelloWorldUser = gotenHelloWorldUser;
  }
  
  public HelloWorldUser getGotenHelloWorldUser() {
    
    return this.gotenHelloWorldUser;
  }
  
  /**
   * @since v0.03
   * @author nbrest
   *      Returns a single instance of a HelloWorldUser
   */
  public HelloWorldUser getHelloWorldUser(String username) {
    
    // TODO Search through the users instead of returning a new one
    return new HelloWorldUser();
  }
  
  /**
   * @since v0.03
   * @author nbrest
   *      Returns all HelloWorldUsers in the repository
   */
  public List<HelloWorldUser> getAllHelloWorldUsers() {
    
    HelloWorldUser helloWorldUser1 = new HelloWorldUser();
    helloWorldUser1.setAge(21);
    helloWorldUser1.setEmail("goku@dbz.com");
    helloWorldUser1.setUsername("goku");
    
    HelloWorldUser helloWorldUser2 = new HelloWorldUser();
    helloWorldUser2.setAge(gotenHelloWorldUser.getAge());
    helloWorldUser2.setEmail(gotenHelloWorldUser.getEmail());
    helloWorldUser2.setUsername(gotenHelloWorldUser.getUsername());
    
    List<HelloWorldUser> helloWorldUsers = new ArrayList<HelloWorldUser>();
    helloWorldUsers.add(helloWorldUser1);
    helloWorldUsers.add(helloWorldUser2);
    helloWorldUsers.add(gohanHelloWorldUser);
    
    return helloWorldUsers;
  }
}
