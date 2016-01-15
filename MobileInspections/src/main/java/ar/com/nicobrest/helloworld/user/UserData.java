package ar.com.nicobrest.helloworld.user;

public class UserData {
	String username;
	String email;
	int age;

	public void setUsername(String value) {
		username = value;
	}

	public void setEmail(String value) {
		email = value;
	}

	public void setAge(int value) {
		age = value;
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