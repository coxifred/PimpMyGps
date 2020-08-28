package pimpmygps.hardware;

import java.io.DataOutputStream;

import pimpmygps.beans.Gps;
import pimpmygps.beans.User;
import pimpmygps.beans.Vehicle;
import pimpmygps.utils.Fonctions;

public class TK103 extends MasterDecoder {

	@Override
	public Integer getPort() {
		return 5001;
	}

	@Override
	public String getName() {
		return "TK103_decoder";
	}

	@Override
	public Boolean receiveMessage(String message, DataOutputStream dOut, Boolean keepDate) {
		Fonctions.trace("DBG", "Receive message [" + message + "] starting to decode...", "TK103");
		// Ex: ##,imei:864180033912549,A;
		// Then respond with LOAD on socket
		// Ex:
		// https://stackoverflow.com/questions/38991490/gps103-tracker-listening-application-in-c-sharp/39828676#39828676
		// 1) Device sends ##,imei:12345678999121,A;
		// 2) Reply with LOAD
		// 3) It sends back 12345678999121;
		// 4) Reply with **,imei:12345678999121,B;
		// 5) It sends back the tracking data
		// imei:12345678999121,tracker,161003171049,,F,091045.000,A,1017.6730,N,07845.7982,E,0.00,0;
		// 6) Reply with ON

		if (message.startsWith("##,imei")) {
			Fonctions.trace("DBG", "Send LOAD sentence", "TK103");
			writeMessage("LOAD", dOut);
			return true;
		} else if (message.startsWith("imei:") && !message.endsWith(";") && !message.contains("tracker")) {
			String imei = message.replace(";", "");
			Fonctions.trace("DBG", "Send " + "**,imei:" + imei + ",B;", "TK103");
			writeMessage("**,imei:" + imei + ",B;", dOut);
			return true;
		} else if (message.startsWith("imei:") && message.endsWith(";")) {
			try {

				// Start Parsing message
				// Remove ; at the end
				message = message.substring(0, message.length() - 1);
				Fonctions.trace("DBG", "Receive real message [" + message + "], it's time now", "TK103");
				String firstField = Fonctions.getFieldFromString(message, ",", 0);
				String imei = Fonctions.getFieldFromString(firstField, ":", 1);

				String latitude_direction = Fonctions.getFieldFromString(message, ",", 8);
				String longitude_direction = Fonctions.getFieldFromString(message, ",", 10);
				Double latitude = Fonctions.dmsToDd(Fonctions.getFieldFromString(message, ",", 7), latitude_direction);
				Double longitude = Fonctions.dmsToDd(Fonctions.getFieldFromString(message, ",", 9),
						longitude_direction);

				String year = Fonctions.getFieldFromString(message, ",", 2).substring(0, 2);
				String month = Fonctions.getFieldFromString(message, ",", 2).substring(2, 4);
				String day = Fonctions.getFieldFromString(message, ",", 2).substring(4, 6);
				String hour = Fonctions.getFieldFromString(message, ",", 2).substring(0, 2);
				String minute = Fonctions.getFieldFromString(message, ",", 2).substring(2, 4);
				String second = Fonctions.getFieldFromString(message, ",", 2).substring(4, 6);

				String speed = Fonctions.getFieldFromString(message, ",", 11);
				String azimut = Fonctions.getFieldFromString(message, ",", 12);
				String version = "";
				// End Parsing message

				// Buildind a GpsEntry
				Gps aGps = new Gps().setId(imei).setProtocol("TK103").setLatitude(latitude).setLongitude(longitude)
						.setSpeed(Float.parseFloat(speed) * 1.852f).setAzimut(Float.parseFloat(azimut))
						.setVersion(version).setRawMessage(message);
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
				Fonctions.trace("DBG", "Send " + "ON", "TK103");
				writeMessage("ON", dOut);
			} catch (Exception e) {
				Fonctions.trace("ERR", "Err decoding message " + message, "H02");
				e.printStackTrace();
			}
			return false;
		} else {
			Fonctions.trace("DBG", "This message " + message + " don't respect TK103 protocol, bypassing", "TK103");
		}
		return false;
	}

}
