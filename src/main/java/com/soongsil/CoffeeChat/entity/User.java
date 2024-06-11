package com.soongsil.CoffeeChat.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//@Inheritance(strategy = InheritanceType.JOINED)  //자식 : Mentor, Mentee
//@DiscriminatorColumn // 하위 테이블의 구분 컬럼 생성(default = DTYPE)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column
	private String username;

	@Column
	private String name;

	@Column
	private String email;

	@Column
	private String role;

	@Column
	private String picture;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_mentor", referencedColumnName = "mentor_id")
	private Mentor mentor;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_mentee", referencedColumnName = "mentee_id")
	private Mentee mentee;
}
