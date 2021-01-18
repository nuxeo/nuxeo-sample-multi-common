package org.nuxeo.sample.tests.service;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.sample.service.SampleService;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("org.nuxeo.sample.operation.nuxeo-customer-project-sample-core")
public class TestSampleService {

    @Inject
    protected SampleService sampleservice;

    @Test
    public void testService() {
        assertNotNull(sampleservice);
    }
}
