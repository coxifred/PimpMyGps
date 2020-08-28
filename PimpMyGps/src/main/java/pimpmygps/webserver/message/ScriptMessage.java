package pimpmygps.webserver.message;

import java.io.File;

public class ScriptMessage extends Message{
	
	public ScriptMessage(String scriptMessage,String state,String server,int rc)
	{
		param.put("script", basename(scriptMessage));
		param.put("rc", rc);
		id=server;
		message=state;
		action="SCRIPTMESSAGE";
		
	}

	
	public String basename(String path) {
			return new File(path).getName();
		}
}
