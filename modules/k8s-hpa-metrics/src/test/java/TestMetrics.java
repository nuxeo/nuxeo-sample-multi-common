import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.ecm.core.work.SleepWork;
import org.nuxeo.ecm.core.work.api.Work;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.core.work.api.WorkSchedulePath;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.stream.RuntimeStreamFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({RuntimeStreamFeature.class, CoreFeature.class})
@Deploy({"org.nuxeo.runtime.metrics","org.nuxeo.k8s.metrics:streamworkmanager.xml", "org.nuxeo.ecm.core.management", "org.nuxeo.k8s.metrics", "org.nuxeo.k8s.metrics:metrics-test-config.xml"})
public class TestMetrics {

	@Inject
	protected WorkManager wm;
	
	@BeforeClass
	public static void  init() {
		Framework.getProperties().setProperty("nuxeo.stream.work.enabled", "true");
	}
	
	@Test
	public void test() throws Exception {

		Thread.sleep(10000);		
		for (int i = 0; i < 10; i++) {
			
			for (int j = 0; j < 8; j++) {
				wm.schedule(new SleepWork(15000));
			}
			Thread.sleep(10000);
			
		}
	}
	
}
