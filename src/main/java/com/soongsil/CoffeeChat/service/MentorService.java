package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.repository.MentorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;
    public MentorService(MentorRepository mentorRepository){
        this.mentorRepository=mentorRepository;
    }
    //사용되는 공통 private method
    private List<Mentor> findMentorListByPart(String part){
        return mentorRepository.findAllByPart(part);
    }


    public List<ResponseMentorListInfo> getMentorDtoListByPart(String part){

    }
}
