package com.soongsil.CoffeeChat.controller;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  //RestController=Controller+ResponseBody
public class ReissueController {  //Refresh토큰으로 Access토큰 발급 및 2차회원가입 컨트롤러
    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;
    public ReissueController(JWTUtil jwtUtil, ReissueService reissueService){
        this.jwtUtil=jwtUtil;
        this.reissueService=reissueService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        return reissueService.responseOfRefreshToken(request, response);
    }
}
