package com.soongsil.CoffeeChat.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.enums.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findApplicationByMentor(Mentor mentor);

    List<Application> findApplicationByMentee(Mentee mentee);

    List<Application> findByPossibleDateAndAccept(
            PossibleDate possibleDate, ApplicationStatus accept);

    @Modifying
    @Transactional
    @Query(
            "DELETE FROM Application a "
                    + "WHERE a.accept = 'UNMATCHED' "
                    + "AND a.possibleDate.id IN ("
                    + "    SELECT pd.id "
                    + "    FROM PossibleDate pd "
                    + "    WHERE pd.date < CURRENT_DATE "
                    + "       OR (pd.date = CURRENT_DATE AND pd.startTime < CURRENT_TIME)"
                    + ")")
    void deleteExpiredApplications();
}
