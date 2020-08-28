package pimpmygps.webserver.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import pimpmygps.beans.Gps;
import pimpmygps.beans.Log;
import pimpmygps.beans.SystemGps;
import pimpmygps.beans.User;
import pimpmygps.beans.Vehicle;
import pimpmygps.core.Core;
import pimpmygps.hardware.MasterDecoder;
import pimpmygps.threads.ThreadSocket;
import pimpmygps.utils.Fonctions;


public class AdminServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8546977083372271300L;
	
	static Map<String,List<Date>> ip2Connect=new HashMap<String,List<Date>>();

	public AdminServlet() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		perform(request, response);
	}
	
	

	private void perform(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getParameter("action") != null) {
			String action = request.getParameter("action");
			switch (action) {
			case "login":
				authenfication(request, response);
				break;
			case "logout":
				logout(request, response);
				break;
			case "isLogged":
				isLogged(request, response);
				break;
			case "addUser":
				addUser(request, response);
				break;
			case "listUsers":
				listUsers(request, response);
				break;
			case "sendRequest":
				sendRequest(request, response);
				break;
			case "removeSentRequest":
				removeSentRequest(request, response);
				break;
			case "removeFriend":
				removeFriend(request, response);
				break;
			case "acceptRequest":
				acceptRequest(request, response);
				break;
			case "removeUser":
				removeUser(request, response);
				break;
			case "addVehicle":
				addVehicle(request, response);
				break;
			case "removeVehicle":
				removeVehicle(request, response);
				break;
			case "checkIfVehicleIsPresent":
				checkIfVehicleIsPresent(request, response);
				break;
			case "checkIfUserIsPresent":
				checkIfUserIsPresent(request, response);
				break;
			case "getVehicles":
				getVehicles(request, response);
				break;
			case "getUsers":
				getUsers(request, response);
				break;
			case "getFriends":
				getFriends(request, response);
				break;
			case "getVehicle":
				getVehicle(request, response);
				break;
			case "getUser":
				getUser(request, response);
				break;
			case "getMe":
				getMe(request, response);
				break;
			case "getLogs":
				getLogs(request, response);
				break;
			case "inject":
				inject(request, response);
				break;
			case "apiProfile":
				apiProfile(request, response);
				break;
			case "apiCore":
				apiCore(request, response);
				break;
			case "deleteGpsEntries":
				deleteGpsEntries(request, response);
				break;
			case "getPath":
				getPath(request, response);
				break;
			case "getOrphans":
				getOrphans(request, response);
				break;
			case "deleteOrphans":
				deleteOrphans(request, response);
				break;
			case "recycleOrphans":
				recycleOrphans(request, response);
				break;
			case "deleteFriend":
				deleteFriend(request, response);
				break;
			case "getPlugins":
				getPlugins(request, response);
				break;
			case "getAvailablePlugins":
				getAvailablePlugins(request, response);
				break;
			case "getSystem":
				getSystem(request, response);
				break;
			case "debug":
				debug(request, response);
				break;
			}

		}
	}

	private void getVehicles(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			if (aUser != null) {
				response.getWriter().write(toGson(aUser.getVehicles().values()));
			}

		}
		response.getWriter().write("");

	}

	private void getLogs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String include = request.getParameter("include");
		String exclude = request.getParameter("exclude");
		String vehicle = request.getParameter("vehicle");
		if (requester != null) {
			if (requester.getName().equals("admin") && vehicle == null) {
				response.getWriter().write(toGson(Log.getLogs(include,exclude,Core.getInstance().getLogs())));
			}else
			{
				if ( vehicle != null )
				{
					Vehicle aVehicle=requester.getVehicles().get(vehicle);
					if ( aVehicle != null && aVehicle.getGps() != null)
					{
						response.getWriter().write(toGson(Log.getLogs(include, exclude, aVehicle.getGps().getLogs())));
					}
				}
			}
		}
		response.getWriter().write("");

	}

	private void getUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			if (requester.getName().equals("admin")) {
				response.getWriter().write(toGson(Core.getInstance().getUsers().values().toArray()));
			} else {
				response.getWriter().write(toGson(requester.getFriends()));
			}
		}
		response.getWriter().write("");

	}

	
	private void deleteFriend(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String friend = request.getParameter("name");
		if (requester != null) {
			requester.getFriends().remove(friend);
		}
		response.getWriter().write("");

	}
	
	private void getPlugins(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String vehicle = request.getParameter("vehicle");
		if (requester != null && vehicle != null && requester.getVehicles().containsKey(vehicle)) {
			response.getWriter().write(toGson(requester.getVehicles().get(vehicle).getPlugins()));
		}
	}
	
	private void getAvailablePlugins(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");

		if (requester != null ) {
			response.getWriter().write(toGson(Core.getInstance().getAvailablePlugins()));
		}
	}
	
	private void getSystem(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			response.getWriter().write(toGson(new SystemGps().initialize()));
		}
		

	}
	
	private void debug(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String debugMode = request.getParameter("debug");
		if (requester != null && requester.getName().equals("admin") ) {
			if ( "false".equals(debugMode))
			{
				Core.getInstance().setDebug(false);
				Fonctions.trace("INFO", "Farewell to Debug mode", "CORE");
			}
			if ( "true".equals(debugMode))
			{
				Core.getInstance().setDebug(true);
				Fonctions.trace("INFO", "Respawn Debug mode", "CORE");
			}
			
		}
		response.getWriter().write(Core.getInstance().getDebug().toString());

	}
	
	private void getFriends(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			response.getWriter().write(toGson(requester.getFriends()));
		}
		response.getWriter().write("");

	}

	private void listUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		List<String> users = new ArrayList<String>();
		if (requester != null) {
			for (User aUser : Core.getInstance().getUsers().values()) {
				if (!aUser.getName().equals(requester.getName()) && !requester.getFriends().contains(aUser.getName())) {
					users.add(aUser.getName());
				}
			}
			response.getWriter().write(toGson(users));
		}

	}

	private void getVehicle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String vehicleName = request.getParameter("vehicleName");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			if (aUser != null) {
				response.getWriter().write(toGson(aUser.getVehicles().get(vehicleName)));
			}

		}
		response.getWriter().write("");

	}

	private void getUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String userName = request.getParameter("userName");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(userName);
			if (aUser != null) {
				response.getWriter().write(toGson(aUser));
			}

		}
		response.getWriter().write("");

	}

	private void inject(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		String carName = request.getParameter("gpsName");
		String clean = request.getParameter("clean");
		String keepDate = request.getParameter("keepDate");
		Vehicle veh = requester.getVehicles().get(carName);
		String info = "";

		if (veh != null && veh.getGps() != null && veh.getGps().getProtocol() != null
				&& !"".equals(veh.getGps().getProtocol())) {
			if (Boolean.valueOf(clean)) {
				veh.getGps().clear();
			}
			MasterDecoder md = ThreadSocket.findHardwareByName(veh.getGps().getProtocol());
			if (md != null) {
				String[] data = request.getParameter("data").split("\n");
				for (String message : data) {
					md.receiveMessage(message, null, Boolean.valueOf(keepDate));
				}
			} else {
				info = "User " + requester.getName() + " vehicle " + carName + " couldn't find protocol gps "
						+ veh.getGps().getProtocol();
			}
		} else {
			info = "User " + requester.getName() + " vehicle " + carName + " is missing or gps is not set correctly";
			Fonctions.trace("ERR",
					"User " + requester.getName() + " vehicle " + carName + " is missing or gps is not set correctly",
					"CORE");
		}
		response.getWriter().write(info);

	}

	private void getMe(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			response.getWriter().write(toGson(requester));
		}
	}

	private String toGson(Object obj) {
		Gson aGson = new Gson();
		return aGson.toJson(obj);
	}

	private void deleteGpsEntries(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String vehicleName = request.getParameter("vehicleName");
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			if (aUser != null && vehicleName != null && aUser.getVehicles().containsKey(vehicleName)) {
				aUser.getVehicles().get(vehicleName).getGps().clear();
				response.getWriter()
						.write(Fonctions.toBoolean(aUser.getVehicles().containsKey(vehicleName)).toString());
			}

		} else {
			Fonctions.trace("ERR", "Request with no user in session, please login", "CORE");
			response.getWriter().write("true");
		}
	}

	private void apiProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {

		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			Gson aGson = new Gson();
			response.getWriter().write(aGson.toJson(aUser));
			response.getWriter().write("");
		} else {
			Fonctions.trace("ERR", "Request with no user in session, please login", "CORE");
			response.getWriter().write("true");
		}
	}

	private void apiCore(HttpServletRequest request, HttpServletResponse response) throws IOException {

		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			if (aUser.getName().equals("admin")) {
				Gson aGson = new Gson();
				response.getWriter().write(aGson.toJson(Core.getInstance()));
			} else {
				Fonctions.trace("ERR", "Request for admin only", "CORE");
			}
		} else {
			Fonctions.trace("ERR", "Request with no user in session, please login", "CORE");
			response.getWriter().write("true");
		}
	}

	private Boolean include(Date aDate, Date searchStart, Date searchEnd) {
		if (aDate != null) {
			Calendar boundInf = Calendar.getInstance();
			boundInf.setTime(searchStart);
			boundInf.set(Calendar.HOUR_OF_DAY, 0);
			boundInf.set(Calendar.MINUTE, 0);
			boundInf.set(Calendar.SECOND, 0);
			boundInf.set(Calendar.MILLISECOND, 0);
			Calendar boundSup = Calendar.getInstance();
			boundSup.setTime(searchEnd);
			boundSup.set(Calendar.HOUR_OF_DAY, 23);
			boundSup.set(Calendar.MINUTE, 59);
			boundSup.set(Calendar.SECOND, 59);
			boundSup.set(Calendar.MILLISECOND, 999);
			return (boundInf.getTime().before(aDate) && boundSup.getTime().after(aDate));
		} else {
			return false;
		}
	}

	private void getPath(HttpServletRequest request, HttpServletResponse response) throws IOException {

		User requester = (User) request.getSession().getAttribute("USER");
		String vehicleName = request.getParameter("vehicleName");
		String dateStart = request.getParameter("dateStart");
		String dateEnd = request.getParameter("dateEnd");

		if (requester != null && vehicleName != null && !"".equals(vehicleName) && dateStart != null
				&& !"".equals(dateStart)) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			Vehicle aVeh = aUser.getVehicles().get(vehicleName);
			if (aVeh != null) {
				Gson aGson = new Gson();
				List<Gps> gpz = new ArrayList<Gps>();
				Date searchStart = new Date();
				Date searchEnd = new Date();
				if (!dateStart.equals("today")) {
					searchStart = Fonctions.getDateFormat(dateStart, "yyyyMMdd");

				}

				if (!dateEnd.equals("today")) {
					searchEnd = Fonctions.getDateFormat(dateStart, "yyyyMMdd");
				}

				for (Gps aGps : aVeh.getGps().getHistoric()) {
					if (include(aGps.getUpdate(), searchStart, searchEnd)) {

						gpz.add(aGps);

					}
				}
				if (include(aVeh.getGps().getUpdate(), searchStart, searchEnd)) {

					gpz.add(aVeh.getGps());

				}

				response.getWriter().write(aGson.toJson(gpz));
				response.getWriter().write("");
			} else {
				Fonctions.trace("ERR", "Request with no user in session, please login", "CORE");
				response.getWriter().write("true");
			}
		}
	}

	private void getOrphans(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null && requester.getName().equals("admin")) {
			Gson aGson = new Gson();
			response.getWriter().write(aGson.toJson(Core.getInstance().getOrphans().values()));
		} else {
			Fonctions.trace("ERR", "Request for admin only", "CORE");
			response.getWriter().write("true");
		}
	}

	private void deleteOrphans(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null && requester.getName().equals("admin")) {
			Fonctions.trace("DBG", "Pruning Orphans", "CORE");
			Core.getInstance().getOrphans().clear();
		} else {
			Fonctions.trace("ERR", "Request for admin only", "CORE");
			response.getWriter().write("true");
		}
	}

	private void recycleOrphans(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null && requester.getName().equals("admin")) {
			Fonctions.trace("DBG", "Recycling Orphans", "CORE");
			List<MasterDecoder> toSend = new ArrayList<MasterDecoder>();

			for (User aUser : Core.getInstance().getUsers().values()) {
				for (Vehicle aVeh : aUser.getVehicles().values()) {
					if (aVeh.getGps() != null && aVeh.getGps().getProtocol() != null) {
						MasterDecoder md = ThreadSocket.findHardwareByName(aVeh.getGps().getProtocol());
						toSend.add(md);
					}
				}
			}
			for (Gps toRecycle : Core.getInstance().getOrphans().values()) {
				for (MasterDecoder md:toSend)
				{
					md.receiveMessage(toRecycle.getRawMessage(), null, true);
				}
			}
		} else {
			Fonctions.trace("ERR", "Request for admin only", "CORE");
			response.getWriter().write("true");
		}
	}

	private void checkIfVehicleIsPresent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String vehicleName = request.getParameter("vehicleName");
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(requester.getName());
			if (aUser != null && vehicleName != null) {
				response.getWriter()
						.write(Fonctions.toBoolean(aUser.getVehicles().containsKey(vehicleName)).toString());
			}

		} else {
			Fonctions.trace("ERR", "Request with no user in session, please login", "CORE");
			response.getWriter().write("true");
		}
	}

	private void checkIfUserIsPresent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userName = request.getParameter("userName");
		User requester = (User) request.getSession().getAttribute("USER");
		if (requester != null) {
			User aUser = Core.getInstance().getUsers().get(userName);
			if (aUser != null) {
				response.getWriter().write("true");
			} else {
				response.getWriter().write("false");
			}
		} else {
			Fonctions.trace("ERR", "Request with no user in session, please login", "CORE");
			response.getWriter().write("true");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		perform(req, resp);
	}

	private void addVehicle(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String vehicleName = request.getParameter("vehicleName");
		String gpsName = request.getParameter("gpsName");
		String gpsId = request.getParameter("gpsId");
		if (requester != null && Core.getInstance().getUsers().containsKey(requester.getName()) && gpsName != null
				&& gpsId != null && vehicleName != null) {
			Core.getInstance().getUsers().get(requester.getName()).addVehicle(vehicleName, gpsName, gpsId);
			Core.getInstance().getUsers().get(requester.getName()).saveUser();
		}
	}

	private void removeVehicle(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String vehicleName = request.getParameter("vehicleName");
		if (requester != null && Core.getInstance().getUsers().containsKey(requester.getName())) {
			Core.getInstance().getUsers().get(requester.getName()).removeVehicle(vehicleName);
			Core.getInstance().getUsers().get(requester.getName()).saveUser();
		}
	}

	private void addUser(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("userName");
		String passwd = request.getParameter("userPasswd");
		String force = request.getParameter("force");
		if (force != null && force.contentEquals("true")) {
			Core.getInstance().addUser(name, passwd, true);
		} else {
			Core.getInstance().addUser(name, passwd, false);
		}

	}

	private void removeUser(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("userName");
		Core.getInstance().deleteUser(name);
	}

	private void sendRequest(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String target = request.getParameter("targetName");
		User user = Core.getInstance().getUsers().get(target);
		if (requester != null && user != null) {
			user.getRequests().add(requester.getName());
			requester.getSentRequests().add(user.getName());
			user.saveUser();
			requester.saveUser();
		}
	}

	private void removeSentRequest(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String target = request.getParameter("targetName");
		User user = Core.getInstance().getUsers().get(target);
		if (requester != null && user != null) {
			user.getRequests().remove(requester.getName());
			requester.getSentRequests().remove(user.getName());
			user.saveUser();
			requester.saveUser();
		}
	}

	private void acceptRequest(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String target = request.getParameter("targetName");
		User user = Core.getInstance().getUsers().get(target);
		if (requester != null && user != null) {
			requester.getRequests().remove(user.getName());
			user.getSentRequests().remove(requester.getName());
			requester.getFriends().add(user.getName());
			user.getFriends().add(requester.getName());
			user.saveUser();
			requester.saveUser();
		}
	}

	private void removeFriend(HttpServletRequest request, HttpServletResponse response) {
		User requester = (User) request.getSession().getAttribute("USER");
		String target = request.getParameter("targetName");
		User user = Core.getInstance().getUsers().get(target);
		if (requester != null && user != null) {
			requester.getRequests().remove(user.getName());
			user.getSentRequests().remove(requester.getName());
			requester.getFriends().remove(user.getName());
			user.getFriends().remove(requester.getName());
			user.saveUser();
			requester.saveUser();
		}
	}

	private void authenfication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = request.getParameter("user");
		String passwd = request.getParameter("passwd");
		manageFail2Ban(request);
		if (User.isAuthentified(user, passwd)) {
			Fonctions.trace("INF","Connection from ip successfull for user " + user,"CORE");
			request.getSession().setAttribute("USER", Core.getInstance().getUsers().get(user));
			if (Core.getInstance().getUsers().get(user).getName().equals("admin")) {
				response.getWriter().write("/main_admin.html");
			} else {
				response.getWriter().write("/main.html");
			}

		} else {
			response.getWriter().write("/index.html");
		}
	}
	
	private void manageFail2Ban(HttpServletRequest request)
	{
		String remoteIp=request.getRemoteAddr();
		List<Date> aDateList=new ArrayList<Date>();
		if ( ip2Connect.containsKey(remoteIp) )
		{
			aDateList=ip2Connect.get(remoteIp);
		}
		Date aDate=new Date();
		aDateList.add(aDate);
		ip2Connect.put(remoteIp,aDateList);
		for ( Date currentDate:ip2Connect.get(remoteIp))
		{
			System.out.println(currentDate);;
		}
		// Counting acces for last 10 minutes, if > 10, then slowing
		Calendar start=Calendar.getInstance(); start.add(Calendar.MINUTE, -10);
		Calendar end=Calendar.getInstance();
		// Discovering dates
		List<Date> newDates=new ArrayList<Date>();
		for ( Date theDate:aDateList)
		{
			if ( theDate.after(start.getTime())&& theDate.before(end.getTime()))
			{
				newDates.add(theDate);
			}
		}
		ip2Connect.put(remoteIp,newDates);
		Fonctions.trace("INF",newDates.size() + " connection(s) within 10 minutes from ip " + remoteIp + " @" + aDate,"CORE");
		if ( newDates.size() > 10)
		{
			Fonctions.trace("INF","Slowing connection, by waiting " + newDates.size() + " * 120 secs for ip " + remoteIp + " @" + aDate,"CORE");
			Fonctions.attendre(newDates.size() * 120000);
		}
		Fonctions.trace("INF","Trying Connection from ip " + remoteIp + " @" + aDate,"CORE");
	}

	private void isLogged(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (request.getSession().getAttribute("USER") != null) {
			response.getWriter().write("true");
		} else {
			response.getWriter().write("false");
		}

	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().removeAttribute("USER");
	}

}