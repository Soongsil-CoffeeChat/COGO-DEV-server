package com.soongsil.CoffeeChat.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceRequest {
    private int userCount;
    private int durationInSeconds;
    private int totalRequests;

    // Getters and setters
    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }
}
