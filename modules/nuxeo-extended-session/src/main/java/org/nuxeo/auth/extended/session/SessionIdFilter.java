/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Nuxeo
 */

package org.nuxeo.auth.extended.session;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;

/**
 * HttpFilter that manage Extended Session management. It is managed with a
 * ThreadLocal and the filter is responsible for setting and clearing the
 * ThreadLocal. Extended Session is controlled by a Cookie.
 *
 * @since 0.1
 */
public class SessionIdFilter implements Filter {

	private static ThreadLocal<String> extendedSessionContext;

	public static boolean useSecureCookie = true;

	@Override
	public void init(FilterConfig filterConfig) {
		extendedSessionContext = new ThreadLocal<>();
	}

	public static String getSessionId() {
		if (extendedSessionContext == null) {
			return null;
		}
		return extendedSessionContext.get();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String exsID = getOrCreateExtendedSessionID((HttpServletRequest) request, (HttpServletResponse) response);
		try {
			extendedSessionContext.set(exsID);
			chain.doFilter(request, response);
		} finally {
			extendedSessionContext.remove();
		}
	}

	public String getOrCreateExtendedSessionID(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			Optional<Cookie> cookie = Arrays.stream(cookies)
					.filter(element -> element.getName().equalsIgnoreCase(SessionConstants.EX_SESSION_COOKIE_NAME))
					.findAny();
			if (cookie.isPresent()) {
				return cookie.get().getValue();
			}
		}
		String id = UUID.randomUUID().toString();

		Cookie cookie = new Cookie(SessionConstants.EX_SESSION_COOKIE_NAME, id);
		cookie.setMaxAge(-1);
		cookie.setSecure(useSecureCookie);
		cookie.setPath(VirtualHostHelper.getContextPath(request));
		response.addCookie(cookie);
		return id;
	}

	@Override
	public void destroy() {
		// NOP
	}

}
