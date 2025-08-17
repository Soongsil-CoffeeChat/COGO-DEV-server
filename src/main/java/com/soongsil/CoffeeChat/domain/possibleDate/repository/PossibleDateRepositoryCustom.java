package com.soongsil.CoffeeChat.domain.possibleDate.repository;

import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateCreateResponse;

import java.util.List;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateCreateResponse> getPossibleDatesByUsername(String username);
}
