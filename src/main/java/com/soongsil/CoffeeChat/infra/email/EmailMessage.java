package com.soongsil.CoffeeChat.infra.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailMessage {
    private String toEmail;
    private String subject;
    private String htmlContent;
}
