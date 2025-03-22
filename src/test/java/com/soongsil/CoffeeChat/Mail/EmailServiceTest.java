package com.soongsil.CoffeeChat.Mail;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
//
// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.context.ApplicationContext;
// import org.springframework.mail.javamail.JavaMailSender;
//
// import com.soongsil.CoffeeChat.infra.email.EmailUtil;
//
// @ExtendWith(MockitoExtension.class)
// public class EmailServiceTest {
//
//    @Mock private JavaMailSender javaMailSender;
//
//    @InjectMocks private EmailUtil emailUtil;
//
//    @Mock private MimeMessage mimeMessage;
//
//    @Mock private ApplicationContext applicationContext;
//
//    @BeforeEach
//    void setUp() {
//        // JavaMailSender의 createMimeMessage 메서드가 mock MimeMessage 객체를 반환하도록 설정
//        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        // EmailUtil 프록시를 반환하도록 ApplicationContext 설정
//        when(applicationContext.getBean(EmailUtil.class)).thenReturn(emailUtil);
//    }
//
//    @Test
//    void testSendAuthenticationEmailReturnsCodeAndHandlesException()
//            throws MessagingException, InterruptedException {
//        // given
//        String receiver = "test@example.com";
//        // sendMail이 호출될 때 예외를 던지도록 설정(런타임)
//        doThrow(new RuntimeException("Failed to send email"))
//                .when(javaMailSender)
//                .send(any(MimeMessage.class));
//        // when
//        String resultCode = null;
//        try {
//            resultCode = emailUtil.sendAuthenticationEmail(receiver);
//        } catch (RuntimeException e) {
//            // 예외가 발생해도 메서드가 종료되지 않고 정상적으로 처리되었는지 확인
//            System.out.println("예외 발생: " + e.getMessage());
//        }
//        // then
//        // assertNotNull(resultCode);  // 반환된 코드가 null이 아닌지 확인
//        assertEquals(6, resultCode.length()); // 반환된 코드의 길이가 6자리인지 확인
//        assertTrue(resultCode.matches("\\d{6}")); // 반환된 코드가 6자리 숫자인지 확인
//        System.out.println("resultCode = " + resultCode);
//        verify(javaMailSender, times(1)).send(any(MimeMessage.class)); // sendMail이 한 번만 호출되었는지 확인
//    }
// }
