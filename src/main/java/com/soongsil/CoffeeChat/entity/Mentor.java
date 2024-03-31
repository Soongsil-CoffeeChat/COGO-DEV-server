package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Mentor extends User{
    @Column
    private int grade;

    @Column
    private String major;

    @Column
    private String memo;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Club> clubs = new HashSet<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PossibleDate> possibleDates = new HashSet<>();
}
