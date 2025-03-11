package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.soongsil.CoffeeChat.dto.MentorIntroductionUpdateRequestDto;

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

    public void updateIntroduction(MentorIntroductionUpdateRequestDto dto) {
        dto.getTitle().ifPresent(this::setTitle);
        dto.getDescription().ifPresent(this::setDescription);
        dto.getAnswer1().ifPresent(this::setAnswer1);
        dto.getAnswer2().ifPresent(this::setAnswer2);
    }
}
