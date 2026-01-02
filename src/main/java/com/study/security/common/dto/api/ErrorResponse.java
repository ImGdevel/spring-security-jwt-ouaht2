package com.study.security.common.dto.api;

import com.study.security.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * 오류 응답 포맷
 *
 * @param code 오류 식별 코드
 * @param message 오류 메시지
 * @param status HTTP 상태 코드
 */
public record ErrorResponse(
        String code,
        String message,
        int status
) {

    public static ErrorResponse of(String message) {
        return new ErrorResponse("UNKNOWN_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getHttpStatus().value()
        );
    }
}
