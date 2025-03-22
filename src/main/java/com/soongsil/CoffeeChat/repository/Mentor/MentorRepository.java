package com.soongsil.CoffeeChat.repository.Mentor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.dto.MentorResponse.*;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.entity.enums.PartEnum;

public interface MentorRepository extends JpaRepository<Mentor, Long>, MentorRepositoryCustom {
    // Mentor findByUsername(String username); 상속시 사용가능

    List<Mentor> findAllByPart(PartEnum part);

    List<MentorListResponse> getMentorListByPart(PartEnum part); // 일반 join

    List<User> getMentorListByPartWithFetch(PartEnum part); // fetch join
}
