package com.soongsil.CoffeeChat.domain.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

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
