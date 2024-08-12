package com.soongsil.CoffeeChat.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.soongsil.CoffeeChat.enums.ApplicationStatus;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mentor_id")
	private Mentor mentor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mentee_id")
	private Mentee mentee;

	@Column
	private String memo;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(255) DEFAULT 'UNMATCHED'")
	private ApplicationStatus accept;

	@OneToOne
	@JoinColumn(name = "possible_date_id")
	private PossibleDate possibleDate;
}