package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	private String date;

	@Column
	private String time;

	@Column
	private String question;

	@Column
	private boolean accept;
}
