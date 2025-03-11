package com.soongsil.CoffeeChat.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import com.soongsil.CoffeeChat.dto.MentorJoinRequestDto;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(of = {"id", "part", "club"})
// @DiscriminatorValue("mentor")
// @PrimaryKeyJoinColumn(name = "mentor_id")
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_introduction", referencedColumnName = "introduction_id")
    private Introduction introduction;

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PossibleDate> possibleDates = new HashSet<>();

    @Builder
    public Mentor(String club, String part) {
        this.club = ClubEnum.valueOf(club);
        this.part = PartEnum.valueOf(part);
    }

    public static Mentor from(MentorJoinRequestDto dto) {
        return Mentor.builder().club(dto.getClub()).part(dto.getPart()).build();
    }

    public void addPossibleDate(PossibleDate possibleDate) {
        this.possibleDates.add(possibleDate);
    }
}
