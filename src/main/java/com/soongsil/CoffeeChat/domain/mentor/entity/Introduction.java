package com.soongsil.CoffeeChat.domain.mentor.entity;

import jakarta.persistence.*;

import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest.MentorIntroductionUpdateRequest;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Introduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduction_id")
    private Long id;

    @Column private String title;

    @Column private String description;

    @Column private String answer1;

    @Column private String answer2;

    public void updateIntroduction(MentorIntroductionUpdateRequest dto) {
        dto.getTitle().ifPresent(this::setTitle);
        dto.getDescription().ifPresent(this::setDescription);
        dto.getAnswer1().ifPresent(this::setAnswer1);
        dto.getAnswer2().ifPresent(this::setAnswer2);
    }
}
