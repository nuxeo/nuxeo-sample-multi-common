/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     Nuxeo
 */
package org.nuxeo.auth.extended.session.tests;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.ServletContainerTransactionalFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

/**
 * @since 0.1
 */

@Features({ ServletContainerTransactionalFeature.class, PlatformFeature.class })
@Deploy("org.nuxeo.ecm.platform.login")
@Deploy("org.nuxeo.ecm.platform.web.common:OSGI-INF/authentication-framework.xml")
@Deploy({ "nuxeo.extended.session", "nuxeo.extended.session:test-authentication-config.xml" ,  "nuxeo.extended.session:embedded-servletcontainer-config.xml" })
public class HttpServerContainerWithAuthFeature  implements RunnerFeature {

}
