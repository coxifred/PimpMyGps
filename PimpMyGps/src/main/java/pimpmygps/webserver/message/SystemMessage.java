package pimpmygps.webserver.message;

import pimpmygps.webserver.Webserver;

public class SystemMessage extends Message{
	
	public SystemMessage()
	{
		Long free=Runtime.getRuntime().freeMemory();
		Long used=Runtime.getRuntime().totalMemory() - free;
		free=free/1024/1024;
		used=used/1024/1024;
		param.put("free", free.toString());
		param.put("used", used.toString());
		param.put("users", Webserver.getUsers());
		
		
		
		action="SYSTEMMESSAGE";
		
	}

}
