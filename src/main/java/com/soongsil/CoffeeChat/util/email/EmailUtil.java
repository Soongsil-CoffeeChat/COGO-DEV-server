package com.soongsil.CoffeeChat.util.email;

// @Component
// @RequiredArgsConstructor
// public class EmailUtil {
//
//	private final JavaMailSender javaMailSender;
//	private final ApplicationContext applicationContext;
//	private final SqsTemplate sqsTemplate;
//	private final ObjectMapper objectMapper;
//	@Value("${cloud.aws.sqs.queue-name}")
//	private String queueName;
//
//	// 인증 이메일 발송 메서드
//	public String sendAuthenticationEmail(String receiver) throws InterruptedException {
//		String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
//
//		// 이메일 메시지를 JSON 형식으로 생성
//		EmailMessage emailMessage = new EmailMessage(receiver, "[COGO] 이메일 인증번호입니다.",
//				createAuthMessageTemplate("[COGO] 이메일 인증 안내", "이메일 인증을 완료하려면 아래의 인증 번호를 사용하여 계속 진행하세요:",
// code));
//
//		try {
//			// 메시지를 JSON으로 변환
//			String messageBody = objectMapper.writeValueAsString(emailMessage);
//
//			// SQS로 메시지 전송
//			sqsTemplate.send(queueName, messageBody);
//			System.out.println("메시지가 SQS로 전송되었습니다: " + messageBody);
//		} catch (Exception e) {
//			// 예외가 발생했을 때 로그를 남기고, 기본 코드 반환 등을 처리
//			System.out.println("SQS 메시지 전송 실패: " + e.getMessage());
//			throw new RuntimeException("메일전송실패");
//		}
//
//		return code;
//	}
//
//	private String createAuthMessageTemplate(String subject, String body, String code) {
//		return "<h1>" + subject + "</h1>" +
//				"<p>" + body + "</p>" +
//				"<h2>인증 번호: " + code + "</h2>";
//	}
//
//
//	// 비동기 메일 발송 메서드
//	@Async("mailExecutor")
//	public void sendMail(String receiver, String subject, String content) throws MessagingException {
//		MimeMessage message = javaMailSender.createMimeMessage();
//		message.setSubject(subject);
//		message.addRecipients(Message.RecipientType.TO, receiver);
//		message.setText(content, "utf-8", "html");
//		javaMailSender.send(message);
//	}
//	/*
//	// 인증 이메일 발송 메서드
//	public String sendAuthenticationEmail(String receiver) throws MessagingException,
// InterruptedException {
//		String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
//
//		// 비동기 메일 발송 메서드를 프록시를 통해 호출
//		EmailUtil proxy = applicationContext.getBean(EmailUtil.class);
//		try {
//			proxy.sendMail(receiver, "[COGO] 이메일 인증번호입니다.",
//					createMessageTemplate("[COGO] 이메일 인증 안내", "이메일 인증을 완료하려면 아래의 인증 번호를 사용하여 계속 진행하세요:", code));
//		} catch (RuntimeException e) {
//			// 예외가 발생했을 때 로그를 남기고, 기본 코드 반환 등을 처리
//			System.out.println("메일 전송 실패: " + e.getMessage());
//		}
//
//		return code;
//	}
//
//	 */
//
//	public void sendApplicationMatchedEmail(String receiver, String mentorName, String menteeName,
// LocalDate date,
//		LocalTime startTime, LocalTime endTime) throws MessagingException {
//		sendMail(receiver, "[COGO] 매칭이 성사되었습니다.",
//			createMessageTemplate("[COGO] 매칭 성사",
//				mentorName + " 멘토님과 " + menteeName + " 멘티님의 매칭이 성사되었습니다!",
//				"일자: " + date + "\n시간: " + startTime + " ~ " + endTime + "\n"));
//	}
//
//	private String createMessageTemplate(String subject, String description, String mainContent) {
//		return "<!DOCTYPE html>\n" +
//			"<html lang=\"ko\">\n" +
//			"<head>\n" +
//			"    <meta charset=\"UTF-8\">\n" +
//			"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//			"    <title>이메일 인증</title>\n" +
//			"    <style>\n" +
//			"        body {\n" +
//			"            font-family: Arial, sans-serif;\n" +
//			"            background-color: #f4f4f4;\n" +
//			"            margin: 0;\n" +
//			"            padding: 0;\n" +
//			"            text-align: center;\n" +
//			"        }\n" +
//			"        .container {\n" +
//			"            max-width: 600px;\n" +
//			"            margin: 50px auto;\n" +
//			"            background-color: #fff;\n" +
//			"            padding: 20px;\n" +
//			"            border-radius: 8px;\n" +
//			"            box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n" +
//			"        }\n" +
//			"        h1 {\n" +
//			"            color: #FF4A4A;\n" +
//			"        }\n" +
//			"        p {\n" +
//			"            color: #666;\n" +
//			"            margin-bottom: 20px;\n" +
//			"        }\n" +
//			"        .main-content {\n" +
//			"            font-size: 24px;\n" +
//			"            font-weight: bold;\n" +
//			"            color: #FF4A4A;\n" +
//			"        }\n" +
//			"        .note {\n" +
//			"            color: #999;\n" +
//			"            margin-top: 30px;\n" +
//			"        }\n" +
//			"        .footer {\n" +
//			"            margin-top: 40px;\n" +
//			"            color: #999;\n" +
//			"        }\n" +
//			"    </style>\n" +
//			"</head>\n" +
//			"<body>\n" +
//			"    <div class=\"container\">\n" +
//			"        <h1>" + subject + "</h1>\n" +
//			"        <p>안녕하세요, COGO 입니다.</p>\n" +
//			"        <p>" + description + "</p>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <div class=\"main-content\">" + mainContent + "</div>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <br>\n" +
//			"        <p class=\"footer\">이 이메일은 자동 발송되었습니다. 회신하지 마세요.</p>\n" +
//			"    </div>\n" +
//			"</body>\n" +
//			"</html>";
//	}
// }
