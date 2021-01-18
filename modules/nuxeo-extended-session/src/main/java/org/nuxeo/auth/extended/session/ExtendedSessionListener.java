/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Nuxeo
 */

package org.nuxeo.auth.extended.session;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.kv.KeyValueService;
import org.nuxeo.runtime.kv.KeyValueStore;

/**
 * Intercepts Login/logout events to manage the Extended Session.
 *
 * @since 0.1
 */
public class ExtendedSessionListener implements EventListener {

	private static final Log log = LogFactory.getLog(ExtendedSessionListener.class);

	@Override
	public void handleEvent(Event event) {
		try {
			if (event.getName().equals("loginSuccess")) {
				createOrExtendAuthSession(event.getContext().getPrincipal());
			} else if (event.getName().equals("logout")) {
				clearAuthSession();
			}
		} catch (Exception e) {
			log.error("Error while processing event " + event.getName(), e);
		}
	}

	protected void createOrExtendAuthSession(Principal principal) {
		KeyValueService keyValueService = Framework.getService(KeyValueService.class);
		KeyValueStore sessionCache = keyValueService.getKeyValueStore(SessionConstants.EX_SESSION_CACHE_NAME);
		String key = SessionIdFilter.getSessionId();
		String principalName = sessionCache.getString(key);
		if (principalName == null) {
			long ttl = Long.parseLong(Framework.getProperty(SessionConstants.EX_SESSION_CACHE_TTL,
					SessionConstants.EX_SESSION_CACHE_DEFAULT_TTL));
			sessionCache.put(key, principal.getName(), ttl);
		}
	}

	protected void clearAuthSession() {
		KeyValueStore sessionCache = Framework.getService(KeyValueService.class)
				.getKeyValueStore(SessionConstants.EX_SESSION_CACHE_NAME);
		String key = SessionIdFilter.getSessionId();
		sessionCache.put(key, (byte[]) null);
	}

}
