package ar.com.nicobrest.mobileinspections.model;

/**
 * 
 * @author nbrest
 *
 * HelloWorldUser POJO used for the test endpoints
 */
public class HelloWorldUser {
	
	String username;
	String email;
	int age;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public int getAge() {
		return age;
	}
}