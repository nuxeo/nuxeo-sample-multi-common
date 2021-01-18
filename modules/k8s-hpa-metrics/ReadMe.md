### About

`k8s-hpa-metrics` is a Nuxeo plugin that expose metrics for K8S.

### Why specific metrics

#### Metadata

Nuxeo already exposes a lot of metrics (i.e. DataDog or StackDriver).

However, when using Nuxeo in K8S (GKE in that case) in order to be able to use Nuxeo Metrics to control AutoScaling (using HorizontalPodAutoscaler/HPA) the metrics need to be properly associated with labels corresponding to the right pod:

 - pod name
 - pod namespace
 - cluster name
 - project name
 - region

In addition of the labels, the metrics need to be associated to the right type of resources.
In StackDriver the default resource type if `k8s_container`, but the GKE HPA expect `k8s_pod`.

This modules takes care of setting the right meta-data for the metrics.

#### Async load

The default HPA relies on a single metrics and a threshold level.

For asynchronous workload, we can not only rely on metrics like CPU and memory.

The current implementation leverage metrics exposed by the `StreamMetricsNuxeoReporter` and aggregate them to expose a `nuxeo.async.load` metrics based on work lag and work latency.

### Deployment

Once the plugin is deployed you need 


Activate the [StreamMetricsNuxeoReporter](https://github.com/nuxeo/nuxeo/blob/master/server/nuxeo-nxr-server/src/main/resources/templates/common-base/nxserver/config/metrics-config.xml#L113)

    metrics.stream.enabled=true

Configure the K8S reporter

    nuxeo.stackdriver.forceResourceType=k8s_pod

