package com.soongsil.CoffeeChat.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLRestriction;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeConverter;
import com.soongsil.CoffeeChat.domain.mentee.dto.MenteeRequest.MenteeJoinRequest;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorConverter;
import com.soongsil.CoffeeChat.domain.mentor.dto.MentorRequest.MentorJoinRequest;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest.UserUpdateRequest;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.OAuth2Response;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("isDeleted = false")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column private String username;

    @Column private String name;

    @Column private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column private String phoneNum; // 전화번호

    @Column private String picture;

    @Builder.Default @Column private Boolean isDeleted = false;

    @Column private LocalDateTime deletedAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_mentor", referencedColumnName = "mentor_id")
    private Mentor mentor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_mentee", referencedColumnName = "mentee_id")
    private Mentee mentee;

    public Mentor registerAsMentor(MentorJoinRequest dto) {
        this.mentor = MentorConverter.toEntity(dto, this);
        if (this.role != Role.ROLE_ADMIN) this.role = Role.ROLE_MENTOR;
        return mentor;
    }

    public Mentee registerAsMentee(MenteeJoinRequest dto) {
        this.mentee = MenteeConverter.toEntity(dto, this);
        if (this.role != Role.ROLE_ADMIN) this.role = Role.ROLE_MENTEE;
        return mentee;
    }

    public void updateUser(UserUpdateRequest request) {
        this.name = request.getName();
        this.phoneNum = request.getPhoneNum();
        this.email = request.getEmail();
    }

    public void updateUser(OAuth2Response response) {
        this.name = response.getName();
        this.email = response.getEmail();
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void undoSoftDelete() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public boolean isMentor() {
        return this.mentor != null;
    }

    public boolean isMentee() {
        return this.mentee != null;
    }
}
