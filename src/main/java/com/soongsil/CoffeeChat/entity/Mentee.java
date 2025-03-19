package com.soongsil.CoffeeChat.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import com.soongsil.CoffeeChat.dto.MenteeRequest.*;
import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Mentee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentee_id")
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PartEnum part;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Application> applications = new HashSet<>();
}
