package com.study.security.application.security.common.util;

import com.study.security.common.dto.api.ApiResponse;
import com.study.security.common.dto.api.ErrorResponse;
import com.study.security.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Spring Security 필터/핸들러에서 사용하는 공통 응답 전송 유틸리티.
 * <p>
 * - JSON 헤더 설정
 * - 상태 코드 설정
 * - {@link ApiResponse} / {@link ErrorResponse} 직렬화
 */
@Component
@RequiredArgsConstructor
public class SecurityResponseSender {

    private final ObjectMapper objectMapper;

    /**
     * 상태 코드와 메시지를 지정해 에러 JSON을 전송한다.
     */
    public void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * {@link ErrorCode} 기반의 상태 코드/메시지를 적절한 JSON으로 응답한다.
     */
    public void sendError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.from(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * 메시지만 포함한 성공 응답을 전송한다.
     */
    public void sendSuccess(HttpServletResponse response, int status, String message) throws IOException {
        sendSuccess(response, status, null, message);
    }

    /**
     * body 포함 성공 응답을 전송한다.
     */
    public <T> void sendSuccess(HttpServletResponse response, int status, T body, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<T> apiResponse = ApiResponse.success(body, message);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
