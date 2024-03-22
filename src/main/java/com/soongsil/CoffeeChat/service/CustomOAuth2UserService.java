package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.dto.GoogleResponse;
import com.soongsil.CoffeeChat.dto.NaverResponse;
import com.soongsil.CoffeeChat.dto.OAuth2Response;
import com.soongsil.CoffeeChat.dto.UserDTO;
import com.soongsil.CoffeeChat.dto.CustomOAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    //리소스 서버에서 제공되는 유저정보 가져오기
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //리소스서버로부터 유저 데이터를 받아 소셜 형식에 맞게 데이터 전처리(DTO로)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setName(oAuth2Response.getName());
        userDTO.setRole("ROLE_USER");
        return new CustomOAuth2User(userDTO);
    }
    }

}
