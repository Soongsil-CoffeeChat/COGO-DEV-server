package com.soongsil.CoffeeChat.entity;

import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
//@DiscriminatorValue("mentor")
//@PrimaryKeyJoinColumn(name = "mentor_id")
public class Mentor{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "mentor_id")
    private Long id;

    @Column(name = "phone_num")
    private String phoneNum;

    @Column
    private String birth;

    @Column
    private String part;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Club> clubs = new HashSet<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PossibleDate> possibleDates = new HashSet<>();


    @Builder
    public Mentor(String phoneNum, String birth, String part){
        this.phoneNum=phoneNum;
        this.birth=birth;
        this.part=part;
    }
    public static Mentor from(CreateMentorRequest dto){
        return Mentor.builder()
                .phoneNum(dto.getPhoneNum())
                .birth(dto.getBirth())
                .part(dto.getPart())
                .build();
    }
}
