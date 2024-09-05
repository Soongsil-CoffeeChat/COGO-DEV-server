package com.soongsil.CoffeeChat.dto.UserController;


import com.soongsil.CoffeeChat.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserInfoDto {
    private String username;
    private String name;
    private String email;
    private String role;
    private String phoneNum;  //전화번호
    private String picture;

    public static UserInfoDto toDto(User user){
        return UserInfoDto.builder().
                username(user.getUsername()).
                name(user.getName()).
                email(user.getEmail()).
                role(user.getRole()).
                phoneNum(user.getPhoneNum()).
                picture(user.getPicture())
                .build();
    }


}
