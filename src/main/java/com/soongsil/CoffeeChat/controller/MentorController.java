package com.soongsil.CoffeeChat.controller;

import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.service.MentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.soongsil.CoffeeChat.enums.RequestUri.MENTOR_URI;

@RequestMapping(MENTOR_URI)
@RestController
public class MentorController {
    private final MentorService mentorService;
    public MentorController(MentorService mentorService){
        this.mentorService=mentorService;
    }
    @GetMapping("/{part}")
    public ResponseEntity<List<ResponseMentorListInfo>>
    getMentorListByPart(Authentication authentication, @PathVariable("part") String part){
        return ResponseEntity.ok().body(mentorService.getMentorDtoListByPart(part));
    }

}
