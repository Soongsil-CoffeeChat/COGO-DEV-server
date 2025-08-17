package com.soongsil.CoffeeChat.infra.sms;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class SmsUtil {
    private final DefaultMessageService messageService;
    private final String from;

    private void sendMessage(String to, String message) {
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

    private void sendMessage(User to, String message) {
        sendMessage(to.getPhoneNum(), message);
    }

    // 단일 메시지 발송 예제
    public String send2FACode(String to) {
        String verificationCode = generateVerificationCode();
        sendMessage(to, "아래의 인증번호를 입력해주세요\n" + verificationCode);
        return verificationCode;
    }

    public void sendMenteeNotificationMessage(Application application) {
        sendMessage(
                application.getMentee().getUser(), "띵동~♪ 멘토님이 커피챗 요청에 응답했어요! 지금 바로 코고 앱에서 확인해 보세요");
    }

    public void sendMentorNotificationMessage(Application application) {
        sendMessage(
                application.getMentee().getUser(),
                "띵동~♪ 멘토님께 커피챗 신청서가 도착했어요! 지금 바로 코고 앱에서 확인해 보세요");
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000; // 4자리 랜덤 숫자 생성
        return String.valueOf(code);
    }
}
