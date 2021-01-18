/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Nuxeo
 */

package org.nuxeo.auth.extended.session;

import static org.nuxeo.auth.extended.session.SessionConstants.EX_SESSION_CACHE_DEFAULT_TTL;
import static org.nuxeo.auth.extended.session.SessionConstants.EX_SESSION_CACHE_NAME;
import static org.nuxeo.auth.extended.session.SessionConstants.EX_SESSION_CACHE_TTL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;
import org.nuxeo.ecm.platform.ui.web.auth.interfaces.NuxeoAuthenticationPlugin;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.kv.KeyValueService;
import org.nuxeo.runtime.kv.KeyValueStore;

/**
 * Allows to automatically re-authenticate using a long running session that can be stored inside a Cache. This is
 * useful to allow authentication session to be shared across a Nuxeo Cluster.
 *
 * @since 0.1
 */
public class ExtendedSessionAuth implements NuxeoAuthenticationPlugin {

    private long ttl;

	private static final Log log = LogFactory.getLog(ExtendedSessionAuth.class);

    @Override
    public List<String> getUnAuthenticatedURLPrefix() {
        return Collections.emptyList();
    }

    @Override
    public Boolean handleLoginPrompt(HttpServletRequest request, HttpServletResponse response, String baseURL) {
    	return false;
    }

    @Override
    public UserIdentificationInfo handleRetrieveIdentity(HttpServletRequest request, HttpServletResponse response) {
        String sid = SessionIdFilter.getSessionId();
        if (sid == null) {
            log.debug("ExtendedSessionAuth NO sid provided");
            return null;
        }
        log.debug("ExtendedSessionAuth with sid = " + sid);
        KeyValueService keyValueService = Framework.getService(KeyValueService.class);
        KeyValueStore sessionCache = keyValueService.getKeyValueStore(EX_SESSION_CACHE_NAME);
        String principalName = sessionCache.getString(sid);
        if (principalName != null) {
            log.debug("ExtendedSessionAuth retrived user session from KV" );
            // refresh ttl
            sessionCache.put(sid, principalName, ttl);
            return new UserIdentificationInfo(principalName, principalName);
        } 
        log.debug("ExtendedSessionAuth no session found in KV!" );
        return null;
    }

    @Override
    public void initPlugin(Map<String, String> parameters) {
        log.debug("ExtendedSessionAuth Plugin initializing");
    	ttl = Long.parseLong(Framework.getProperty(EX_SESSION_CACHE_TTL, EX_SESSION_CACHE_DEFAULT_TTL));
    }

    @Override
    public Boolean needLoginPrompt(HttpServletRequest request) {
        return false;
    }

}
