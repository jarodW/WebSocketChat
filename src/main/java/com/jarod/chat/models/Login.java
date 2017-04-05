package com.jarod.chat.models;

import java.util.Date;

public class Login {

	private String username;
	private Date time;

	public Login(String username) {
		this.username = username;
		time = new Date();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
