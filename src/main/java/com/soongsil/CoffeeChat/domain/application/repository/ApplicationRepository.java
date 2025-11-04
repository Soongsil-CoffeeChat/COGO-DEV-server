package com.soongsil.CoffeeChat.domain.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("""
            select distinct a from Application a
            join fetch a.mentee me
            join fetch me.user meu
            join fetch a.mentor mo
            join fetch mo.user mou
            left join fetch a.possibleDate pd
            where (meu.username= :username or mou.username= :username)
                and (:status is null or a.status= :status)
            order by a.id desc
            """)
    List<Application> findByUserNameAndOptionalStatus(
            @Param("username") String username, @Param("status") ApplicationStatus status);
}
