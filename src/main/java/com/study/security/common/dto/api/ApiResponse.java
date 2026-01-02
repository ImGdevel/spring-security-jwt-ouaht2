package com.study.security.common.dto.api;

/**
 * API 응답을 감싸는 공통 DTO.
 *
 * @param success 응답 성공 여부
 * @param data 응답 데이터
 * @param message 추가 메시지(선택)
 */
public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {

    /**
     * 성공 응답 생성.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }
}
