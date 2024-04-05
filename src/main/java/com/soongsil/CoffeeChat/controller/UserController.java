package com.soongsil.CoffeeChat.controller;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.repository.UserRepository;
import com.soongsil.CoffeeChat.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.soongsil.CoffeeChat.enums.RequestUri.USER_URI;

@RestController
@RequestMapping(USER_URI)
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/join/mentor")
    public ResponseEntity<Mentor> joinWithMentor(Authentication authentication,
                                                 @RequestBody CreateMentorRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.saveMentorInformation(authentication.getName(), dto)
        );
    }

    @PostMapping("/join/mentee")
    public ResponseEntity<Mentee> joinWithMentor(Authentication authentication,
                                                 @RequestBody CreateMenteeRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.saveMenteeInformation(authentication.getName(), dto)
        );
    }

}
