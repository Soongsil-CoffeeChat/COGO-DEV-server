package com.soongsil.CoffeeChat.entity;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
//@Inheritance(strategy = InheritanceType.JOINED)  //자식 : Mentor, Mentee
//@DiscriminatorColumn // 하위 테이블의 구분 컬럼 생성(default = DTYPE)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column
	private String phone;  //전화번호

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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_mentor", referencedColumnName = "mentor_id")
	private Mentor mentor;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_mentee", referencedColumnName = "mentee_id")
	private Mentee mentee;

}
