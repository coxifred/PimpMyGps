package pimpmygps.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pimpmygps.utils.Fonctions;

public class Webserver {

	String ip;
	Integer port;
	JettyWebServer webSocketThread;
	
	
	static List<User> users=new ArrayList<User>();

	public static Set<Integer> icons=new TreeSet<Integer>();
	
	
	public void startWebSocket(Boolean http2) {
		
		generateIcons();

		Fonctions.trace("INF", "Starting JettyWebServer on ip " + ip + " listening on port " + port + " hardware", "CORE");
		webSocketThread = new JettyWebServer(ip, getPort(),http2);
	}

	private void generateIcons() {
		for ( int i=1;i<=16;i++)
		{
			icons.add(i);
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public JettyWebServer getWebSocketThread() {
		return webSocketThread;
	}

	public void setWebSocketThread(JettyWebServer webSocketThread) {
		this.webSocketThread = webSocketThread;
	}

	
	public static List<User> getUsers() {
		return users;
	}

	public static void setUsers(List<User> users) {
		Webserver.users = users;
	}
	
	public static User getUser(String username)
	{
		for (User anUser:users)
		{
			if ( username.equals(anUser.getName()))
			{
				return anUser;
			}
		}
		return null;
	}



}
