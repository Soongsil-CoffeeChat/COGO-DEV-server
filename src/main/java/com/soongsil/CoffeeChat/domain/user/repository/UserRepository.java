package com.soongsil.CoffeeChat.domain.user.repository;

import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest.UserGetRequest;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    @Query(value = "select * from User where username = :username", nativeQuery = true)
    Optional<User> findByUsernameWithDeleted(@Param("username") String username);

    Optional<User> findByUsername(String username);

    User findByMentor(Mentor mentor);

    User findByMenteeId(Long menteeId);

    UserGetRequest findUserInfoByUsername(String username);
}
