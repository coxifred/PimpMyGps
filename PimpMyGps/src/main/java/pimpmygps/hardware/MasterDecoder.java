package pimpmygps.hardware;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import pimpmygps.beans.Gps;
import pimpmygps.beans.User;
import pimpmygps.beans.Vehicle;
import pimpmygps.core.Core;
import pimpmygps.utils.Fonctions;

public abstract class MasterDecoder {

	public abstract Boolean receiveMessage(String message, DataOutputStream out, Boolean keepDate);

	protected void writeMessage(String message, DataOutputStream dOut) {
		if (message != null && dOut != null) {
			byte[] bytes = message.getBytes(StandardCharsets.US_ASCII);
			StringBuilder strb = new StringBuilder();
			for (byte aByte : bytes) {
				strb.append("[" + Integer.toHexString(aByte) + "] ");
			}
			strb.append(" -> ");
			for (byte aByte : bytes) {
				strb.append("[" + (char) aByte + "] ");
			}
			Fonctions.trace("DBG", "Flushing a byte array " + strb, "CORE");
			try {
				dOut.write(bytes);
				dOut.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Fonctions.trace("WNG", "Couldn't write message, because null or outputStream is null", "CORE");
		}
	}

	protected Boolean foundId(Gps foundGps) {
		Boolean found = false;

		for (User aUser : Core.getInstance().getUsers().values()) {
			for (Vehicle aVeh : aUser.getVehicles().values()) {
				if (aVeh.getGps() != null && foundGps.getId().equals(aVeh.getGps().getId())) {
					Fonctions
							.trace("DBG",
									"Found user " + aUser.getName() + " with vehicle " + aVeh.getName() + " with gps "
											+ aVeh.getGps().getProtocol() + " matching updating ....",
									foundGps.getProtocol());
					found = true;
				}
			}
		}
		if (found) {
			Core.getInstance().getOrphans().remove(foundGps.getId());
		} else {
			Core.getInstance().getOrphans().remove(foundGps.getId());
			Core.getInstance().getOrphans().put(foundGps.getId(), foundGps);
			Fonctions.trace("DBG", "Can't link IMEI with existing user/gps, putting imei in orphans.",
					foundGps.getProtocol());
		}
		return found;
	}

	protected Vehicle getVehicleFromId(String imei) {
		for (User aUser : Core.getInstance().getUsers().values()) {
			for (Vehicle aVeh : aUser.getVehicles().values()) {
				if (aVeh.getGps() != null && imei.equals(aVeh.getGps().getId())) {
					return aVeh;
				}
			}
		}
		return null;
	}

	protected User getUserFromId(String imei) {

		for (User aUser : Core.getInstance().getUsers().values()) {
			for (Vehicle aVeh : aUser.getVehicles().values()) {
				if (aVeh.getGps() != null && imei.equals(aVeh.getGps().getId())) {
					return aUser;
				}
			}
		}
		return null;
	}

	public abstract Integer getPort();

	public abstract String getName();
}