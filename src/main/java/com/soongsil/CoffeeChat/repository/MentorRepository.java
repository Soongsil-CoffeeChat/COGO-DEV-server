package com.soongsil.CoffeeChat.repository;

import com.soongsil.CoffeeChat.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    //Mentor findByUsername(String username); 상속시 사용가능

    List<Mentor> findAllByPart(String part);
}
