package com.soongsil.CoffeeChat.domain.repository.PossibleDate;

import java.util.List;

import com.soongsil.CoffeeChat.domain.dto.PossibleDateResponse.*;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateCreateResponse> getPossibleDatesByUsername(String username);
}
