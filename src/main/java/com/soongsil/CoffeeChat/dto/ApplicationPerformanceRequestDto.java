package com.soongsil.CoffeeChat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationPerformanceRequestDto {
    private ApplicationCreateRequest applicationCreateRequest;
    private PerformanceRequest performanceRequest;
    private int apiNum;
}
