package pimpmygps.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import pimpmygps.core.Core;
import pimpmygps.plugins.IPlugin;
import pimpmygps.utils.Fonctions;

public class Gps implements Cloneable {

	String protocol;
	String version;
	String id;
	Float speed;
	
	Double longitude;
	Double latitude;
	String rawMessage;

	Float azimut;
	Date update;
	Vector<Gps> historic = new Vector<Gps>();
	List<IPlugin> plugins = new ArrayList<IPlugin>();
	Map<Object, Object> parameters = new HashMap<Object, Object>();
	List<Log> logs = new ArrayList<Log>();
	
	Gps maxSpeed;

	public String getProtocol() {
		return protocol;
	}

	public Gps setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	public Float getSpeed() {
		return speed;
	}

	public Gps setSpeed(Float speed) {
		this.speed = speed;
		return this;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Gps setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Gps setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	public String getId() {
		return id;
	}

	public Gps setId(String id) {
		this.id = id;
		return this;
	}

	
	public Date getUpdate() {
		return update;
	}

	public Gps setUpdate(Date update) {
		this.update = update;
		return this;
	}

	public Vector<Gps> getHistoric() {
		return historic;
	}

	public Gps setHistoric(Vector<Gps> historic) {
		this.historic = historic;
		return this;
	}

	public List<IPlugin> getPlugins() {
		return plugins;
	}

	public Gps setPlugins(List<IPlugin> plugins) {
		this.plugins = plugins;
		return this;
	}

	public Map<Object, Object> getParameters() {
		return parameters;
	}

	public Gps setParameters(Map<Object, Object> parameters) {
		this.parameters = parameters;
		return this;
	}

	public Gps clear() {
		historic.clear();
		parameters.clear();
		logs.clear();
		plugins.clear();
		if ( maxSpeed != null )
		{
			maxSpeed.clear();
		}
		return this;

	}

	public Float getAzimut() {
		return azimut;
	}

	public Gps setAzimut(Float azimut) {
		this.azimut = azimut;
		return this;
	}

	public Gps getMaxSpeed() {
		return maxSpeed;
	}

	public Gps setMaxSpeed(Gps maxSpeed) {
		this.maxSpeed = maxSpeed;
		return this;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public Gps setLogs(List<Log> logs) {
		this.logs = logs;
		return this;
	}

	@Override
	public String toString() {
		return "id:" + id + ", speed:" + speed + ", latitude:" + latitude + " longitude:" + longitude + " azimut:"
				+ azimut + " date:" + update + " protocol:" + protocol + " version:" + version;
	}

	public String getVersion() {
		return version;
	}

	public Gps setVersion(String version) {
		this.version = version;
		return this;
	}

	public void addLogs(String message,String from) {
		String date = Fonctions.getDateFormat(new Date(), "HH:mm:ss.SSS");
		String threadId=String.format("%02d", Thread.currentThread().getId());
		Log aLog=new Log(new Date(),date,threadId,"",from,message);
		logs.add(aLog);
		if (logs.size() > Core.getInstance().getMaxLogEntries()) {
			logs.remove(0);
		}
	}

	@Override
	public Gps clone() throws CloneNotSupportedException {
		Gps aClone = new Gps();
		aClone.setAzimut(Float.valueOf(azimut));
		aClone.setId(this.id);
		aClone.setLatitude(Double.valueOf(this.latitude));
		aClone.setLongitude(Double.valueOf(this.longitude));
		aClone.setParameters(new HashMap<Object,Object>(this.parameters));
		aClone.setProtocol(this.protocol);
		aClone.setRawMessage(this.rawMessage);
		aClone.setSpeed(Float.valueOf(this.speed));
		aClone.setUpdate(this.update);
		aClone.setVersion(this.version);
		
		return aClone;
	}
	
	public Boolean checkSanity(Vehicle aVeh)
	{
		Boolean sanity=true;
		// Checking for bad data (ie : longitude & latitude == 0)
		if (latitude.doubleValue() == 0 && longitude.doubleValue()  == 0) {
			Fonctions.trace("WNG",
					"Warning latitude " + latitude + " longitude " + longitude + " are equals to 0, bypass",
					getProtocol());
			aVeh.getGps()
			.addLogs(Fonctions.getDateFormat(new Date(), null) + " Warning latitude " + latitude
					+ " longitude " + longitude + " are equals to 0, bypass " + rawMessage + " "
					+ this,getProtocol());
			sanity=false;
		}
		
		// Checking if dates are in the past
//		if ( aVeh.getGps() != null && aVeh.getGps().getUpdate() != null &&  aVeh.getGps().getUpdate().getTime() > getUpdate().getTime())
//		{
//			Fonctions.trace("WNG",
//					"Warning Gps date is too old than the last update, message_date=" + getUpdate() + " lastUpdate=" + aVeh.getGps().getUpdate(),getProtocol());
//			aVeh.getGps()
//			.addLogs(Fonctions.getDateFormat(new Date(), null) + "Warning Gps date is too old than the last update, message_date=" + getUpdate() + " lastUpdate=" + aVeh.getGps().getUpdate()+ ", bypass " + rawMessage + " " + this,getProtocol());
//			sanity=false;
//		}
		
		if ( sanity )
				{
				aVeh.getGps().addLogs(Fonctions.getDateFormat(new Date(), null) + " Raw Message received " + rawMessage+ " " + this,getProtocol());
				}
		return sanity;
	}

	public void initUpdate(String year,String month,String day,String hour,String minute,String second,Boolean keepDate)
	{
		// Ajouter un calendar
		Calendar aCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		aCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
		aCalendar.set(Calendar.MINUTE, Integer.valueOf(minute));
		aCalendar.set(Calendar.SECOND, Integer.valueOf(second));
		Date aDate = aCalendar.getTime();
		if (keepDate) {
			aDate = Fonctions.getDateFormat(year + month + day + hour + minute + second,
					"yyyyMMddHHmmss");
		}
		setUpdate(aDate);
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public Gps setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
		return this;
	}

}
