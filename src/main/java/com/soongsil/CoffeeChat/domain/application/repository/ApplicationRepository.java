package com.soongsil.CoffeeChat.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @EntityGraph(attributePaths = {"metee.user", "mentor.user"})
    @Query(
            value =
                    """
            select a from Application a
            join a.mentee me
            join me.user meu
            join a.mentor mo
            join mo.user mou
            where (meu.username= :username or mou.uername= :username)
                and (:status is null or a.status= :status)
            order by a.id desc
            """,
            nativeQuery = true)
    List<Application> findByUserNameAndOptionalStatus(
            @Param("username") String username, @Param("status") ApplicationStatus status);
}
