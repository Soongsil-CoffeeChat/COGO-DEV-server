package com.soongsil.CoffeeChat.repository.Mentor;

import static com.soongsil.CoffeeChat.entity.QIntroduction.*;
import static com.soongsil.CoffeeChat.entity.QMentor.*;
import static com.soongsil.CoffeeChat.entity.QUser.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.MentorGetListResponseDto;
import com.soongsil.CoffeeChat.dto.MentorGetUpdateDetailDto;
import com.soongsil.CoffeeChat.dto.QMentorGetListResponseDto;
import com.soongsil.CoffeeChat.dto.QMentorGetUpdateDetailDto;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

public class MentorRepositoryImpl implements MentorRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public MentorRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<MentorGetListResponseDto> getMentorListByPart(PartEnum part) { //일반 join

		return queryFactory
			.select(new QMentorGetListResponseDto(
				user.picture,
				user.name.as("mentorName"),
				mentor.part,
				mentor.club,
				user.username,
				mentor.id.as("mentorId"),
				introduction.title,
				introduction.description
			))
			.from(user)
			.join(user.mentor, mentor)
			.join(mentor.introduction, introduction)
			.where(mentor.part.eq(part))
			.fetch();
	}

	@Override
	public List<MentorGetListResponseDto> getMentorListByClub(ClubEnum club) { //일반 join
		return queryFactory
			.select(new QMentorGetListResponseDto(
				user.picture,
				user.name.as("mentorName"),
				mentor.part,
				mentor.club,
				user.username,
				mentor.id.as("mentorId"),
				introduction.title,
				introduction.description
			))
			.from(user)
			.join(user.mentor, mentor)
			.join(mentor.introduction, introduction)
			.where(mentor.club.eq(club))
			.fetch();
	}

	@Override
	public List<MentorGetListResponseDto> getMentorListByPartAndClub(PartEnum part, ClubEnum club) { //일반 join
		return queryFactory
			.select(new QMentorGetListResponseDto(
				user.picture,
				user.name.as("mentorName"),
				mentor.part,
				mentor.club,
				user.username,
				mentor.id.as("mentorId"),
				introduction.title,
				introduction.description
			))
			.from(user)
			.join(user.mentor, mentor)
			.join(mentor.introduction, introduction)
			.where(mentor.club.eq(club).and(mentor.part.eq(part)))
			.fetch();
	}

	@Override
	public List<User> getMentorListByPartWithFetch(PartEnum part) {  //fetch join
		return queryFactory
			.selectFrom(user)
			.join(user.mentor, mentor).fetchJoin()
			.where(mentor.part.eq(part))
			.fetch();
	}

	@Override
	public MentorGetUpdateDetailDto getMentorInfoByMentorId(Long mentorId) {
		return queryFactory
			.select(new QMentorGetUpdateDetailDto(
				mentor.id.as("mentorId"),
				user.name.as("mentorName"),
				mentor.part,
				introduction.title.as("introductionTitle"),
				introduction.description.as("introductionDescription"),
				introduction.answer1.as("introductionAnswer1"),
				introduction.answer2.as("introductionAnswer2"),
				user.picture.as("imageUrl")
			))
			.from(user)
			.join(user.mentor, mentor)
			.join(mentor.introduction, introduction)
			.where(mentor.id.eq(mentorId))
			.fetchOne();
	}

}
