package com.soongsil.CoffeeChat.repository;

import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.enums.ApplicationStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findApplicationByMentor(Mentor mentor);

    List<Application> findApplicationByMentee(Mentee mentee);

    List<Application> findByPossibleDateAndAccept(PossibleDate possibleDate, ApplicationStatus accept);

    @Modifying
    @Transactional
    @Query("DELETE FROM Application a " +
            "WHERE a.accept = 'UNMATCHED' " +
            "AND (a.possibleDate.date < CURRENT_DATE) ")
    void deleteExpiredApplications();
}
