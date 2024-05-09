package com.soongsil.CoffeeChat.entity;

import java.util.HashSet;
import java.util.Set;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
	private String part;

	@Column
	private String memo;

	@Column
	private String nickname;

	@OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Application> applications = new HashSet<>();

	@Builder
	public Mentee(String phoneNum, String birth, int grade, String part, String memo) {
		this.phoneNum = phoneNum;
		this.birth = birth;
		this.grade = grade;
		this.part = part;
		this.memo = memo;
	}

	public static Mentee from(CreateMenteeRequest dto) {
		return Mentee.builder()
			.nickname(dto.getNickname())
			.part(dto.getPart())
			.build();
	}

}
