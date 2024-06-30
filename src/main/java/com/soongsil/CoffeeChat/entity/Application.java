package com.soongsil.CoffeeChat.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.soongsil.CoffeeChat.enums.ApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Application {
	@Column(name = "application_id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "mentor_id")
	private Mentor mentor;

	@ManyToOne
	@JoinColumn(name = "mentee_id")
	private Mentee mentee;

	@Column
	private LocalDate date;

	@Column
	private LocalTime startTime;

	@Column
	private LocalTime endTime;

	// @Column
	// private String question;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'UNMATCHED'")
	private ApplicationStatus accept;

}