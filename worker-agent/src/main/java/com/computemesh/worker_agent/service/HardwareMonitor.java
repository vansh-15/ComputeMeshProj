package com.computemesh.worker_agent.service;

import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Service
public class HardwareMonitor {

    private final SystemInfo systemInfo;
    private final CentralProcessor processor;
    private final GlobalMemory memory;
    private long[] previousCpuTicks;


    public HardwareMonitor() {
        this.systemInfo = new SystemInfo();
        this.processor = systemInfo.getHardware().getProcessor();
        this.memory = systemInfo.getHardware().getMemory();
        this.previousCpuTicks = processor.getSystemCpuLoadTicks();
    }

    public int getPhysicalCpuCores() {
        return processor.getPhysicalProcessorCount();
    }

    public int getLogicalCpuCores() {
        return processor.getLogicalProcessorCount();
    }

    public long getTotalMemory() {
        return memory.getTotal();
    }

    public long getAvailableMemory() {
        return memory.getAvailable();
    }

    public double getCpuUsage() {

        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(
                previousCpuTicks
        ) * 100;

        previousCpuTicks = processor.getSystemCpuLoadTicks();

        return Math.round(cpuUsage * 100.0) / 100.0;
    }

}
