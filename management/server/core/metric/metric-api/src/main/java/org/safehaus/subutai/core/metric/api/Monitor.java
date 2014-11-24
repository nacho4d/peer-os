package org.safehaus.subutai.core.metric.api;


import java.util.Set;

import org.safehaus.subutai.core.environment.api.helper.Environment;


/**
 * Interface for monitor
 */
public interface Monitor
{
    /**
     * Returns current metrics of containers belonging to the given environment
     *
     * @param environment environment which containers' metrics to return
     *
     * @return set of metrics, one per each container within an environment
     */
    public Set<ContainerHostMetric> getContainerHostsMetrics( Environment environment ) throws MonitorException;


    /**
     * Returns current metrics of local resource hosts
     *
     * These metrics are to be used by container placement strategies in the context of local peer and for heartbeats to
     * HUB.
     *
     * Resource hosts are "visible" to the local peer only
     *
     * @return set of metrics, one per each resource host within the local peer
     */
    public Set<ResourceHostMetric> getResourceHostsMetrics() throws MonitorException;


    /**
     * Enables {@code AlertListener} to be triggered if thresholds on some containers within the given environment are
     * exceeded
     *
     * @param alertListener alertListener  to trigger
     * @param environment environment to monitor
     */

    public void startMonitoring( AlertListener alertListener, Environment environment ) throws MonitorException;

    /**
     * Disables {@code AlertListener} to be triggered for the given environment
     *
     * @param alertListener alertListener  to trigger
     * @param environment environment to monitor
     */
    public void stopMonitoring( AlertListener alertListener, Environment environment ) throws MonitorException;

    /**
     * This method is called by REST endpoint from local peer indicating that some container hosted locally is under
     * stress.
     *
     * @param alertMetric - body of alert in JSON
     */
    public void alert( String alertMetric ) throws MonitorException;


    public void addAlertListener( AlertListener listener );

    public void removeAlertListener( AlertListener listener );
}
