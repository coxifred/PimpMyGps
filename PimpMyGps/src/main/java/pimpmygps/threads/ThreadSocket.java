package pimpmygps.threads;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pimpmygps.beans.User;
import pimpmygps.beans.Vehicle;
import pimpmygps.core.Core;
import pimpmygps.hardware.MasterDecoder;
import pimpmygps.utils.Fonctions;

public class ThreadSocket extends Thread {

	Boolean run = true;
	Integer port;

	public ThreadSocket(Integer port) {
		this.port = port;
	}

	@Override
	public void run() {
		setName("ThreadSocket" + getId());
		List<MasterDecoder> mdz = (List<MasterDecoder>) getMasterDecoderFromPort(port);
		ServerSocket serverSocket = null;
		Socket socket = null;
		while (run) {
			try {
				if (serverSocket == null) {
					serverSocket = new ServerSocket(port);
				}	
				String inputLine;
				socket = serverSocket.accept();
				Fonctions.trace("DBG", "Opening Socket " + socket.toString(), "CORE");
				Integer maxWait = 100; // 2 seconds
				// PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
				BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

				Boolean stillCommunication = true;
				while (stillCommunication && maxWait > 0) {

					byte[] b = new byte[4096];
					int stream = in.read(b);
					inputLine = new String(b, 0, stream);

					for (MasterDecoder md : mdz) {

						if (!md.receiveMessage(inputLine, dOut, false)) {
							Fonctions.trace("DBG",
									"No more communication expected, attempt to close socket " + socket.toString(),
									"CORE");
							stillCommunication = false;
						}
					}
					// cbf = CharBuffer.allocate(1024);
					Fonctions.attendre(20);
					maxWait--;
				}

				Fonctions.trace("DBG", "Closing Socket " + socket.toString(), "CORE");
				dOut.close();
				in.close();
				socket.close();
			} catch (Exception e) {
				Fonctions.trace("ERR", "Something bad in ThreadSocket " + e.getMessage(), " will reset in 250ms");
				e.printStackTrace();
				if ( serverSocket != null ) {
					
					try {
						serverSocket.close();
					} catch (Exception e1) {
						e.printStackTrace();
					}
				}
				try {
					if ( serverSocket != null ) serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					serverSocket = null;
					Fonctions.trace("DBG", "This threadSocket will be shutdown","CORE");
					run=false;
				}

			} finally {
				if ( serverSocket != null ) {
					
					try {
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Fonctions.attendre(250);
		}
		Fonctions.trace("DBG", "Existing from ThreadSocket, because run=false","CORE");
	}

	public void kill() {
		run = false;
	}

	private List<MasterDecoder> getMasterDecoderFromPort(Integer port) {
		Map<String, MasterDecoder> decoders = new HashMap<String, MasterDecoder>();
		for (User aUser : Core.getInstance().getUsers().values()) {
			for (Vehicle aVeh : aUser.getVehicles().values()) {
				if (!"".equals(aVeh.getGps().getProtocol())) {
					MasterDecoder md = findHardwareByName(aVeh.getGps().getProtocol());
					if (md != null) {
						if (md.getPort().equals(port)) {
							Fonctions.trace("DBG",
									"Found hardware " + md.getName() + " with same port " + port
											+ " adding to pipeline " + aVeh.getGps().getProtocol() + " For Veh "
											+ aVeh.getName() + " for User " + aUser.getName(),
									"CORE");
							decoders.put(aVeh.getGps().getProtocol(), findHardwareByName(aVeh.getGps().getProtocol()));
						} else {
							Fonctions.trace("DBG",
									"This hardware " + md.getName() + " don't match with port " + port + " bypass "
											+ aVeh.getGps().getProtocol() + " For Veh " + aVeh.getName() + " for User "
											+ aUser.getName(),
									"CORE");
						}
					} else {
						Fonctions.trace("DBG", "Can't found hardware class " + aVeh.getGps().getProtocol() + " For Veh "
								+ aVeh.getName() + " for User " + aUser.getName(), "CORE");
					}
				}
			}
		}
		return new ArrayList<MasterDecoder>(decoders.values());
	}

	public static MasterDecoder findHardwareByName(String name) {
		MasterDecoder findMd = null;
		try {
			Class<?> aClass = (Class<?>) Class.forName("pimpmygps.hardware." + name);
			try {
				findMd = (MasterDecoder) aClass.getConstructor(new Class<?>[0]).newInstance();
				if (findMd != null) {
					Fonctions.trace("DBG", "Found class " + name + " return an instanciation", "CORE");
					return findMd;
				}
			} catch (Exception e) {
				Fonctions.trace("ERR", "Couldn't instanciate class " + name + " " + e.getMessage()
						+ " is there at least a Pojo constructor in the class?", "CORE");
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			Fonctions.trace("ERR",
					"Something bad in searching class " + name + " " + e.getMessage() + " did you include plugins ?",
					"CORE");
			e.printStackTrace();
		}

		return findMd;
	}
}
