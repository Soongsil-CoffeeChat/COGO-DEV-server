package com.soongsil.CoffeeChat.domain.possibleDate.repository;

import java.util.List;

import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateCreateResponse;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateCreateResponse> getPossibleDatesByUsername(String username);
}
