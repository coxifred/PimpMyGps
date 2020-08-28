package pimpmygps.threads;

import pimpmygps.beans.User;
import pimpmygps.core.Core;
import pimpmygps.utils.Fonctions;

public class ThreadSaver extends Thread {

	

	@Override
	public void run() {
		setName("ThreadSaver");
		while (true) {
			Fonctions.trace("DBG", "Saving data now", "CORE");
			for (User aUser : Core.getInstance().getUsers().values()) {
				aUser.saveUser();
			}

			Fonctions.attendre(600000);
		}
	}

	
}
