package com.jarod.chat.models;

public class Logout {
	
	private String username;

	public Logout(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
