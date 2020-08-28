package pimpmygps.threads;

import java.util.HashMap;
import java.util.Map;

import pimpmygps.beans.User;
import pimpmygps.beans.Vehicle;
import pimpmygps.core.Core;
import pimpmygps.hardware.MasterDecoder;
import pimpmygps.utils.Fonctions;

public class ThreadSocketDeclarator extends Thread {

	Map<Integer, ThreadSocket> portToSocket = new HashMap<Integer, ThreadSocket>();

	@Override
	public void run() {
		setName("ThreadSocketDeclarator" + getId());
		while (true) {
			Fonctions.trace("DBG", "Checking if there is some socket to open", "CORE");
			for (User aUser : Core.getInstance().getUsers().values()) {
				for (Vehicle aVehicle : aUser.getVehicles().values()) {
					// Checking that Socket is started for gps kind
					if (aVehicle.getGps() != null && aVehicle.getGps().getProtocol() != null
							&& !"".equals(aVehicle.getGps().getProtocol())) {
						try {
							Class<?> aClass = Class
									.forName("pimpmygps.hardware." + aVehicle.getGps().getProtocol());
							try {
								MasterDecoder aGps = (MasterDecoder) aClass.getDeclaredConstructor().newInstance();
								if (portToSocket.containsKey(aGps.getPort())) {
									ThreadSocket aThreadSocket = portToSocket.get(aGps.getPort());
									if (!aThreadSocket.isAlive()) {
										aThreadSocket.kill();
										Fonctions.trace("INF", "Recreate Socket in 4 secs for port " + aGps.getPort() + " seems to be dead","CORE");
										Fonctions.attendre(4000);
										createNewThreadSocket(aGps.getPort());
									}

								} else {
									Fonctions.trace("INF", "Create Socket for port " + aGps.getPort(),"CORE");
									createNewThreadSocket(aGps.getPort());
								}
							} catch (Exception e) {
								Fonctions.trace("ERR", "Couldn't instanciate class pimpmygps.filters.decoders."
										+ aVehicle.getGps().getProtocol() + " error " + e.getMessage(), "CORE");
								e.printStackTrace();
							}
						} catch (ClassNotFoundException e) {
							Fonctions.trace("ERR", "Couldn't find class with name " + "pimpmygps.hardware."
									+ aVehicle.getGps().getProtocol() + " this protocol is unknown from PimpMyGps",
									"CORE");
						}
					}else
					{
					Fonctions.trace("DBG", "Warning User " + aUser.getName() + " with vehicle " + aVehicle.getName() + " dont have set gps or protocol is empty",
							"CORE");
					}
				}
			}
			Fonctions.attendre(5000);
		}

	}

	private void createNewThreadSocket(Integer port) {
		ThreadSocket ts = new ThreadSocket(port);
		ts.start();
		portToSocket.put(port, ts);

	}

}
