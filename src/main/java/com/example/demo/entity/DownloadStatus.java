package com.example.demo.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.example.demo.DownloadState;

@Entity
public class DownloadStatus {
	
	public static final Integer DEFAULT_PRIORITY = 1;
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
	
	private String type;
	private String url;
	private String user;
	//for now it is stored as plain text but for production environment it should be encrypted by symmetric key
	private String password;
	private String state;
	private Integer priority;
	
	
	
	public DownloadStatus() {
		super();
		this.state = DownloadState.STARTED.toString();
		priority = DEFAULT_PRIORITY;
	}
	public UUID getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getState() {
		return state;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "DownloadStatus [id=" + id + ", type=" + type + ", url=" + url + ", user=" + user + ", password="
				+ password + ", state=" + state + ", priority=" + priority + "]";
	}
}
