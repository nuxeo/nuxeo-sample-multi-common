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
package org.nuxeo.sample.login.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nuxeo.sample.login.LoginTenantHelper;

public class TestColors {

    protected static final String DEFAULT_COLOR = "#FCF4A8";

    @Test
    public void testColors() {
        assertEquals(DEFAULT_COLOR, LoginTenantHelper.getColor4Tenant(null));
        assertEquals(DEFAULT_COLOR, LoginTenantHelper.getColor4Tenant(""));
        assertEquals(DEFAULT_COLOR, LoginTenantHelper.getColor4Tenant(" "));
        assertEquals("#61A695", LoginTenantHelper.getColor4Tenant("tenant1"));
        assertEquals("#FEF95B", LoginTenantHelper.getColor4Tenant("tenant2"));
        assertEquals("#810403", LoginTenantHelper.getColor4Tenant("tenant3"));
    }

}
