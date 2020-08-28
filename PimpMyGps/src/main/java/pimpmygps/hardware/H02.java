package pimpmygps.hardware;

import java.io.DataOutputStream;

import pimpmygps.beans.Gps;
import pimpmygps.beans.User;
import pimpmygps.beans.Vehicle;
import pimpmygps.utils.Fonctions;

public class H02 extends MasterDecoder {

	@Override
	public Integer getPort() {
		return 5013;
	}

	@Override
	public String getName() {
		return "H02";
	}

	@Override
	public Boolean receiveMessage(String message, DataOutputStream dOut, Boolean keepDate) {
		// Some examples
		// *HQ,4720010182,V1,165301,V,4743.4849,N,00645.1911,E,000.00,000,080919,FFFFFBFF,208,01,0,0,6#
		// *HQ,4720010182,V1,165416,A,4743.1323,N,00644.3495,E,031.20,202,080919,FFFFFBFF,208,01,0,0,6#
		// *HQ,4720010182,V1,165416,A,4743.1323,N,00644.3495,E,031.20,202,080919,FFFFFBFF,208,01,0,0,6#
		Fonctions.trace("DBG", "Receive message [" + message + "] starting to decode...", "H02");
		if (message.startsWith("*HQ") && message.endsWith("#")) {
			try {

				// Parsing message

				String imei = Fonctions.getFieldFromString(message, ",", 1);
				String version = Fonctions.getFieldFromString(message, ",", 2);
				String hour = Fonctions.getFieldFromString(message, ",", 3).substring(0, 2);
				String minute = Fonctions.getFieldFromString(message, ",", 3).substring(2, 4);
				String second = Fonctions.getFieldFromString(message, ",", 3).substring(4, 6);
				String validity = Fonctions.getFieldFromString(message, ",", 4);

				String latitude_direction = Fonctions.getFieldFromString(message, ",", 6);
				String longitude_direction = Fonctions.getFieldFromString(message, ",", 8);

				Double latitude = Fonctions.dmsToDd(Fonctions.getFieldFromString(message, ",", 5), latitude_direction);
				Double longitude = Fonctions.dmsToDd(Fonctions.getFieldFromString(message, ",", 7),
						longitude_direction);

				Fonctions.trace("DBG", "latitude " + latitude + " longitude " + longitude, "H02");

				String speed = Fonctions.getFieldFromString(message, ",", 9);
				String azimut = Fonctions.getFieldFromString(message, ",", 10);
				String day = Fonctions.getFieldFromString(message, ",", 11).substring(0, 2);
				String month = Fonctions.getFieldFromString(message, ",", 11).substring(2, 4);
				String year = Fonctions.getFieldFromString(message, ",", 11).substring(4, 6);

				// End Parsing

				// Build aGps entry
				Gps aGps = new Gps().setId(imei).setLatitude(latitude).setLongitude(longitude).setProtocol("H02")
						.setVersion(version).setSpeed(Float.parseFloat(speed) * 1.852f)
						.setAzimut(Float.parseFloat(azimut)).setRawMessage(message);
				aGps.initUpdate("20" + year, month, day, hour, minute, second, keepDate);

				// Now find the good gps with same id
				Boolean found = foundId(aGps);
				if (found) {
					Vehicle aVeh = getVehicleFromId(imei);
					User aUser = getUserFromId(imei);
					if (aGps.checkSanity(aVeh)) {
						aVeh.addGpsEntry(aGps, aUser);
					}
				} else {
					Fonctions.trace("WNG", "Don't found " + getName() + " in user/veh list with id " + imei, "H02");
				}

			} catch (Exception e) {
				Fonctions.trace("ERR", "Err decoding message " + message, "H02");
				e.printStackTrace();
			}
		} else {
			Fonctions.trace("DBG", "Conformity error on message " + message + " bypass ", "H02");
		}
		return false;
	}

}
