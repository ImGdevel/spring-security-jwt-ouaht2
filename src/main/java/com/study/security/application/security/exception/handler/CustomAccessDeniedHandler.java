package com.study.security.application.security.exception.handler;

import com.study.security.application.security.common.util.SecurityResponseSender;
import com.study.security.common.exception.code.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 커스텀 인가 접근 거부 핸들러 (403 Forbidden)
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityResponseSender securityResponseSender;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 403 Forbidden 응답 전송
        securityResponseSender.sendError(response, AuthErrorCode.ACCESS_DENIED);
    }
}
