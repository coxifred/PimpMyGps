package pimpmygps.webserver;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import pimpmygps.utils.Fonctions;
import pimpmygps.utils.JettyLogger;
import pimpmygps.webserver.servlets.AdminServlet;
import pimpmygps.webserver.websocket.JettyWebSocketServlet;

public class JettyWebServer extends Thread {

	Integer port;
	String ip;
	Server serverWeb;
	Server serverSocket;
	Boolean http2;

	public JettyWebServer(String ip, Integer port,Boolean http2) {
		this.ip = ip;
		this.port = port;
		this.http2 = http2;
		start();
	}

	public void run() {
		// InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(ip,
		// port);
		org.eclipse.jetty.util.log.Log.setLog(new JettyLogger());
		
		serverWeb = new Server();
		serverSocket = new Server();
		http2 = false;
		try {
			if ( http2)
			{
				Fonctions.trace("DBG", "Starting HTTP2 server", "CORE");
				startWeb(serverWeb);
			}else
			{
				Fonctions.trace("DBG", "Starting classic server", "CORE");
				startWebSimple(serverWeb);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void startWeb(Server server) throws Exception, InterruptedException {

		setContextsWeb(server);

		String jettyDistKeystore = "ssl/keystore";
		String keyStorePath = getClass().getClassLoader().getResource(jettyDistKeystore).toExternalForm();

		// HTTP Configuration
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(port);
		http_config.addCustomizer(new SecureRequestCustomizer());

		// SSL Context Factory for HTTPS and HTTP/2
		SslContextFactory sslContextFactory = new SslContextFactory.Server();

		sslContextFactory.setKeyStorePath(keyStorePath);
		;
		sslContextFactory.setKeyStorePassword("pimpMyGps");
		sslContextFactory.setKeyManagerPassword("pimpMyGps");
		sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);


		// HTTP/2 Connection Factory
		HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(http_config);
		ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
		alpn.setDefaultProtocol("h2");

		// SSL Connection Factory
		SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

		// HTTP/2 Connector
		ServerConnector http2Connector = new ServerConnector(server, ssl, alpn, h2,	new HttpConnectionFactory(http_config));
		http2Connector.setPort(port);
		http2Connector.setHost(ip);
		server.addConnector(http2Connector);
		
		server.start();
		server.join();

	}
	
	
	
	
	
	
	
	
	private void startWebSimple(Server server) throws Exception, InterruptedException {

		setContextsWeb(server);

		SslContextFactory contextFactory = new SslContextFactory();
		
		String jettyDistKeystore = "ssl/keystore";
		String keyStorePath = getClass().getClassLoader().getResource(jettyDistKeystore).toExternalForm();

		contextFactory.setKeyStorePath(keyStorePath);
		contextFactory.setKeyStorePassword("pimpMyGps");
		contextFactory.setKeyManagerPassword("pimpMyGps");
		contextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
		
		SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(contextFactory, org.eclipse.jetty.http.HttpVersion.HTTP_1_1.toString());

		HttpConfiguration config = new HttpConfiguration();
		config.setSecureScheme("https");
		config.setSecurePort(port);
		config.setOutputBufferSize(32786);
		config.setRequestHeaderSize(8192);
		config.setResponseHeaderSize(8192);
		HttpConfiguration sslConfiguration = new HttpConfiguration(config);
		sslConfiguration.addCustomizer(new SecureRequestCustomizer());
		HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(sslConfiguration);

		ServerConnector connector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
		connector.setPort(port);
		server.addConnector(connector);

		server.start();
		server.join();

	}

	private void setContextsWeb(Server server) {
		String webInterfaceDir = getClass().getClassLoader().getResource("webInterface").toExternalForm();
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setResourceBase(webInterfaceDir);
		context.addServlet(AdminServlet.class, "/admin/*");

		// context.addServlet(new ServletHolder(new JettyWebSocketServlet()), "/hello");
//		server.setHandler(context);

//		
		ContextHandlerCollection contexts = new ContextHandlerCollection();
//		WebAppContext webAppContext = new WebAppContext();
//		ServletContextHandler adminContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		adminContext.setContextPath("/admin");
//		adminContext.addServlet(new ServletHolder(new AdminServlet()), "/*");

		ServletContextHandler socketContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		socketContext.setContextPath("/adminwebsocket");
		socketContext.addServlet(new ServletHolder(new JettyWebSocketServlet()), "");

//		try {
//			ServerContainer jsrContainer = WebSocketServerContainerInitializer.configureContext(webAppContext);
//			jsrContainer.addEndpoint(AdminWebSocket.class);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		String webInfDir = "webInterface";
//		if (getClass().getClassLoader().getResource("webInterface") != null) {
//			webInfDir = getClass().getClassLoader().getResource("webInterface").toExternalForm();
//		}
//		webAppContext.setOverrideDescriptor(webInfDir + "/web.xml");
//		webAppContext.setContextPath("/");
//		String webInterfaceDir = getClass().getClassLoader().getResource("webInterface").toExternalForm();
//		webAppContext.setResourceBase(webInterfaceDir);
		// webAppContext.setResourceBase("webInterface");
		contexts.setHandlers(new Handler[] { context, socketContext });
		server.setHandler(contexts);
	}

	
	public Server getServerWeb() {
		return serverWeb;
	}

	public void setServerWeb(Server serverWeb) {
		this.serverWeb = serverWeb;
	}

	public Server getServerSocket() {
		return serverSocket;
	}

	

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Boolean getHttp2() {
		return http2;
	}

	public void setHttp2(Boolean http2) {
		this.http2 = http2;
	}

	

}
