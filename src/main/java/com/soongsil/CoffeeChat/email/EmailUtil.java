package com.soongsil.CoffeeChat.email;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class EmailUtil {

	private final JavaMailSender javaMailSender;

	@Async("mailExecutor")
	public void sendMail(String receiver, String subject, String content) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.setSubject(subject);
		message.addRecipients(Message.RecipientType.TO, receiver);
		message.setText(content, "utf-8", "html");
		javaMailSender.send(message);
	}

	@Async("mailExecutor")
	public CompletableFuture<String> sendAuthenticationEmail(String receiver) throws MessagingException, InterruptedException {
		String code = String.valueOf((int)((Math.random() * 900000) + 100000));

		sendMail(receiver, "[COGO] 이메일 인증번호입니다.",
				createMessageTemplate("[COGO] 이메일 인증 안내", "이메일 인증을 완료하려면 아래의 인증 번호를 사용하여 계속 진행하세요:", code));

		return CompletableFuture.completedFuture(code);
	}


	public void sendApplicationMatchedEmail(String receiver, String mentorName, String menteeName, LocalDate date,
											LocalTime startTime, LocalTime endTime) throws MessagingException {
		sendMail(receiver, "[COGO] 매칭이 성사되었습니다.",
				createMessageTemplate("[COGO] 매칭 성사",
						mentorName + " 멘토님과 " + menteeName + " 멘티님의 매칭이 성사되었습니다!",
						"일자: " + date + "\n시간: " + startTime + " ~ " + endTime + "\n"));
	}

	private String createMessageTemplate(String subject, String description, String mainContent) {
		return "<!DOCTYPE html>\n" +
				"<html lang=\"ko\">\n" +
				"<head>\n" +
				"    <meta charset=\"UTF-8\">\n" +
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
				"    <title>이메일 인증</title>\n" +
				"    <style>\n" +
				"        body {\n" +
				"            font-family: Arial, sans-serif;\n" +
				"            background-color: #f4f4f4;\n" +
				"            margin: 0;\n" +
				"            padding: 0;\n" +
				"            text-align: center;\n" +
				"        }\n" +
				"        .container {\n" +
				"            max-width: 600px;\n" +
				"            margin: 50px auto;\n" +
				"            background-color: #fff;\n" +
				"            padding: 20px;\n" +
				"            border-radius: 8px;\n" +
				"            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n" +
				"        }\n" +
				"        h1 {\n" +
				"            color: #FF4A4A;\n" +
				"        }\n" +
				"        p {\n" +
				"            color: #666;\n" +
				"            margin-bottom: 20px;\n" +
				"        }\n" +
				"        .main-content {\n" +
				"            font-size: 24px;\n" +
				"            font-weight: bold;\n" +
				"            color: #FF4A4A;\n" +
				"        }\n" +
				"        .note {\n" +
				"            color: #999;\n" +
				"            margin-top: 30px;\n" +
				"        }\n" +
				"        .footer {\n" +
				"            margin-top: 40px;\n" +
				"            color: #999;\n" +
				"        }\n" +
				"    </style>\n" +
				"</head>\n" +
				"<body>\n" +
				"    <div class=\"container\">\n" +
				"        <h1>" + subject + "</h1>\n" +
				"        <p>안녕하세요, COGO 입니다.</p>\n" +
				"        <p>" + description + "</p>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <div class=\"main-content\">" + mainContent + "</div>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <br>\n" +
				"        <p class=\"footer\">이 이메일은 자동 발송되었습니다. 회신하지 마세요.</p>\n" +
				"    </div>\n" +
				"</body>\n" +
				"</html>";
	}
}
