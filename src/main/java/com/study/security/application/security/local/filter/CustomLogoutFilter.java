package com.study.security.application.security.local.filter;


import com.study.security.application.security.common.constants.SecurityConstants;
import com.study.security.application.security.local.handler.LogoutHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final LogoutHandler logoutHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if (!SecurityConstants.LOGOUT_URL.equals(requestURI) || !"POST".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        logoutHandler.onLogout(request, response);

    }
}
