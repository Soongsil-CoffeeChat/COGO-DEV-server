package com.soongsil.CoffeeChat.repository.PossibleDate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;

public interface PossibleDateRepository extends JpaRepository<PossibleDate, Long>, PossibleDateRepositoryCustom {
	List<PossibleDate> getPossibleDatesByMentorId(Long mentorId);

	@Modifying
	@Query("DELETE FROM PossibleDate pd WHERE pd.mentor = :mentor")
	void deleteAllByMentor(@Param("mentor") Mentor mentor);
}
