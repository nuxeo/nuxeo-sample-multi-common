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
package org.nuxeo.sample.hpa.tests;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.stream.RuntimeStreamFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ RuntimeStreamFeature.class, CoreFeature.class })
@Deploy({ "org.nuxeo.runtime.metrics", "org.nuxeo.k8s.metrics:streamworkmanager.xml", "org.nuxeo.ecm.core.management",
        "org.nuxeo.k8s.metrics", "org.nuxeo.k8s.metrics:metrics-test-config.xml" })
@Ignore("Missing assertions")
public class TestMetrics {

    @Inject
    protected WorkManager wm;

    @BeforeClass
    public static void init() {
        Framework.getProperties().setProperty("nuxeo.stream.work.enabled", "true");
    }

    @Test
    public void test() throws InterruptedException {

        Thread.sleep(10000);
        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 8; j++) {
                wm.schedule(new SleepyWork(15000));
            }
            Thread.sleep(10000);

        }
    }

}
