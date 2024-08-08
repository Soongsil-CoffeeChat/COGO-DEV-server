package com.soongsil.CoffeeChat.entity;

import java.util.HashSet;
import java.util.Set;

import com.soongsil.CoffeeChat.dto.MentorDto;
import com.soongsil.CoffeeChat.enums.MentorPart;

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
//@DiscriminatorValue("mentor")
//@PrimaryKeyJoinColumn(name = "mentor_id")
public class Mentor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mentor_id")
	private Long id;

	@Column
	@Enumerated(EnumType.STRING)
	private MentorPart part;

	@Column
	private int club;

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
	public Mentor(int club, String part) {
		this.club = club;
		this.part = MentorPart.valueOf(part);
	}

	public static Mentor from(MentorDto dto) {
		return Mentor.builder()
			.club(dto.getClub())
			.part(MentorPart.valueOf(dto.getPart()))
			.build();
	}

	public void addPossibleDate(PossibleDate possibleDate) {
		this.possibleDates.add(possibleDate);
	}
}
