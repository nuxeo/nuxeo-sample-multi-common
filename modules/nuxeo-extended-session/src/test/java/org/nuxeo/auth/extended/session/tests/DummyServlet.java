package org.nuxeo.auth.extended.session.tests;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nuxeo.ecm.platform.ui.web.auth.NuxeoSecuredRequestWrapper;

public class DummyServlet extends HttpServlet {

	
	@Override
	public void init(ServletConfig config) throws ServletException {		
		super.init(config);
		System.out.println("Starting the Dummy Servlet");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		

		String uri = req.getRequestURI();
		
		if (uri.startsWith("/sayHello")) {
			sayHello(req,resp);
		} else if (uri.startsWith("/killSession")) {
			invalidateSession(req);
		} 
		else {
			System.out.println(" requested : " + uri);			
		}
		
		resp.setStatus(200);
	}

	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String uri = req.getRequestURI();	
		
		if (uri.startsWith("/login")) {
			System.out.println("login!");
			sayHello(req,resp);
		} 
		else {
			System.out.println("POST requested : " + uri);			
		}
		
		resp.setStatus(200);;
	}

	protected void invalidateSession(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session!=null) {
			session.invalidate();
		}
	}
	
	protected void sayHello(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		 if (req instanceof NuxeoSecuredRequestWrapper) {
			Principal principal = ((NuxeoSecuredRequestWrapper)req).getUserPrincipal();
			resp.getWriter().append("Hello " + principal.getName());
			resp.flushBuffer();
		 } else {
			resp.getWriter().append("No body");
			resp.flushBuffer();
		 }
	}
	
	
}
