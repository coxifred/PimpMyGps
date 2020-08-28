package pimpmygps.webserver.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pimpmygps.webserver.User;



public class FilterAuth implements javax.servlet.Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
				User requester = (User) request.getAttribute("USER");
					if (requester != null) {
						
					}
					
					
					HttpServletRequest httpRequest = (HttpServletRequest) request;
					HttpServletResponse httpResponse = (HttpServletResponse) response;
			        HttpSession session = httpRequest.getSession(false);
			        User aUser=null;
			        if ( session != null )
			        {
			        	aUser=(User) session.getAttribute("USER");
			        }
			        
			        boolean isLoggedIn = (session != null && session.getAttribute("USER") != null);
			 
			        String loginURI = httpRequest.getContextPath() + "index.html";
			 
			        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
			 
			        boolean isLoginPage = httpRequest.getRequestURI().endsWith("index.html");
			 
			        if (isLoggedIn && (isLoginRequest || isLoginPage)) {
			            // the admin is already logged in and he's trying to login again
			            // then forwards to the admin's homepage
			        	if ( aUser != null && aUser.getName().equals("admin"))
			        			{
			        			RequestDispatcher dispatcher = request.getRequestDispatcher("main_admin.html");
			        			dispatcher.forward(request, response);
			        			} else if ( aUser != null && ! aUser.getName().equals("admin"))
			        			{
			        				RequestDispatcher dispatcher = request.getRequestDispatcher("main.html");
				        			dispatcher.forward(request, response);
			        			}
			        			
			 
			        } else if (isLoggedIn ) {
			            // continues the filter chain
			            // allows the request to reach the destination
			            chain.doFilter(request, response);
			 
			        } else {
			            // the admin is not logged in, so authentication is required
			            // forwards to the Login page
			        	if ( isLoginPage )
			        	{
			        		 chain.doFilter(request, response);
			        	}else
			        	{
			        		httpResponse.setContentType("text/html");
			        	httpResponse.sendRedirect("/index.html");
			        	}
			 
			        }
			 
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		
	}

}
