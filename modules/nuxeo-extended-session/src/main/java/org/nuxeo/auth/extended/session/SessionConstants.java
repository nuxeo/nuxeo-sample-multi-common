/*
 * (C) Copyright 2021 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thierry Delprat
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
