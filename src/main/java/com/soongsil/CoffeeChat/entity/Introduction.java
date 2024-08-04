package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Introduction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "introduction_id")
    private Long id;

    @Column
    private String answer1;

    @Column
    private String answer2;

}
