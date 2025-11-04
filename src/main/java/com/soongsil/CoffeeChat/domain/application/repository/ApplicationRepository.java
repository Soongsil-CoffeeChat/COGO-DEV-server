package com.soongsil.CoffeeChat.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @EntityGraph(attributePaths = {"mentee.user", "mentor.user", "possibleDate"})
    @Query(
            """
            select distinct a from Application a
            where (a.mentee.user.username= :username or a.mentor.user.username= :username)
                and (:status is null or a.applicationStatus= :status)
            order by a.id desc
            """)
    List<Application> findByUserNameAndOptionalStatus(
            @Param("username") String username, @Param("status") ApplicationStatus status);
}
