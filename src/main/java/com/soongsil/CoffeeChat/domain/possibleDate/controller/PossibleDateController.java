package com.soongsil.CoffeeChat.domain.possibleDate.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.POSSIBLEDATE_URI;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateRequest.PossibleDateCreateRequest;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateCreateResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.service.PossibleDateService;
import com.soongsil.CoffeeChat.global.annotation.CurrentUsername;
import com.soongsil.CoffeeChat.global.api.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(POSSIBLEDATE_URI)
@RequiredArgsConstructor
@Tag(name = "POSSIBLEDATE", description = "커피챗 시간 관련 api")
public class PossibleDateController {
    private final PossibleDateService possibleDateService;

    @PutMapping
    @Operation(summary = "멘토가 직접 커피챗 가능시간 갱신하기")
    public ResponseEntity<ApiResponse<List<PossibleDateCreateResponse>>> updatePossibleDate(
            @RequestBody List<PossibleDateCreateRequest> dtos,
            @Parameter(hidden = true) @CurrentUsername String username) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                possibleDateService.updatePossibleDate(dtos, username)));
    }

    @GetMapping("{mentorId}")
    @Operation(summary = "멘토ID로 커피챗 가능시간 불러오기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponse<List<PossibleDateCreateResponse>>> getPossibleDates(
            @PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                possibleDateService.findPossibleDateListByMentor(mentorId)));
    }

    @GetMapping("")
    @Operation(summary = "토큰으로 멘토 본인의 커피챗 가능시간 불러오기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponse<List<PossibleDateCreateResponse>>> getPossibleDatesByToken(
            @Parameter(hidden = true) @CurrentUsername String username) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                possibleDateService.findMentorPossibleDateListByUsername(
                                        username)));
    }
}
