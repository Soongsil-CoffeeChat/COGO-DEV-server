package com.soongsil.CoffeeChat.entity;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mentee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentee_id")
    private Long id;

    @Column
    private String picture;
    //TODO: aws파지면 사진처리 해줘야됨

    @Column(name = "phone_num")
    private String phoneNum;

    @Column
    private String birth;

    @Column
    private int grade;

    @Column
    private String major;

    @Column
    private String memo;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Application> applications = new HashSet<>();

    @Builder
    public Mentee(String phoneNum, String birth, int grade, String major, String memo){
        this.phoneNum=phoneNum;
        this.birth=birth;
        this.grade=grade;
        this.major=major;
        this.memo=memo;
    }

    public static Mentee from(CreateMenteeRequest dto){
        return Mentee.builder()
                .phoneNum(dto.getPhoneNum())
                .birth(dto.getBirth())
                .grade(dto.getGrade())
                .major(dto.getMajor())
                .memo(dto.getMemo())
                .build();
    }

}
