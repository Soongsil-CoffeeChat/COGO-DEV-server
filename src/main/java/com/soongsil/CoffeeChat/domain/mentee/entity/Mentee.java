package com.soongsil.CoffeeChat.domain.mentee.entity;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @OneToOne(mappedBy = "mentee", fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Application> applications = new HashSet<>();
}
