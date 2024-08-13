package com.soongsil.CoffeeChat.repository.PossibleDate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetDto;
import com.soongsil.CoffeeChat.entity.PossibleDate;

public interface PossibleDateRepository extends JpaRepository<PossibleDate, Long>, PossibleDateRepositoryCustom {
	List<PossibleDateCreateGetDto> getPossibleDatesById(Long mentorId);
}
