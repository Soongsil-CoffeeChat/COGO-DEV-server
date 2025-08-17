package com.soongsil.CoffeeChat.domain.mentor.entity;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;
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
@ToString(of = {"id", "part", "club"})
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_id")
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private PartEnum part;

    @Column
    @Enumerated(EnumType.STRING)
    private ClubEnum club;

    @OneToOne(mappedBy = "mentor", fetch = FetchType.LAZY)
    private User user;

    @Builder.Default
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_introduction", referencedColumnName = "introduction_id")
    private Introduction introduction = new Introduction();

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PossibleDate> possibleDates = new HashSet<>();

    public void addPossibleDate(PossibleDate possibleDate) {
        this.possibleDates.add(possibleDate);
    }
}
