package com.soongsil.CoffeeChat.repository.PossibleDate;

import java.util.List;

import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;

public interface PossibleDateRepositoryCustom {
	List<PossibleDateCreateGetResponseDto> getPossibleDatesByUsername(String username);
}
