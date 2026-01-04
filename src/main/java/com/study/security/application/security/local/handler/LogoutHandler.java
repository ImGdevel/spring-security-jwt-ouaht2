package com.study.security.application.security.local.handler;

import com.study.security.application.security.common.logout.LogoutAction;
import com.study.security.application.security.common.util.SecurityResponseSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutHandler {

    private final SecurityResponseSender securityResponseSender;
    private final List<LogoutAction> logoutActions;

    public void onLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        SecurityContextHolder.clearContext();

        for (LogoutAction logoutAction : logoutActions) {
            logoutAction.onLogout(request, response);
        }

        securityResponseSender.sendSuccess(response, HttpServletResponse.SC_OK, "로그아웃되었습니다.");
    }
}
