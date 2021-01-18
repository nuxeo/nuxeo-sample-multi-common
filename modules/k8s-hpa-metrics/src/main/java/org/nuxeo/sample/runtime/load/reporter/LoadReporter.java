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
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.dropwizard.metrics5.Counter;
import io.dropwizard.metrics5.Gauge;
import io.dropwizard.metrics5.Histogram;
import io.dropwizard.metrics5.Meter;
import io.dropwizard.metrics5.MetricFilter;
import io.dropwizard.metrics5.MetricName;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.metrics5.ScheduledReporter;
import io.dropwizard.metrics5.SlidingTimeWindowArrayReservoir;
import io.dropwizard.metrics5.Timer;

public class LoadReporter extends ScheduledReporter {

    private static final Logger log = LogManager.getLogger(LoadReporter.class);

    public static class LoadGauge implements Gauge<Number> {

        protected Number load;

        @Override
        public Number getValue() {
            return load;
        }

    }

    protected LoadGauge gauge;

    protected int done;

    protected int running;

    protected int scheduled;

    protected SlidingTimeWindowArrayReservoir throughput = new SlidingTimeWindowArrayReservoir(300, TimeUnit.SECONDS);

    protected long period;

    protected TimeUnit periodUnit;

    public LoadGauge getGauge() {
        return gauge;
    }

    public LoadReporter(MetricRegistry registry, String name, MetricFilter filter) {
        super(registry, name, filter, TimeUnit.SECONDS, TimeUnit.SECONDS);
        this.gauge = new LoadGauge();

    }

    @Override
    public void start(long initialDelay, long period, TimeUnit unit) {
        this.period = period;
        this.periodUnit = unit;
        super.start(initialDelay, period, unit);
    }

    @Override
    public void report(SortedMap<MetricName, Gauge> gauges, SortedMap<MetricName, Counter> counters,
            SortedMap<MetricName, Histogram> histograms, SortedMap<MetricName, Meter> meters,
            SortedMap<MetricName, Timer> timers) {

        long lag = 0;
        long latency = 0;

        for (Map.Entry<MetricName, Gauge> entry : gauges.entrySet()) {

            if (entry.getKey().getKey().equals("nuxeo.works.global.queue.canceled")
                    || entry.getKey().getKey().equals("nuxeo.works.global.queue.completed")) {
                Number v = (Number) entry.getValue().getValue();
                done += v.intValue();
            } else if (entry.getKey().getKey().equals("nuxeo.works.global.queue.running")) {
                Number v = (Number) entry.getValue().getValue();
                running += v.intValue();
            } else if (entry.getKey().getKey().equals("nuxeo.works.global.queue.scheduled")) {
                Number v = (Number) entry.getValue().getValue();
                scheduled += v.intValue();
            } else if (entry.getKey().getKey().equals("nuxeo.streams.global.stream.group.lag")) {
                Number v = (Number) entry.getValue().getValue();
                lag = Long.max(lag, v.longValue());
            } else if (entry.getKey().getKey().equals("nuxeo.streams.global.stream.group.latency")) {
                Number v = (Number) entry.getValue().getValue();
                latency = Long.max(latency, v.longValue());
            }

        }
        // double wload = computeWorkersLoad(done, scheduled, running);
        double sload = computeStreamLoad(lag, latency);

        log.info(" Computed load {}", sload);

        gauge.load = sload;

    }

    protected double computeStreamLoad(long lag, long latency) {

        log.info("\n lag: {} latency: {}", lag, latency);
        return Double.max(lag / 20.0, latency / (1000 * 5 * 60.0));
    }

    protected double computeWorkersLoad(int newDone, int newScheduled, int newRunning) {

        int deltaDone = newDone - this.done;
        int deltaScheduled = newScheduled - this.scheduled;

        done = newDone;
        running = newRunning;
        scheduled = newScheduled;
        if (deltaDone >= 0) {
            throughput.update(deltaDone);
        }

        log.info("\n done: {} sheduled: {} running: {} -- ", newDone, newScheduled, newRunning);
        if (newRunning == 0) {
            return 0;
        }

        double ratio = 1;
        if (deltaScheduled > 1) {
            ratio = 1.25;
        }
        double meanThroughputPerMinute = (60 / period) * throughput.getSnapshot().getMean();

        double load = 0;
        if (meanThroughputPerMinute > 0) {
            load = (ratio * newScheduled) / (meanThroughputPerMinute);
        }
        log.info("Load = {}", load);
        return load;
    }

}
