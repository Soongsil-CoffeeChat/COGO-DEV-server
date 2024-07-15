package com.soongsil.CoffeeChat.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.soongsil.CoffeeChat.dto.CreateMentorRequest;

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
@ToString(of = {"id", "picture", "part"})
//@DiscriminatorValue("mentor")
//@PrimaryKeyJoinColumn(name = "mentor_id")
public class Mentor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mentor_id")
	private Long id;

	@Column
	private String picture;

	@Column(name = "phone_num")
	private String phoneNum;

	@Column
	private String birth;

	@Column
	private String part;

	@Column
	private String field;

	@Builder.Default
	@OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Application> applications = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Club> clubs = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<PossibleDate> possibleDates = new HashSet<>();

	@Builder
	public Mentor(String phoneNum, String birth, String part) {
		this.phoneNum = phoneNum;
		this.birth = birth;
		this.part = part;
	}

	public static Mentor from(CreateMentorRequest dto) {
		return Mentor.builder()
			.phoneNum(dto.getPhoneNum())
			.birth(dto.getBirth())
			.part(dto.getPart())
			.build();
	}

	public void addPossibleDate(PossibleDate possibleDate) {
		this.possibleDates.add(possibleDate);
	}
}
