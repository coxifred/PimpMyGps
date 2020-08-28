package pimpmygps.plugins;

import com.google.gson.Gson;

import pimpmygps.beans.Gps;
import pimpmygps.beans.Vehicle;
import pimpmygps.core.Core;

public abstract class Plugin implements IPlugin{
	String name;
	String version;
	String description;
	Boolean updateable;

	public abstract String getName();

	public void setName(String name) {
		this.name = name;
	}



	public void setDescription(String description) {
		this.description = description;
	}

	

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public abstract Gps filter(Gps aGps,Vehicle aVehicle);
	public abstract Boolean update(Core core);
	
	
	
	public abstract String fromWeb(Plugin pluginWeb,Vehicle aVehicle);
	public abstract void init(Core aCore,Vehicle aVehicle);
	
	public String returnMe()
	{
		Gson aGson=new Gson();
		return aGson.toJson(this);
	}

	public abstract Boolean getUpdateable();

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}
	
}
