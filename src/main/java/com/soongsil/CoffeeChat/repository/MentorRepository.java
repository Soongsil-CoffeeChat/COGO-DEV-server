package com.soongsil.CoffeeChat.repository;

import com.soongsil.CoffeeChat.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Mentor findByUsername(String username);
}
