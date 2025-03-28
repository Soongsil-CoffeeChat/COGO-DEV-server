package com.soongsil.CoffeeChat.global.security.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.CoffeeChat.global.exception.ErrorResponse;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthExceptionHandlingFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (GlobalException e) {
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            GlobalErrorCode code = e.getGlobalErrorCode();

            ErrorResponse errorResponse = new ErrorResponse(code);

            mapper.writeValue(response.getOutputStream(), errorResponse);
        }
    }
}
