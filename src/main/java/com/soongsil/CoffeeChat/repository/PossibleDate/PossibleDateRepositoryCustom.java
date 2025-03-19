package com.soongsil.CoffeeChat.repository.PossibleDate;

import java.util.List;

import com.soongsil.CoffeeChat.dto.PossibleDateResponse.*;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateCreateResponse> getPossibleDatesByUsername(String username);
}
