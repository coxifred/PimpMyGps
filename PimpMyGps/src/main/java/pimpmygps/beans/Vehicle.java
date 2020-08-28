package pimpmygps.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import pimpmygps.plugins.Plugin;
import pimpmygps.utils.Fonctions;

public class Vehicle {

	String name;
	Gps gps = new Gps();
	Date lastUpdate = new Date();
	List<Plugin> plugins=new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Gps getGps() {
		return gps;
	}

	public void setGps(Gps gps) {
		this.gps = gps;
	}

	public void addGps(String gpsName, String gpsId) {
		Gps aGps=new Gps();
		aGps.setId(gpsId);
		aGps.setProtocol(gpsName);
		setGps(aGps);
		
	}
	
	public void addGpsEntry(Gps newEntry,User aUser) throws CloneNotSupportedException
	{
		
			// Starting by retrieve all historical
			newEntry.getHistoric().addAll(new Vector<Gps>(this.gps.getHistoric()));
			// Adding the old one in historical
			newEntry.getHistoric().add(getGps());
			// Then retrieve all logs messages
			newEntry.getLogs().addAll(this.gps.getLogs());
			// Clearing historic and log from historic list :) outofmemory prevention
			// Cleaning actually historic of historic injected just above
			for (Gps anHisto : newEntry.getHistoric()) {
				anHisto.getHistoric().clear();
				anHisto.getLogs().clear();
			}
			// Then retrieve all parameters
			newEntry.setParameters(getGps().getParameters());
			
			newEntry.addLogs(Fonctions.getDateFormat(new Date(), null) + " Adding a new entry "
					+ newEntry,getName());
			// Perform max speed detection
			// Look if we beat maxSpeed
			if ( getGps().getMaxSpeed() == null )
			{
				Fonctions.trace("DBG","Setting new entry as maxSpeed record.",newEntry.getProtocol());
				newEntry.setMaxSpeed(newEntry.clone());
			}else if ( getGps().getMaxSpeed().getSpeed().floatValue() < newEntry.getSpeed().floatValue()) {
					Fonctions.trace("DBG",
						"User " + aUser.getName() + " with vehicle " + getName() + " with gps "
								+ newEntry.getProtocol() + " beats its speed record @" + newEntry.getSpeed() + " old one was " + getGps().getMaxSpeed().getSpeed(),
								newEntry.getProtocol());
				newEntry.addLogs(Fonctions.getDateFormat(new Date(), null) + " You beat your speed record "
						+ newEntry.getSpeed(),getName());
				newEntry.setMaxSpeed(newEntry.clone());
			}
				
			// Copy Gps into vehicle
			setGps(newEntry);
			// Update date for the vehicle
			setLastUpdate(newEntry.getUpdate());
			// Extra deep clean
			aUser.lightUserVeh();

	}

	public List<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<Plugin> plugins) {
		this.plugins = plugins;
	}

}
