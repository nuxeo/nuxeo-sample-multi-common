/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
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
 *     bdelbosc
 */
package org.nuxeo.runtime.metrics.reporter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.common.utils.DurationUtils;
import org.nuxeo.runtime.api.Framework;

import com.google.api.MonitoredResource;

import io.dropwizard.metrics5.MetricAttribute;
import io.dropwizard.metrics5.MetricFilter;
import io.dropwizard.metrics5.MetricRegistry;
import io.opencensus.common.Duration;
import io.opencensus.contrib.dropwizard5.DropWizardMetrics;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsConfiguration;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter;
import io.opencensus.metrics.LabelKey;
import io.opencensus.metrics.LabelValue;
import io.opencensus.metrics.Metrics;

/**
 * Reports metrics to Google Stackdriver.
 *
 * @since 11.4
 */
public class K8StackdriverReporter extends StackdriverReporter {

    private static final Logger log = LogManager.getLogger(K8StackdriverReporter.class);
    
    protected K8SEnv k8senv;	
    
    protected String getPodInfo(String attribute) {    	
    	String value = k8senv.getPodInfo(attribute);    	
    	if (options.containsKey(attribute)) {
    		value = options.get(attribute); 
    	}    	
    	if (value == null) {
			log.warn("Unable to find podInfo " + attribute);
		}
    	return value;    	
    }

    protected Map<LabelKey, LabelValue> getPodLabels(String projectId) {
    	    	
    	k8senv = new K8SEnv();
    	
    	Map<LabelKey, LabelValue> labels = new HashMap<>();
    	
    	String value = getPodInfo("pod_name");
    	if (value!=null) {
    		labels.put(
    			LabelKey.create("pod_name", "Pod name"),
    			LabelValue.create(value));
    	}

    	value = getPodInfo("namespace");
    	if (value!=null) {    	
	    	labels.put(
	    			LabelKey.create("namespace_name", "Namespace"),
	    			LabelValue.create(value));
    	}

    	if (projectId!=null) {    	
    		labels.put(
    			LabelKey.create("project_id", "ProjectId"),
    			LabelValue.create(projectId));
    	}
    	

    	value = getPodInfo("cluster_name");
    	if (value!=null) {    		    
    		labels.put(
    			LabelKey.create("cluster_name", "cluster_name"),
    			LabelValue.create(value));
    	}
    	
    	value = getPodInfo("cluster_location");
    	if (value!=null) {    		       	
    		labels.put(
    			LabelKey.create("location", "Cluster location"),
    			LabelValue.create(value));
    	}

    	for (LabelKey key : labels.keySet()) {
    		log.debug(key.getKey() + " : '"  + labels.get(key).getValue() + "'");
    	}
    	
    	return labels;
    	
    }
    
    protected Map<String, String> getLabelsAsMap(String projectId) {
    	Map<LabelKey, LabelValue> labels = getPodLabels(projectId);
    	Map<String, String> result = new HashMap<>();
    	for (LabelKey key : labels.keySet()) {
    		result.put(key.getKey(),labels.get(key).getValue());
    	}
    	return result;
    }
        
    
    @Override
    public void start(MetricRegistry registry, MetricFilter filter, Set<MetricAttribute> deniedExpansions) {
        log.warn("Creating Stackdriver metrics reporter");
        DropWizardMetrics registries = new DropWizardMetrics(Collections.singletonList(registry), filter);
        Metrics.getExportComponent().getMetricProducerManager().add(registries);
        Duration timeout = Duration.create(
                DurationUtils.parsePositive(options.get(TIMEOUT_OPTION), DEFAULT_TIMEOUT).getSeconds(), 0);
        String projectId = StackdriverTraceReporter.getGcpProjectId(options);
           
        String prefix = options.getOrDefault(PREFIX_OPTION, DEFAULT_PREFIX);
        Duration interval = Duration.fromMillis(getPollInterval() * 1000);
        
        // 'k8s_pod' vs 'k8s_container'
        String forceResourceType = Framework.getProperty("nuxeo.stackdriver.forceResourceType");
        
        MonitoredResource resource=null;
        if (forceResourceType!=null) {
        	 resource =
             	    MonitoredResource.newBuilder().setType(forceResourceType).putAllLabels(getLabelsAsMap(projectId)).build(); 	
        }
        
        StackdriverStatsConfiguration.Builder builder = StackdriverStatsConfiguration.builder()
                .setDeadline(timeout)
                .setProjectId(projectId)
                .setMetricNamePrefix(prefix)
                .setConstantLabels(getPodLabels(projectId))
                .setExportInterval(interval);
        
        if (resource!=null) {
        	builder.setMonitoredResource(resource);            
        }
        
        StackdriverStatsConfiguration configuration = builder.build();           
        try {
            StackdriverStatsExporter.createAndRegister(configuration);
        } catch (IOException e) {
            log.error("Fail to create a Stackdriver metric reporter for K8S", e);
            return;
        }
        activated = true;
    }

}
