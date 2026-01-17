package com.soongsil.CoffeeChat.domain.application.repository;

import com.soongsil.CoffeeChat.domain.application.entity.Application;

public interface ApplicationRepositoryCustom {

    Application findByMentorMentee(String mentorName, String menteeName);
}
