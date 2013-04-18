package com.example.choiceandroid;

public class Vote {
	public String desc = null;
	public String username = null;
	public String datetime = null;
	public int id;
	
	public Vote(int id, String desc, String username, String datetime){
		this.id = id;
		this.desc = desc;
		this.username = username;
		this.datetime = datetime;
	}
}
