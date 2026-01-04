package com.study.security.application.security.common.logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 로그아웃 시 수행할 확장 가능한 작업(SPI).
 * <p>
 * 예: refresh token 블랙리스트 등록, 쿠키 삭제, 외부 세션 정리 등.
 */
public interface LogoutAction {

    void onLogout(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

