package com.soongsil.CoffeeChat.controller;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.service.MentorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.soongsil.CoffeeChat.enums.RequestUri.MENTOR_URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(MENTOR_URI)
@RestController
@Tag(name="MENTOR", description = "멘토 관련 api")
public class MentorController {
    private final MentorService mentorService;
    public MentorController(MentorService mentorService){
        this.mentorService=mentorService;
    }
    @GetMapping("/{part}")
    @Operation(summary="파트별 멘토 리스트 가져오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<List<ResponseMentorListInfo>>
    getMentorListByPart(Authentication authentication, @PathVariable("part") String part){
        return ResponseEntity.ok().body(mentorService.getMentorDtoListByPart(part));
    }

    @GetMapping("/possibleDates/{username}")
    @Operation(summary="멘토의 username으로 커피챗가능시간 불러오기")
    @ApiResponse(responseCode = "200", description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<List<PossibleDateRequestDto>> getPossibleDates
            (Authentication authentication,
             @PathVariable("username") String username){
        return ResponseEntity.ok().body(mentorService.findPossibleDateListByMentor(username));
    }

}
