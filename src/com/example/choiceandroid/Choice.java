package com.example.choiceandroid;

public class Choice {
	public String url = null;
	public String description = null;
	public int id = 0;
	public int votes = 0;
	public Choice(String description, String url){
		this.description = description;
		this.url = url;
	}
	public Choice(String description, String url, int id, int votes){
		this.description = description;
		this.url = url;
		this.id = id;
		this.votes = votes;
	}
}
