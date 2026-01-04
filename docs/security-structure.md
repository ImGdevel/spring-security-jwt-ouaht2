# `security.application.security` 구조/역할 정리

Spring Security 기반 인증/인가를 “구성(config) + 필터(filter) + 핸들러(handler) + 유틸(util)”로 분리해 둔 패키지입니다.
로컬(이메일/비밀번호) 로그인, JWT 인증, OAuth2 로그인 플로우가 한 곳에서 조립됩니다.

## 패키지 트리

- `src/main/java/com/study/security/application/security`
  - `annotation/`
    - `CurrentUser`: 컨트롤러 메서드 파라미터에 현재 사용자 UID 주입용 애노테이션.
    - `RefreshToken`: 컨트롤러 메서드 파라미터에 리프레시 토큰(쿠키) 주입용 애노테이션.
  - `resolver/`
    - `CurrentUserArgumentResolver`: `@CurrentUser Long` 처리 (SecurityContext의 principal에서 UID 추출).
    - `RefreshTokenArgumentResolver`: `@RefreshToken String` 처리 (쿠키에서 refresh token 추출).
  - `config/`
    - `SecurityConfig`: 필터 체인 구성(권한 규칙, 커스텀 로그인/로그아웃 필터, 예외 처리, 하위 설정 조립).
    - `JwtSecurityConfig`: `JwtAuthenticationFilter`를 필터 체인에 등록.
    - `OAuth2SecurityConfig`: OAuth2 로그인(userService/authorizationRequestRepository/successHandler) 구성.
    - `CorsConfig`: CORS 설정.
    - `PasswordEncoderConfig`: 비밀번호 인코더 빈.
    - `WebConfig`: MVC argument resolver 등록(`CurrentUserArgumentResolver`, `RefreshTokenArgumentResolver`).
    - `config/properties/CorsProperties`: CORS 허용 오리진 프로퍼티 바인딩.
    - `config/properties/SecurityCookieProperties`: OAuth2 쿠키의 `Secure`/`SameSite` 정책 바인딩 및 유효성 검증.
  - `common/`
    - `constants/`
      - `SecurityConstants`: 로그인/로그아웃/회원가입/리프레시 URL, 공개/보호/관리자 URL 패턴, 헤더 상수.
    - `util/`
      - `RedirectUriValidator`: 허용 오리진 기반 redirect URI 검증(OAuth2에서 사용).
      - `SecurityResponseSender`: 시큐리티 필터/핸들러에서 공통 JSON 응답 전송 유틸.
      - `NicknameGenerator`: 이메일/이름 기반 간단 닉네임 생성 유틸.
  - `exception/`
    - `filter/FilterChainExceptionFilter`: 필터 체인에서 발생한 예외를 포착해 공통 500 응답 처리.
    - `handler/CustomAuthenticationEntryPoint`: 인증 실패 시 401 응답 처리.
    - `handler/CustomAccessDeniedHandler`: 인가 실패 시 403 응답 처리.
    - `model/CustomAuthenticationException`, `model/CustomAccessDeniedException`: 에러코드 기반 커스텀 예외.
  - `jwt/`
    - `common/JwtTokenConstants`: JWT 클레임 키/토큰 타입 상수.
    - `common/JwtCookieConstants`: Refresh Token 쿠키 관련 상수.
    - `properties/JwtProperties`: 시크릿/만료시간 설정 바인딩.
    - `provider/JwtTokenProvider`: 액세스/리프레시 토큰 생성·검증, 클레임 추출, 만료 검사.
    - `provider/JwtCookieProvider`: Refresh Token 쿠키 발급/조회/삭제.
    - `filter/JwtAuthenticationFilter`: `Authorization: Bearer <token>` 검증 후 `SecurityContext` 인증 주입.
    - `repository/TokenBlacklistStore`: 블랙리스트 저장소 포트.
    - `repository/impl/RedisTokenBlacklistStore`: Redis 기반 블랙리스트 저장 어댑터.
    - `service/TokenBlacklistService`: refresh token 블랙리스트 등록/조회(키 구성 포함).
    - `service/TokenRefreshService`: refresh token 검증 + 블랙리스트 확인 후 access token 재발급.
  - `local/` (이메일/비밀번호 로그인)
    - `dto/CustomUserDetails`, `SignupRequest`, `LoginResponse`: 인증 principal 및 요청/응답 DTO.
    - `filter/CustomLoginAuthenticationFilter`: `/auth/login` JSON 본문 파싱 후 `AuthenticationManager`에 인증 위임.
    - `filter/CustomLogoutFilter`: `/auth/logout` POST 요청을 가로채 `LogoutHandler`로 위임.
    - `handler/LoginSuccessHandler`, `LoginFailureHandler`: 로그인 성공/실패 시 JSON 응답 및 토큰/쿠키 처리.
    - `handler/LogoutHandler`: 로그아웃 시 refresh token 블랙리스트 등록 및 쿠키 삭제.
    - `service/LoginService`: `UserDetailsService` 역할(이메일 기반 조회, 상태 검증 등).
    - `service/SignupService`: 회원가입 처리 및 초기 토큰/응답 구성.
  - `oauth/`
    - `common/OAuth2CookieConstants`: OAuth2 플로우 쿠키 이름/만료 등 상수.
    - `config/OAuth2ClientConfig`: OAuth2 클라이언트 설정.
    - `cookie/OAuth2CookieProvider`: OAuth2 인가요청/redirect URI 쿠키 발급·조회·삭제.
    - `repository/HttpCookieOAuth2AuthorizationRequestRepository`: OAuth2 authorization request를 쿠키에 직렬화 저장/복원.
    - `provider/*UserInfo`, `OAuthUserInfoFactory`: 공급자별 사용자 프로필 파싱.
    - `dto/CustomOAuthUserDetails`: OAuth2 인증 결과 principal.
    - `service/OAuthLoginService`: OAuth 사용자 정보 로드 및 사용자 등록/로그인 처리(userService).
    - `handler/OAuthLoginSuccessHandler`: OAuth 로그인 성공 시 refresh token 쿠키 발급 후 프론트로 리다이렉트.
  - `user/`
    - `dto/UserAccount`: 애플리케이션 레이어 사용자 모델.
    - `repository/UserRepositoryPort`: 사용자 조회/저장 포트.
    - `repository/impl/UserPersistenceAdapter`: 영속성 어댑터(도메인 repository 연계).

