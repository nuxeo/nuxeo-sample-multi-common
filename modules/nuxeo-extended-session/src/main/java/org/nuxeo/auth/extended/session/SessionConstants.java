/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Nuxeo
 */

package org.nuxeo.auth.extended.session;

/**
 * Constants used by the Extended Session system.
 *
 * @since 0.1
 */
public class SessionConstants {

    private SessionConstants() {
        // Hide implicit constructor
    }

    public static final String EX_SESSION_COOKIE_NAME = "NXExtendedSession";

    public static final String EX_SESSION_CACHE_NAME = "extendedSession-cache";

    public static final String EX_SESSION_CACHE_TTL = "nuxeo.extended.session.cache.ttl";

    public static final String EX_SESSION_CACHE_DEFAULT_TTL = "18000"; // 5 hours
}
