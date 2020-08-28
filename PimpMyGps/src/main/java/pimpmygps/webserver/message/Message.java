package pimpmygps.webserver.message;

import java.util.HashMap;
import java.util.Map;

import pimpmygps.webserver.User;

public class Message {
	User fromUser;
	String message;
	String action;
	String id;
	Map<String,Object> param=new HashMap<String,Object>();
	
	
	public User getFromUser() {
		return fromUser;
	}
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
