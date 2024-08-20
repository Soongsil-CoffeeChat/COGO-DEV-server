package com.soongsil.CoffeeChat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PerformanceResult {
    private List<Double> averageTimesPerThread;
    private double overallAverageTime;

    public PerformanceResult(List<Double> averageTimesPerThread, double overallAverageTime) {
        this.averageTimesPerThread = averageTimesPerThread;
        this.overallAverageTime = overallAverageTime;
    }

    // Getters and Setters
}

