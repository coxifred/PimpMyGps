package pimpmygps.beans;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

import pimpmygps.core.Core;
import pimpmygps.utils.Fonctions;

public class User {
	
	Integer joinYear;
	String description;
	String email;
	String amountMessages;
	Integer distance;
	
	public User()
	{
		joinYear=Calendar.getInstance().get(Calendar.YEAR);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Map<String, Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	String name;
	String password;
	UserSettings settings=new UserSettings();
	Map<String, Vehicle> vehicles = new HashMap<String, Vehicle>();
	Date lastUpdate = new Date();
	Set<String> friends=new HashSet<String>();
	Set<String> requests=new HashSet<String>();
	Set<String> sentRequests=new HashSet<String>();

	public static Boolean isAuthentified(String user, String passwd) {
		Boolean retour = false;
		for (User aUser : Core.getInstance().getUsers().values()) {
			if (aUser.getName().equals(user) && aUser.getPassword().contentEquals(passwd)) {
				return true;
			}
		}

		return retour;
	}

	public static User getUserByName(String user) {
		for (User aUser : Core.getInstance().getUsers().values()) {
			if (aUser.getName().equals(user)) {
				return aUser;
			}
		}
		return null;
	}

	public void addVehicle(String name) {
		Vehicle aVeh = new Vehicle();
		aVeh.setName(name);
		vehicles.put(name, aVeh);
	}

	public void addVehicle(String name, String gpsName, String gpsId) {
		Vehicle aVeh = new Vehicle();
		aVeh.setName(name);
		aVeh.addGps(gpsName, gpsId);
		vehicles.put(name, aVeh);
	}

	public void removeVehicle(String name) {
		vehicles.remove(name);
	}

	public void saveUser() {
		XStream aStream = new XStream();
		try {
			File aFile = new File(System.getProperty("dataPath", Core.getInstance().getDataPath()));
			Fonctions.trace("DBG", "Saving file to " + aFile.getAbsolutePath() + "/pimpMyGps_user_" + name + ".xml",
					"CORE");
			lightUserVeh();
			aStream.toXML(this, new FileWriter(aFile.getAbsolutePath() + "/pimpMyGps_user_" + name + ".xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, User> loadUsers() {
		Map<String, User> userList = new HashMap<String, User>();
		XStream aStream = new XStream();
		File aFile = new File(System.getProperty("dataPath", Core.getInstance().getDataPath()));
		if ( aFile.exists())
		{
		for (File theFile : aFile.listFiles()) {
			if (theFile.getName().startsWith("pimpMyGps") && theFile.getName().endsWith(".xml")) {
				try {
					User aUser = (User) aStream.fromXML(new FileReader(theFile));
					userList.put(aUser.getName(), aUser);
					aUser.lightUserVeh();
				} catch (Exception e) {
					Fonctions.trace("ERR", "Couldn't reload " + theFile + e.getMessage(), "CORE");
				}
			}
		}
		
		}else
		{
			Fonctions.trace("DEAD", "Couldn't evaluate dataPatgh [" + aFile + "], is it empty? Please set dataPath on a valid path in aCore.xml or in -DdataPath=...", "CORE");
		}
		if (!userList.containsKey("admin")) {
			User admin = new User();
			admin.setName("admin");
			admin.setLastUpdate(new Date());
			admin.setPassword(Core.getInstance().getAdminPassword());
			userList.put("admin", admin);
		}
		return userList;

	}
	
	public void lightUserVeh()
	{
		for ( Vehicle aVeh:getVehicles().values())
		{
			if ( aVeh.getGps().getMaxSpeed() !=null )
				{
				aVeh.getGps().getMaxSpeed().clear();
				}
			List<Gps> toRemove=new ArrayList<Gps>();
			for ( Gps aGps:aVeh.getGps().getHistoric())
			{
				//aGps.clear();
				// Remove 0 longitude and 0 latitude and longitude/latitude null
				if (   aGps.getLongitude() == null || aGps.getLatitude() == null ||  ( aGps.getLatitude().doubleValue() == 0 && aGps.getLongitude().doubleValue() == 0))
				{
					toRemove.add(aGps);
				}
			}
			for ( Gps aRemove:toRemove)
			{
				Fonctions.trace("WNG", "Remove bad 0 or null latitude and longitude", "CORE");
				aVeh.getGps().getHistoric().remove(aRemove);
			}
		}
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void deleteUser() {
		File aFile = new File(name + ".xml");
		aFile.delete();
	}

	public Set<String> getFriends() {
		return friends;
	}

	public void setFriends(Set<String> friends) {
		this.friends = friends;
	}

	public Set<String> getRequests() {
		return requests;
	}

	public void setRequests(Set<String> requests) {
		this.requests = requests;
	}

	public Set<String> getSentRequests() {
		return sentRequests;
	}

	public void setSentRequests(Set<String> sentRequests) {
		this.sentRequests = sentRequests;
	}

	public Integer getJoinYear() {
		return joinYear;
	}

	public void setJoinYear(Integer joinYear) {
		this.joinYear = joinYear;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAmountMessages() {
		return amountMessages;
	}

	public void setAmountMessages(String amountMessages) {
		this.amountMessages = amountMessages;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public UserSettings getSettings() {
		return settings;
	}

	public void setSettings(UserSettings settings) {
		this.settings = settings;
	}

	
	
	
}
