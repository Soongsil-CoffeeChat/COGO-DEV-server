package com.soongsil.CoffeeChat.domain.possibleDate.repository;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PossibleDateRepository
        extends JpaRepository<PossibleDate, Long>, PossibleDateRepositoryCustom {
    List<PossibleDate> getPossibleDatesByMentorId(Long mentorId);

    @Modifying
    @Query("DELETE FROM PossibleDate pd WHERE pd.mentor = :mentor")
    void deleteAllByMentor(@Param("mentor") Mentor mentor);
}
