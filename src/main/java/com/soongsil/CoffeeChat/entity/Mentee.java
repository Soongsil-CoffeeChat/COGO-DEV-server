package com.soongsil.CoffeeChat.entity;

import java.util.HashSet;
import java.util.Set;

import com.soongsil.CoffeeChat.dto.MenteeDto;

import com.soongsil.CoffeeChat.enums.PartEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


	@OneToMany(mappedBy = "mentee", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Application> applications = new HashSet<>();


	public static Mentee from(MenteeDto dto) {
		return Mentee.builder()
			.part(dto.getPart())
			.build();
	}
}
