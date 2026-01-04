package com.study.security.application.security.exception.handler;

import com.study.security.application.security.common.util.SecurityResponseSender;
import com.study.security.common.exception.code.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 커스텀 인증 예외 (401 Unauthorized) 핸들러
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityResponseSender securityResponseSender;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 401 Unauthorized 응답 전송
        securityResponseSender.sendError(response, AuthErrorCode.AUTHENTICATION_REQUIRED);
    }
}
