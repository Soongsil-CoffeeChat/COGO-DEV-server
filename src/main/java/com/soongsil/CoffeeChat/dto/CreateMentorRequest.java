package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateMentorRequest {
    private String phoneNum;
    private String birth;
    private String part;


}
