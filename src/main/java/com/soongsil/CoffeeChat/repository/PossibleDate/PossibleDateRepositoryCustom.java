package com.soongsil.CoffeeChat.repository.PossibleDate;

import java.util.List;

import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetDto;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateCreateGetDto> getPossibleDatesByUsername(String username);
}
