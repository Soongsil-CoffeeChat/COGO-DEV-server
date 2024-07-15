package com.soongsil.CoffeeChat.repository.PossibleDate;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;

import java.util.List;

public interface PossibleDateRepositoryCustom {
    List<PossibleDateRequestDto> getPossibleDatesByUsername(String username);
}
