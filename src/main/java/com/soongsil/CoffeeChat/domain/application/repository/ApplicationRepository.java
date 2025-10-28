package com.soongsil.CoffeeChat.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

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
                and (:status is null or a.status = :status)
            """)
    List<Application> findByUserIdAndOptionalStatus(
            @Param("userId") Long userId, @Param("status") ApplicationStatus status);

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
            where (uMentor.username= :username or uMentee.username= :username)
                and (:status is null or a.status = :status)
            """)
    List<Application> findByUserNameAndOptionalStatus(
            @Param("username") String username, @Param("status") ApplicationStatus status);
}
