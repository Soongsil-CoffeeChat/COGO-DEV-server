package com.soongsil.CoffeeChat.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findApplicationByMentor(Mentor mentor);

    List<Application> findApplicationByMentee(Mentee mentee);

    List<Application> findByPossibleDateAndAccept(
            PossibleDate possibleDate, ApplicationStatus accept);

    @EntityGraph(
            attributePaths = {
                "mentor", "mentor.user",
                "mentee", "mentee.user"
            })
    @Query(
            """
            select a from Application a
            join a.mentor mentor
            join mentor.user uMentor
            join a.mentee mentee
            join mentee.user uMentee
            where (uMentor.id= :userId or uMentee.id= :userId)
                and (:status is null or a.accept = :status)
            """)
    List<Application> findByUserIdAndOptionalStatus(
            @Param("userId") Long userId, @Param("status") ApplicationStatus status);

    //    @Modifying
    //    @Transactional
    //    @Query(
    //            "DELETE FROM Application a "
    //                    + "WHERE a.accept = 'UNMATCHED' "
    //                    + "AND a.possibleDate.id IN ("
    //                    + "    SELECT pd.id "
    //                    + "    FROM PossibleDate pd "
    //                    + "    WHERE pd.date < CURRENT_DATE "
    //                    + "       OR (pd.date = CURRENT_DATE AND pd.startTime < CURRENT_TIME)"
    //                    + ")")
    //    void deleteExpiredApplications();

}
