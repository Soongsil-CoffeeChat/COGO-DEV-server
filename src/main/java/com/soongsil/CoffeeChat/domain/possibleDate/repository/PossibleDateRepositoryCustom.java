package com.soongsil.CoffeeChat.domain.possibleDate.repository;

import java.util.List;

import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateDetailResponse;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateDetailResponse> getPossibleDatesByUsername(String username);
}
