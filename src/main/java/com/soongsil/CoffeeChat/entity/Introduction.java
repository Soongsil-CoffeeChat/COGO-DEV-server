package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Introduction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduction_id")
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String answer1;

    @Column
    private String answer2;

}
