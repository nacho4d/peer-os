package org.safehaus.subutai.core.metric.impl;


import org.safehaus.subutai.common.command.RequestBuilder;
import org.safehaus.subutai.common.peer.Host;
import org.safehaus.subutai.common.metric.MetricType;
import org.safehaus.subutai.core.metric.api.MonitoringSettings;


/**
 * Commands for Monitor
 */
public class Commands
{

    public RequestBuilder getCurrentMetricCommand( String hostname )
    {
        return new RequestBuilder( String.format( "subutai monitor %s", hostname ) );
    }


    public RequestBuilder getHistoricalMetricCommand( Host host, MetricType metricType )
    {
        return new RequestBuilder( String.format( "subutai monitor -q %s %s | grep e+", metricType.getName(), host.getHostname() ) );
    }


    public RequestBuilder getActivateMonitoringCommand( String hostname, MonitoringSettings monitoringSettings )
    {
        return new RequestBuilder( String.format(
                "subutai monitor -c all -p \" metricCollectionIntervalInMin:%s, maxSampleCount:%s, "
                        + "metricCountToAverageToAlert:%s, intervalBetweenAlertsInMin:%s, ramAlertThreshold:%s, "
                        + "cpuAlertThreshold:%s, diskAlertThreshold:%s \" %s",
                monitoringSettings.getMetricCollectionIntervalInMin(), monitoringSettings.getMaxSampleCount(),
                monitoringSettings.getMetricCountToAverageToAlert(), monitoringSettings.getIntervalBetweenAlertsInMin(),
                monitoringSettings.getRamAlertThreshold(), monitoringSettings.getCpuAlertThreshold(),
                monitoringSettings.getDiskAlertThreshold(), hostname ) );
    }


    public RequestBuilder getProcessResourceUsageCommand( String hostname, int pid )
    {
        return new RequestBuilder( String.format( "subutai monitor -i %s %s", pid, hostname ) );
    }
}
