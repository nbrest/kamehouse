package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import java.util.List;

/**
 * @since v0.02
 * @author nicolas.brest
 *
 *      HelloWorldUserDao interface
 */
public interface HelloWorldUserDao {

  public HelloWorldUser getHelloWorldUser(String username);

  public List<HelloWorldUser> getAllHelloWorldUsers();

}
