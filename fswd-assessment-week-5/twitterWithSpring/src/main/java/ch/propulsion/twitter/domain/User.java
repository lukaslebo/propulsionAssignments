package ch.propulsion.twitter.domain;

import java.time.LocalDateTime;

public class User {
	
	/*
	 * FIELDS
	 */
	
	private Integer id;
	private String userName;
	private String email;
	private String password;
	private LocalDateTime dateCreated;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public User(String userName, String email, String password) {
		this.id = null;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.dateCreated = LocalDateTime.now();
	}
	
	public User(int id, String userName, String email, String password, LocalDateTime dateCreated) {
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.dateCreated = dateCreated;
	}
	
	/*
	 * GETTER & SETTER
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}
	
}
