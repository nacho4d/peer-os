/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.api.lxcmanager;

import java.util.Collections;
import java.util.Map;
import org.safehaus.kiskis.mgmt.api.monitor.Metric;

/**
 *
 * @author dilshat
 */
public class ServerMetric {

    private int freeHddMb;
    private int freeRamMb;
    private int cpuLoadPercent;
    private int numOfProcessors;
    private int numOfLxcs;
    private final Map<Metric, Double> averageMetrics;

    public ServerMetric(int freeHddMb, int freeRamMb, int cpuLoadPercent, int numOfProcessors, Map<Metric, Double> averageMetrics) {
        this.freeHddMb = freeHddMb;
        this.freeRamMb = freeRamMb;
        this.cpuLoadPercent = cpuLoadPercent;
        this.numOfProcessors = numOfProcessors;
        this.averageMetrics = averageMetrics;
    }

    public Map<Metric, Double> getAverageMetrics() {
        return Collections.unmodifiableMap(averageMetrics);
    }

    public Double getAverageMetric(Metric metricKey) {
        return averageMetrics.get(metricKey);
    }

    public int getNumOfProcessors() {
        return numOfProcessors;
    }

    public void setNumOfProcessors(int numOfProcessors) {
        this.numOfProcessors = numOfProcessors;
    }

    public int getFreeHddMb() {
        return freeHddMb;
    }

    public void setFreeHddMb(int freeHddMb) {
        this.freeHddMb = freeHddMb;
    }

    public int getFreeRamMb() {
        return freeRamMb;
    }

    public void setFreeRamMb(int freeRamMb) {
        this.freeRamMb = freeRamMb;
    }

    public int getCpuLoadPercent() {
        return cpuLoadPercent;
    }

    public void setCpuLoadPercent(int cpuLoadPercent) {
        this.cpuLoadPercent = cpuLoadPercent;
    }

    public int getNumOfLxcs() {
        return numOfLxcs;
    }

    public void setNumOfLxcs(int numOfLxcs) {
        this.numOfLxcs = numOfLxcs;
    }

}
