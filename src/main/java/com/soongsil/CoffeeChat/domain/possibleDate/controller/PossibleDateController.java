package com.soongsil.CoffeeChat.domain.possibleDate.controller;

import static com.soongsil.CoffeeChat.global.uri.RequestUri.POSSIBLEDATE_URI;

import java.net.URI;
import java.util.List;

import feign.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateRequest;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.dto.PossibleDateResponse.PossibleDateDetailResponse;
import com.soongsil.CoffeeChat.domain.possibleDate.service.PossibleDateService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(POSSIBLEDATE_URI)
@RequiredArgsConstructor
@Tag(name = "POSSIBLEDATE", description = "커피챗 시간 관련 api")
public class PossibleDateController {
    private final PossibleDateService possibleDateService;

    private String getUserNameByAuthentication(Authentication authentication) throws Exception {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if (principal == null) throw new Exception(); // TODO : Exception 만들기
        return principal.getUsername();
    }

    @GetMapping("{mentorId}")
    @Operation(summary = "멘토ID로 커피챗 가능시간 불러오기")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "DTO LIST형식으로 정보 반환")
    public ResponseEntity<ApiResponse<List<PossibleDateDetailResponse>>> getPossibleDates(
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
    public ResponseEntity<ApiResponse<List<PossibleDateDetailResponse>>> getPossibleDatesByToken(
            Authentication authentication) throws Exception {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                possibleDateService.findMentorPossibleDateListByUsername(
                                        getUserNameByAuthentication(authentication))));
    }

    @PostMapping("")
    @Operation(summary = "멘토 본인의 커피챗 가능시간 생성")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "생성된 가능시간 기본 정보 반환")
    public ResponseEntity<ApiResponse<PossibleDateResponse.PossibleDateCreateUpdateResponse>>
            createPossibleDate(
                    Authentication authentication,
                    @RequestBody PossibleDateRequest.PossibleDateCreateUpdateRequest request)
                    throws Exception {
        String username = getUserNameByAuthentication(authentication);
        PossibleDateResponse.PossibleDateCreateUpdateResponse response =
                possibleDateService.createPossibleDate(request, username);
        return ResponseEntity.created(
                        URI.create(POSSIBLEDATE_URI + "/" + response.getPossibleDateId()))
                .body(ApiResponse.onSuccessCREATED(response));
    }

    @PutMapping("/{possibleDateId}")
    @Operation(summary = "멘토 본인의 가능시간 수정")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "특정 PossibleDate 일부 정보 반환")
    public ResponseEntity<ApiResponse<PossibleDateResponse.PossibleDateCreateUpdateResponse>>
            updatePossibleDate(
                    Authentication authentication,
                    @PathVariable("possibleDateId") Long possibleDateId,
                    @RequestBody PossibleDateRequest.PossibleDateCreateUpdateRequest request)
                    throws Exception {
        return ResponseEntity.ok(
                ApiResponse.onSuccessOK(
                        possibleDateService.updatePossibleDate(
                                possibleDateId,
                                request,
                                getUserNameByAuthentication(authentication))));
    }

    @PutMapping("")
    @Operation(summary = "멘토 가능 시간 전체 교체")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "2주 내의 active 슬롯 목록 반환")
    public ResponseEntity<ApiResponse<List<PossibleDateDetailResponse>>> updatePossibleDateList(
            Authentication authentication,
            @RequestBody List<PossibleDateRequest.PossibleDateCreateUpdateRequest> requests) throws Exception{

        String username=getUserNameByAuthentication(authentication);
        List<PossibleDateDetailResponse> body=
                possibleDateService.replaceMyPossibleDated(requests,username);
        return ResponseEntity.ok(ApiResponse.onSuccessOK(body));
    }
}
