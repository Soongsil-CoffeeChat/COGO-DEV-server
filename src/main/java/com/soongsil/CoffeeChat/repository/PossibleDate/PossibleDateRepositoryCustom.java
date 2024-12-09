package com.soongsil.CoffeeChat.repository.PossibleDate;

import java.util.List;

import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.entity.PossibleDate;

public interface PossibleDateRepositoryCustom {
	List<PossibleDateCreateGetResponseDto> getPossibleDatesByUsername(String username);
}
