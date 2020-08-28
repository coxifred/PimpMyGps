package pimpmygps.webserver.websocket;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import com.google.gson.Gson;

import pimpmygps.utils.Fonctions;
import pimpmygps.webserver.message.Message;

public class SkittleSocket implements WebSocketListener {

	private static Map<String, Session> allSessions;

	@Override
	public void onWebSocketClose(int arg0, String arg1) {
		System.out.println("onWebSocketClose " + arg1 + " " + arg0);
	}

	@Override
	public void onWebSocketConnect(Session arg0) {
		if (allSessions == null) {
			allSessions = new HashMap<String, Session>();
		}
		if (!allSessions.containsKey(arg0.toString())) {
			allSessions.put(arg0.getRemoteAddress().toString(), arg0);
		}

		System.out.println("onWebSocketConnect");

	}

	@Override
	public void onWebSocketError(Throwable arg0) {
		System.out.println("onWebSocketError");

	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
		System.out.println("onWebSocketBinary");

	}

	public synchronized static void broadcastMessage(Message message) {
		Gson aGson = new Gson();
		String messageJson = aGson.toJson(message);

		for (Session sess : allSessions.values()) {

			try {
				sess.getRemote().sendString(messageJson);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
//		if ("POSTMESSAGESHARE".equals(message.getAction())) {
//			Webserver.setShareBox(message.getMessage());
//		}

	}

	//
	// private static void setUser(Message message, String messageJson) {
	// User realUser=null;
	// for (User aUser:Webserver.getUsers())
	// {
	// if ( aUser.getName().equals(message.getFromUser().getName()))
	// {
	// realUser=aUser;
	// break;
	// }
	// }
	// // On en profite pour rebalancer un message de stats
	//
	// if ( Core.getInstance().setUserForServer(realUser, message.getMessage())
	// )
	// {
	// for (Session sess : allSessions.values()) {
	//
	// try {
	// sess.getRemote().sendString(messageJson);
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	// }

	@Override
	public void onWebSocketText(String message) {

		Fonctions.trace("DBG", "onWebSocketText " + message, "CORE");

		analyseRequest(message);

	}

	public static Map<String, Session> getAllSessions() {
		return allSessions;
	}

	public static void setAllSessions(Map<String, Session> allSessions) {
		SkittleSocket.allSessions = allSessions;
	}

	private static void analyseRequest(String message) {
		Gson aGson = new Gson();
		Message aMessage = aGson.fromJson(message, Message.class);
		if (aMessage != null) {

			broadcastMessage(aMessage);

		}

	}

}
