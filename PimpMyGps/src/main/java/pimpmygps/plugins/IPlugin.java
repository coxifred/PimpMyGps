package pimpmygps.plugins;

import pimpmygps.beans.Gps;
import pimpmygps.beans.Vehicle;
import pimpmygps.core.Core;

public interface IPlugin {
	
	Gps filter(Gps aGps,Vehicle aVehicle);
	String getName();
	Boolean getUpdateable();
	Boolean update(Core core);
	Object fromWeb(Plugin pluginWeb,Vehicle aVehicle);
	void init(Core aCore,Vehicle aVehicle);
}
