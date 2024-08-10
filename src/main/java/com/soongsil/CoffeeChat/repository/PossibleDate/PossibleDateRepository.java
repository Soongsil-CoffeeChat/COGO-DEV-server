package com.soongsil.CoffeeChat.repository.PossibleDate;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.PossibleDate;

import java.util.List;

public interface PossibleDateRepository extends JpaRepository<PossibleDate, Long>, PossibleDateRepositoryCustom {
	List<PossibleDateRequestDto> getPossibleDatesById(Long mentorId);
}
