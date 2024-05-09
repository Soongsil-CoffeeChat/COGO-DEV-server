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
public class Club {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "club_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "mentor_id")
	private Mentor mentor;

	@Column
	private String name;

	@Column
	private String position;
}
