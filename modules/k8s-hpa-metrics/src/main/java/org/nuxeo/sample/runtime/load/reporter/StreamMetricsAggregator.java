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
package org.nuxeo.sample.runtime.load.reporter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.nuxeo.runtime.metrics.AbstractMetricsReporter;

import io.dropwizard.metrics5.MetricAttribute;
import io.dropwizard.metrics5.MetricFilter;
import io.dropwizard.metrics5.MetricRegistry;

public class StreamMetricsAggregator extends AbstractMetricsReporter {

    protected static final MetricFilter STREAM_METRICS_FILTER = (name, metric) -> {
        String metricName = name.getKey();
        if (metricName.startsWith("nuxeo.stream")) {
            return true;
        } else if (metricName.startsWith("nuxeo.works")) {
            return true;
        }
        return false;
    };

    protected LoadReporter reporter;

    @Override
    public void init(long pollInterval, Map<String, String> options) {
        super.init(pollInterval, options);
    }

    @Override
    public void start(MetricRegistry registry, MetricFilter filter, Set<MetricAttribute> deniedExpansions) {

        reporter = new LoadReporter(registry, "worker-load-reporter", STREAM_METRICS_FILTER);

        registry.register(MetricRegistry.name("nuxeo", "async", "load"), reporter.getGauge());

        reporter.start(getPollInterval(), TimeUnit.SECONDS);
        reporter.report();
    }

    @Override
    public void stop() {
        reporter.stop();
    }

}
