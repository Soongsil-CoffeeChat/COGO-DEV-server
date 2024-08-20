package com.soongsil.CoffeeChat.util.sms;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

import jakarta.annotation.PostConstruct;

@Component
public class SmsUtil {
	@Value("${SMS_KEY}")
	private String smsKey;

	@Value("${SMS_SECRET}")
	private String smsSecret;

	@Value("${SMS_FROM}")
	private String from;

	private DefaultMessageService messageService;

	@PostConstruct
	private void init() {
		this.messageService = NurigoApp.INSTANCE.initialize(smsKey, smsSecret, "https://api.coolsms.co.kr");
	}

	// 단일 메시지 발송 예제
	public String sendOne(String to) {
		String verificationCode = generateVerificationCode();
		Message message = new Message();
		//전송메시지 생성
		message.setFrom(from);
		message.setTo(to);
		message.setText("[COGO] 아래의 인증번호를 입력해주세요\n" + verificationCode);

		try {
			// 메시지 전송
			messageService.send(message);
		} catch (NurigoMessageNotReceivedException exception) {
			// 발송에 실패한 메시지 목록을 확인할 수 있습니다.
			System.out.println("Failed message list: " + exception.getFailedMessageList());
			System.out.println("Error message: " + exception.getMessage());
			return null;
		} catch (Exception exception) {
			System.out.println("Error message: " + exception.getMessage());
			return null;
		}

		return verificationCode;
	}

	private String generateVerificationCode() {
		Random random = new Random();
		int code = random.nextInt(9000) + 1000; // 4자리 랜덤 숫자 생성
		return String.valueOf(code);
	}
}
