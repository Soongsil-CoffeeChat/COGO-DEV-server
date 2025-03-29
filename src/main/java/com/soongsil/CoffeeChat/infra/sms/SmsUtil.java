package com.soongsil.CoffeeChat.infra.sms;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.domain.entity.Application;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component
@RequiredArgsConstructor
public class SmsUtil {
    private final DefaultMessageService messageService;
    private final String from;

    public void sendMessage(String to, String message) {
        Message messageToSend = new Message();
        messageToSend.setFrom(from);
        messageToSend.setTo(to);
        messageToSend.setText("[COGO]\n" + message);
        try {
            messageService.send(messageToSend);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.USER_SMS_ERROR);
        }
    }

    public void sendMessage(User to, String message) {
        sendMessage(to.getPhoneNum(), message);
    }

    // 단일 메시지 발송 예제
    public String send2FACode(String to) {
        String verificationCode = generateVerificationCode();
        sendMessage(to, "아래의 인증번호를 입력해주세요\n" + verificationCode);
        return verificationCode;
    }

    public void sendAcceptCogoMessage(Application application) {
        sendMessage(application.getMentee().getUser(), "멘토링이 성사되었습니다. COGO 앱에 접속해 확인해 보세요!");
        sendMessage(application.getMentee().getUser(), "멘토링이 성사되었습니다. COGO 앱에 접속해 확인해 보세요!");
    }

    public void sendRejectCogoMessage(Application application) {
        sendMessage(application.getMentee().getUser(), "멘토님의 답장이 도착했어요. 지금 바로 코고 앱에서 확인해 보세요!");
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000; // 4자리 랜덤 숫자 생성
        return String.valueOf(code);
    }
}
