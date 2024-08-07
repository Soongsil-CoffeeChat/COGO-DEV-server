package com.soongsil.CoffeeChat.repository.Mentor;

import java.util.List;

import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;

import com.soongsil.CoffeeChat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.entity.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, Long> , MentorRepositoryCustom{
	//Mentor findByUsername(String username); 상속시 사용가능

	List<Mentor> findAllByPart(String part);
	List<ResponseMentorListInfo> getMentorListByPart(String part);  //일반 join
	List<User> getMentorListByPart2(String part); //fetch join
}
