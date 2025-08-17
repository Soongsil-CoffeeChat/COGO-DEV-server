package com.soongsil.CoffeeChat.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtUtils {
    public static Map<String, Object> decodeJwtPayload(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT", e);
        }
    }
}
