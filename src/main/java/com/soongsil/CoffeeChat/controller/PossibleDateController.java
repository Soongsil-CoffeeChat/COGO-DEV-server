package com.soongsil.CoffeeChat.controller;


import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.service.PossibleDateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.service.PossibleDateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(POSSIBLEDATE_URI)
@RequiredArgsConstructor
@Tag(name="POSSIBLEDATE", description = "커피챗 시간 관련 api")
public class PossibleDateController {
	private final PossibleDateService possibleDateService;

    @PostMapping()
    @Operation(summary="멘토가 직접 커피챗 가능시간 추가하기")
    @ApiResponse(responseCode = "200", description = "DTO형식으로 정보 반환")
    public ResponseEntity<PossibleDate> addPossibleDate(Authentication authentication,
                                             @RequestBody PossibleDateRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                possibleDateService.createPossibleDate(dto, authentication.getName()));
    }
}
