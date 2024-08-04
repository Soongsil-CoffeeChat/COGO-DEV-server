package com.soongsil.CoffeeChat.entity;

import java.util.HashSet;
import java.util.Set;

import com.soongsil.CoffeeChat.dto.MentorDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(of = {"id","part", "club"})
//@DiscriminatorValue("mentor")
//@PrimaryKeyJoinColumn(name = "mentor_id")
public class Mentor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mentor_id")
	private Long id;

	@Column
	private String part;

	@Column
	private int club;


	@Builder.Default
	@OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Application> applications = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PossibleDate> possibleDates = new HashSet<>();

	@Builder
	public Mentor(int club, String part) {
		this.club=club;
		this.part = part;
	}

	public static Mentor from(MentorDto dto) {
		return Mentor.builder()
				.club(dto.getClub())
				.part(dto.getPart())
				.build();
	}

	public void addPossibleDate(PossibleDate possibleDate) {
		this.possibleDates.add(possibleDate);
	}
}
