package ch.propulsion.twitter.domain;

import java.time.LocalDateTime;

public class Tweet {
	
	/*
	 *  FIELDS
	 */
	
	private Integer id;
	private String msg;
	private String authorName;
	private LocalDateTime dateCreated;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public Tweet(String msg, String authorName) {
		this.id = null;
		this.msg = msg;
		this.authorName = authorName;
		this.dateCreated = LocalDateTime.now();
	}
	
	public Tweet(Integer id, String msg, String authorName, LocalDateTime dateCreated) {
		this.id = id;
		this.msg = msg;
		this.authorName = authorName;
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
	public String getMsg() {
		return msg;
	}	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getAuthorName() {
		return authorName;
	}
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}
	
}
