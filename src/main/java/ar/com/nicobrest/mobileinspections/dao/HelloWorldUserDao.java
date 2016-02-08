package ar.com.nicobrest.mobileinspections.dao;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import java.util.List;

public interface HelloWorldUserDao {

  public HelloWorldUser getHelloWorldUser(String username);

  public List<HelloWorldUser> getAllHelloWorldUsers();

}
