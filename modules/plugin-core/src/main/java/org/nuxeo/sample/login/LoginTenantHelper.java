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
package org.nuxeo.sample.login;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.runtime.api.Framework;

public class LoginTenantHelper {

    private static final Logger log = LogManager.getLogger(LoginTenantHelper.class);

    private static final String DEFAULT_TENANT_ID = "Unknown";

    public static String getColor() {
        return getColor4Tenant(Framework.getProperty("nuxeo.tenantId"));
    }

    public static String getColor4Tenant(String tenantId) {
        String token = tenantId;
        if (StringUtils.isBlank(token)) {
            token = DEFAULT_TENANT_ID;
        }
        token = token.trim();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(token.getBytes());
            byte[] digest = md.digest();
            token = new String(digest, UTF_8);
        } catch (Exception e) {
            log.error(e, e);
        }
        int color = token.hashCode();

        return String.format("#%06X", (0xFFFFFF & color));
    }

}
