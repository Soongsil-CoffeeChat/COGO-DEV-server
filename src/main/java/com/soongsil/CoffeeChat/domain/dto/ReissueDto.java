package com.soongsil.CoffeeChat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReissueDto {
    private String refreshToken;
    private String accessToken;
}
