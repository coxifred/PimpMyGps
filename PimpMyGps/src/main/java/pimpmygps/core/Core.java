package pimpmygps.core;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import com.pimpmygps.plugin.PluginAgent;

import pimpmygps.beans.Gps;
import pimpmygps.beans.Log;
import pimpmygps.beans.User;
import pimpmygps.plugins.Plugin;

import pimpmygps.threads.ThreadMemory;
import pimpmygps.threads.ThreadSaver;
import pimpmygps.threads.ThreadSocketDeclarator;
import pimpmygps.utils.Fonctions;
import pimpmygps.webserver.Webserver;
import pimpmygps.webserver.message.SystemMessage;
import pimpmygps.webserver.websocket.SkittleSocket;

public class Core {

	/**
	 * Singleton code
	 */
	static Core instance;

	/**
	 * webserver port
	 */
	Integer webServerPort = 8080;

	/**
	 * Ip for binding server
	 */
	String webServerIp;

	/**
	 * Debug mode
	 */
	Boolean debug = false;
	
	/**
	 * Debug mode for Jetty
	 */
	Boolean debugJetty = false;

	/**
	 * Webserver
	 */
	transient Webserver ws;

	/**
	 * Admin password
	 */
	String adminPassword;

	/**
	 * uuid generated in case of webserver
	 */
	UUID uuid;

	Integer maxLogEntries = 1000;

	/*
	 * for https,http2 and alpn)
	 */
	Boolean http2 = true;

	/**
	 * List des users
	 * 
	 * @throws Exception
	 */
	Map<String, User> users = new HashMap<String, User>();

	/**
	 * Logs
	 */
	List<Log> logs = new ArrayList<Log>();

	/**
	 * OrphansId
	 */
	Map<String, Gps> orphans = new HashMap<String, Gps>();

	/**
	 * Datapath
	 */
	String dataPath = "";

	/**
	 * Plugin list
	 */
	List<Plugin> availablePlugins = new ArrayList<Plugin>();

	public void launch() throws Exception {

		Fonctions.trace("INF", "Starting main core", "CORE");
		// Chargement des users
		setUsers(User.loadUsers());
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		// Creating default Directory (plugins)
		Fonctions.trace("INF", "Checking/Creating plugins directory in " + getDataPath() + "/plugins", "CORE");
		new File(getDataPath() + "/plugins").mkdirs();
		
		// Loading plugins
		loadPlugin();

		/**
		 * Starting webserver
		 */
		ws = new Webserver();

		Fonctions.trace("INF", "Starting webserver on port " + getWebServerPort(), "CORE");
		ws = new Webserver();
		if (getWebServerIp() == null) {
			ws.setIp(InetAddress.getLocalHost().getHostAddress());
		} else {
			ws.setIp(getWebServerIp());
		}
		ws.setPort(getWebServerPort());
		ws.startWebSocket(http2);
		Fonctions.traceBanner();
		
		Fonctions.trace("INF", "", "CORE");
		Fonctions.trace("INF", "", "CORE");
		Fonctions.trace("INF",
				"Please open this url in you favorite browser https://" + ws.getIp() + ":" + ws.getPort(), "CORE");
		Fonctions.trace("INF", "", "CORE");
		Fonctions.trace("INF", "", "CORE");

		// Starting ThreadSocketDeclarator
		ThreadSocketDeclarator tsd = new ThreadSocketDeclarator();
		tsd.start();

		// Starting ThreadMemory
		ThreadMemory tm = new ThreadMemory();
		tm.start();

		// Starting ThreadSaver (save data)
		ThreadSaver ts = new ThreadSaver();
		ts.start();

		while (ws.getWebSocketThread().isAlive()) {
			Fonctions.attendre(5000);
			if (!tsd.isAlive()) {
				tsd = new ThreadSocketDeclarator();
				tsd.start();
			}
			if (!ts.isAlive()) {
				ts = new ThreadSaver();
				ts.start();
			}
		}

		Fonctions.trace("INF", "Ending main core", "CORE");
		System.exit(0);
	}

	private void loadPlugin() throws IOException {
		availablePlugins.clear();
		for ( File aPlugin : new File("./plugins").listFiles())
		{
			if ( aPlugin.getName().endsWith(".jar") && ! aPlugin.getName().equals("pluginAgent.jar"))
			{
			Fonctions.trace("INF", " --> Loading plugin file " + aPlugin.getName(), "CORE");
			// Trying to instanciate a plugin (empty)
			try {
				PluginAgent.addToClassPath(aPlugin);
				Class<?> aClass = Class.forName("com.pimpmygps.plugins." + aPlugin.getName().replace(".jar", ""));
				Plugin loadedPlugin=(Plugin) aClass.getDeclaredConstructor().newInstance();
				loadedPlugin.setName(aPlugin.getName().replace(".jar", ""));
				availablePlugins.add(loadedPlugin);
			} catch (Exception e) {
				Fonctions.trace("ERR", " --> Problem while loading plugin " + aPlugin.getName() + " " + e.getMessage(), "CORE");
				e.printStackTrace();
			}
			
			}
		}
	}

	private void sendStatistics(ExecutorService executor) {

		SkittleSocket.broadcastMessage(new SystemMessage());

	}

	public static Core getInstance() {
		if (instance == null) {
			instance = new Core();
		}
		return instance;
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public static void setInstance(Core instance) {
		Core.instance = instance;
	}

	public Integer getWebServerPort() {
		return webServerPort;
	}

	public void setWebServerPort(Integer webServerPort) {
		this.webServerPort = webServerPort;
	}

	public String getWebServerIp() {
		return webServerIp;
	}

	public void setWebServerIp(String webServerIp) {
		this.webServerIp = webServerIp;
	}

	public Webserver getWs() {
		return ws;
	}

	public void setWs(Webserver ws) {
		this.ws = ws;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUsers(Map<String, User> users) {
		this.users = users;
	}

	public void addUser(String name, String passwd, Boolean force) {
		if (!users.containsKey(name) && !name.equals("admin") || force) {
			User aUser = new User();
			aUser.setName(name);
			aUser.setPassword(passwd);
			users.put(name, aUser);
			aUser.saveUser();
		}
	}

	public void deleteUser(String name) {
		if (users.containsKey(name) && !name.equals("admin")) {
			User aUser = users.get(name);
			aUser.deleteUser();
			users.remove(name);
		}
	}
	
	
	

	public Boolean getHttp2() {
		return http2;
	}

	public void setHttp2(Boolean http2) {
		this.http2 = http2;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(Vector<Log> logs) {
		this.logs = logs;
	}

	public int getMaxLogEntries() {
		return maxLogEntries;
	}

	public void setMaxLogEntries(Integer maxLogEntries) {
		this.maxLogEntries = maxLogEntries;
	}

	public Map<String, Gps> getOrphans() {
		return orphans;
	}

	public void setOrphans(Map<String, Gps> orphans) {
		this.orphans = orphans;
	}

	public List<Plugin> getAvailablePlugins() {
		return availablePlugins;
	}

	public void setAvailablePlugins(List<Plugin> availablePlugins) {
		this.availablePlugins = availablePlugins;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public Boolean getDebugJetty() {
		return debugJetty;
	}

	public void setDebugJetty(Boolean debugJetty) {
		this.debugJetty = debugJetty;
	}

}
