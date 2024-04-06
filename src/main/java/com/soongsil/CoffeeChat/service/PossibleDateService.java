package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.dto.CreatePossibleDateRequest;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PossibleDateService {
    private final PossibleDateRepository possibleDateRepository;
    private final UserRepository userRepository;
    public PossibleDate createPossibleDate(CreatePossibleDateRequest dto,
                                           String username){
        User user=userRepository.findByUsername(username);
        Mentor mentor=user.getMentor();
        PossibleDate possibleDate=PossibleDate.from(dto);
        possibleDate.setMentor(mentor);
        mentor.addPossibleDate(possibleDate);
        possibleDateRepository.save(possibleDate);
        return possibleDate;
    }
}
