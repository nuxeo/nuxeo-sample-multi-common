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
package org.nuxeo.auth.extended.session.tests;

import org.nuxeo.ecm.core.test.ServletContainerTransactionalFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ ServletContainerTransactionalFeature.class, PlatformFeature.class })
@Deploy("org.nuxeo.ecm.platform.login")
@Deploy("org.nuxeo.ecm.platform.web.common:OSGI-INF/authentication-framework.xml")
@Deploy({ "nuxeo.extended.session", "nuxeo.extended.session:test-authentication-config.xml",
        "nuxeo.extended.session:embedded-servletcontainer-config.xml" })
public class HttpServerContainerWithAuthFeature implements RunnerFeature {

}
