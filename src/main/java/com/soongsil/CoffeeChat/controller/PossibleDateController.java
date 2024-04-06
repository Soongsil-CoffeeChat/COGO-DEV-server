package com.soongsil.CoffeeChat.controller;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.service.PossibleDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.soongsil.CoffeeChat.enums.RequestUri.POSSIBLEDATE_URI;

@RestController
@RequestMapping(POSSIBLEDATE_URI)
@RequiredArgsConstructor
public class PossibleDateController {
    private final PossibleDateService possibleDateService;

    @PostMapping()
    public ResponseEntity<?> addPossibleDate(Authentication authentication,
                                             @RequestBody PossibleDateRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                possibleDateService.createPossibleDate(dto, authentication.getName()));
    }
}