## 런타임 흐름(요약)

### 1) 요청 인증(JWT)

- `JwtAuthenticationFilter`가 `Authorization` 헤더에서 Bearer 토큰을 추출해 유효하면 `SecurityContext`에 인증을 주입합니다.
- 유효하지 않은 토큰은 예외를 던지지 않고 로그만 남긴 뒤 다음 필터로 진행합니다(즉, “비인증 요청”으로 처리).

### 2) 로컬 로그인(`/auth/login`)

- `CustomLoginAuthenticationFilter`가 JSON 요청을 파싱해 `AuthenticationManager`로 인증을 위임합니다.
- 성공/실패 응답은 `LoginSuccessHandler` / `LoginFailureHandler`가 담당합니다.

### 3) OAuth2 로그인

- `HttpCookieOAuth2AuthorizationRequestRepository`가 OAuth2 authorization request와 redirect URI를 쿠키로 저장/복원합니다.
- 성공 시 `OAuthLoginSuccessHandler`가 refresh token 쿠키를 발급하고, redirect URI(기본값 `http://localhost:3000/oauth/callback`)로 리다이렉트합니다.
- 쿠키 SameSite 정책은 `docs/security-cookie-policy.md`를 참고하세요.

### 4) 예외 처리(401/403/500)

- 401/403은 Spring Security의 `exceptionHandling()`에 등록된 `CustomAuthenticationEntryPoint` / `CustomAccessDeniedHandler`가 담당합니다.
- 그 외 예외는 `FilterChainExceptionFilter`가 포착해 공통 500 JSON 응답으로 마무리합니다.

## 참고

- 쿠키 SameSite/secure 정책: `docs/security-cookie-policy.md`
- `/auth` 엔드포인트: `src/main/java/com/study/security/presentation/controller/AuthController.java`
- 의존 도메인(멤버): `src/main/java/com/study/security/domain/member`
