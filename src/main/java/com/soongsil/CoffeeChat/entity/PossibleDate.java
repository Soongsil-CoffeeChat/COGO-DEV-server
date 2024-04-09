package com.soongsil.CoffeeChat.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PossibleDate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "possible_date_id")
	private Long id;

	@Setter
	@ManyToOne
	@JoinColumn(name = "mentor_id")
	private Mentor mentor;

	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate date;

	@JsonFormat(pattern = "HH:mm")  //datetimeformat은 ss까지 전부 다 받아야 오류안남
	LocalTime startTime;

	@JsonFormat(pattern = "HH:mm")
	LocalTime endTime;

	@Column
	private boolean apply;

	public static PossibleDate from(PossibleDateRequestDto dto) {
		return PossibleDate.builder()
			.date(dto.getDate())
			.startTime(dto.getStartTime())
			.endTime(dto.getEndTime())
			.apply(false)
			.build();
	}
}
