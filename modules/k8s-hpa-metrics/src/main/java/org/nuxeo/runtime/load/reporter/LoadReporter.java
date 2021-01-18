package org.nuxeo.runtime.load.reporter;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

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
	
	public void start(long initialDelay, long period, TimeUnit unit) {
		this.period=period;
		this.periodUnit=unit;
		super.start(initialDelay, period, unit);		
	}
	    
	@Override
	public void report(SortedMap<MetricName, Gauge> gauges, SortedMap<MetricName, Counter> counters,
			SortedMap<MetricName, Histogram> histograms, SortedMap<MetricName, Meter> meters,
			SortedMap<MetricName, Timer> timers) {
		

			int done=0;
			int scheduled=0;
			int running=0;
			
			long lag=0;
			long latency=0;
			
		  	for (Map.Entry<MetricName, Gauge> entry : gauges.entrySet()) {
		  	 
		  		if (entry.getKey().getKey().equals("nuxeo.works.global.queue.canceled") || entry.getKey().getKey().equals("nuxeo.works.global.queue.completed")) {		  		
		  			Number v = (Number) entry.getValue().getValue();		
		  			done += v.intValue();
		  		}
		  		else if (entry.getKey().getKey().equals("nuxeo.works.global.queue.running") ) {		  		
		  			Number v = (Number) entry.getValue().getValue();		
			  		running += v.intValue();
		  		}
		  		else if (entry.getKey().getKey().equals("nuxeo.works.global.queue.scheduled") ) {		  		
			  		Number v = (Number) entry.getValue().getValue();		
			  		scheduled += v.intValue();
		  		}
		  		else if (entry.getKey().getKey().equals("nuxeo.streams.global.stream.group.lag") ) {		  		
			  		Number v = (Number) entry.getValue().getValue();		
			  		lag = Long.max(lag, v.longValue());
		  		}
		  		else if (entry.getKey().getKey().equals("nuxeo.streams.global.stream.group.latency") ) {		  		
			  		Number v = (Number) entry.getValue().getValue();		
			  		latency = Long.max(latency, v.longValue());
		  		}		  				  		
	        	     
		  	}
	        //double wload = computeWorkersLoad(done, scheduled, running);
	        double sload = computeStreamLoad(lag, latency);
	        
	        System.out.println(" Computed load " + sload);
	        
	        gauge.load=sload;

	    
	}
	
	protected double computeStreamLoad(long lag, long latency) {		
		
		System.out.printf("\n lag: %s latency: %s ", lag, latency);		
		return Double.max(lag/20.0, latency / (1000* 5*60.0));
	}
		
	protected double computeWorkersLoad(int newDone, int newScheduled, int newRunning) {
		
		int deltaDone = newDone - this.done;
		int deltaScheduled = newScheduled - this.scheduled;
		
	    done = newDone;
        running=newRunning;
        scheduled=newScheduled;
        if (deltaDone>=0) {
        	throughput.update(deltaDone);		
        }
        
		System.out.printf("\n done: %s sheduled: %s running: %s -- ", newDone, newScheduled, newRunning);		
		if (newRunning==0) {
			return 0;
		}
		
		double ratio = 1;
		if (deltaScheduled>1) {
			ratio=1.25;
		}
		double meanThroughputPerMinute = (60/period) * throughput.getSnapshot().getMean();
	
		double load=0;		
		if (meanThroughputPerMinute> 0) {
			load = (ratio*newScheduled ) / (meanThroughputPerMinute);
		}
		System.out.println("Load = " + load);
		return load;
	}
	

}
