# spring-security-jwt-oauth2 (study)

Spring Security를 기반으로 다음을 “직접 구현/조립”하면서 심화 내용을 학습하기 위한 프로젝트입니다.

- Stateless 보안 구성 (세션 미사용)
- JWT Access/Refresh 토큰 기반 인증
- Refresh Token 블랙리스트(로그아웃/무효화)
- OAuth2 로그인(Authorization Code Grant, Confidential Client) + 로그인 후 토큰 전달 전략

관련 구현의 구조는 `docs/security-structure.md`, 평가/개선 포인트는 `docs/security-module-review.md`에 정리되어 있습니다.

---

## 1) 목적

이 프로젝트의 목표는 다음과 같습니다.

1. Spring Security의 필터 체인/핸들러/예외 처리 구조를 실제 서비스 형태로 구성해보기
2. JWT Access/Refresh 토큰을 이용한 인증/재발급/무효화(로그아웃) 흐름 구현
3. OAuth2 로그인(Authorization Code Grant, Confidential Client)을 적용하고, “로그인 결과를 프론트로 전달하는 방식”을 설계/구현

> 참고: 현재 코드 기준으로 OAuth2 provider user-info 파싱은 `google`, `github`가 구현되어 있습니다.
> (카카오/네이버는 동일한 Authorization Code 기반 흐름으로 확장 가능하지만, provider별 user-info 매핑은 별도 구현이 필요합니다.)

---

## 2) 구현 개요(현재 코드 기준)

### 보안 설정 조립

- 최상위 조립: `src/main/java/com/study/security/application/security/config/SecurityConfig.java`
  - CORS/CSRF/Session/RequestCache/권한 규칙
  - 예외 처리(401/403) + 필터 체인 예외 처리(500)
  - 하위 설정 조립 호출
- 로컬 로그인/로그아웃: `src/main/java/com/study/security/application/security/local/config/LocalSecurityConfig.java`
- JWT 인증 필터 등록: `src/main/java/com/study/security/application/security/jwt/config/JwtSecurityConfig.java`
- OAuth2 로그인 조립: `src/main/java/com/study/security/application/security/oauth/config/OAuth2SecurityConfig.java`

### 토큰 모델(요약)

- Access Token: `Authorization: Bearer <token>` 헤더로 전달
- Refresh Token: HttpOnly Cookie로 전달/저장
  - 쿠키 발급/삭제: `src/main/java/com/study/security/application/security/jwt/provider/JwtCookieProvider.java`
  - 쿠키 정책: `src/main/java/com/study/security/application/security/config/properties/SecurityCookieProperties.java`,
    `docs/security-cookie-policy.md`

---

## 3) OAuth2 로그인 방식: Authorization Code Grant (Confidential Client)

### 왜 Authorization Code(Confidential)인가?

브라우저 기반 로그인에서 “외부 OAuth Provider(구글/깃허브/카카오/네이버) 인증 → 우리 서버 콜백” 흐름을 안전하게 처리하려면,
Authorization Code Grant가 기본 선택지입니다.

- **토큰을 프론트로 직접 노출하지 않고**, 서버가 code를 받아 token endpoint와 통신(서버-서버)
- **client secret을 서버에서만 보관**(Confidential Client)
- 스프링 시큐리티 `spring-boot-starter-oauth2-client`가 이 플로우를 표준적으로 지원

### 현재 구현에서의 대응 구조

- OAuth2 authorization request를 **서버 세션이 아니라 쿠키**에 저장
  - `src/main/java/com/study/security/application/security/oauth/repository/HttpCookieOAuth2AuthorizationRequestRepository.java`
  - 쿠키 생성/삭제: `src/main/java/com/study/security/application/security/oauth/cookie/OAuth2CookieProvider.java`
- callback 성공 후 사용자 등록/조회 및 principal 구성
  - `src/main/java/com/study/security/application/security/oauth/service/OAuthLoginService.java`
  - provider별 프로필 파싱: `src/main/java/com/study/security/application/security/oauth/provider/*`
- 성공 후 후처리(토큰 전달/리다이렉트)
  - `src/main/java/com/study/security/application/security/oauth/handler/OAuthLoginSuccessHandler.java`

---

## 4) “로그인 결과 전달”에 대한 도전/고민: Refresh Cookie + 재발급 방식

이 프로젝트는 로그인(로컬/OAuth2) 이후 Access Token을 프론트로 전달하는 방식에서 다음 흐름을 사용합니다.

### 목표

- Refresh Token은 브라우저에 **HttpOnly Cookie**로만 저장(자바스크립트 접근 불가)
- Access Token은 API 호출 시 헤더로 사용(짧은 수명)
- OAuth2 로그인 성공 시 서버가 Refresh 쿠키를 심고, 프론트는 콜백 페이지에서 Access Token을 “재발급” 받아 사용

### 실제 흐름(현재 구현 기준)

#### A) 로컬 로그인

1. 클라이언트가 `POST /auth/login` 호출(JSON body)
2. `CustomLoginAuthenticationFilter`가 인증 시도
3. 성공 시 `LoginSuccessHandler`가
   - Refresh Token을 쿠키로 설정
   - Access Token을 JSON 응답 body로 반환
   - 구현: `src/main/java/com/study/security/application/security/local/handler/LoginSuccessHandler.java`

#### B) OAuth2 로그인

1. 클라이언트가 `/oauth2/authorization/{registrationId}`로 OAuth2 시작
2. 성공 콜백 이후 `OAuthLoginSuccessHandler`가
   - Refresh Token을 쿠키로 설정
   - 프론트 콜백 URL로 리다이렉트
   - 구현: `src/main/java/com/study/security/application/security/oauth/handler/OAuthLoginSuccessHandler.java`
3. 프론트 콜백 페이지에서 `POST /auth/refresh`를 호출하여 Access Token을 발급받음
   - 구현: `src/main/java/com/study/security/presentation/controller/AuthController.java`
   - 서비스: `src/main/java/com/study/security/application/security/jwt/service/TokenRefreshService.java`

### 왜 이렇게 했나? (효과/의도)

- Refresh Token을 응답 body나 URL 파라미터로 넘기지 않아 **유출 면적을 줄임**
  - URL/리퍼러/로그/브라우저 히스토리에 남기지 않음
- OAuth2는 “리다이렉트”가 본질인데, redirect response에 Access Token을 직접 넣는 방식은 구현/보안/운영에서 복잡도가 증가
  - 대신 “Refresh 쿠키 설정 → 프론트에서 refresh endpoint 호출”로 단순화
- Refresh Token 재발급 시점/정책을 서버에서 일원화
  - 블랙리스트/만료/사용자 상태(비활성 등) 체크를 서버에서 수행

### 확장성: 쿠키 기반 “요청 상태 저장소”

OAuth2 authorization request를 쿠키에 저장하는 방식은
세션 저장소(서버 상태)에 의존하지 않고도 OAuth2 플로우를 유지할 수 있게 해줍니다.

- `HttpCookieOAuth2AuthorizationRequestRepository`는 `AuthorizationRequestRepository` 구현체로 동작
- redirect URI를 별도 쿠키로 저장하고, `RedirectUriValidator`로 검증하여 오픈 리다이렉트 위험을 줄임

---

## 5) 로그아웃/무효화

- 로그아웃은 “확장 가능한 액션(SPI)”으로 구성되어, JWT/local/oauth2를 쉽게 on/off할 수 있게 설계했습니다.
  - SPI: `src/main/java/com/study/security/application/security/common/logout/LogoutAction.java`
  - JWT refresh 무효화: `src/main/java/com/study/security/application/security/jwt/logout/JwtRefreshTokenLogoutAction.java`
  - 실행 트리거: `src/main/java/com/study/security/application/security/local/handler/LogoutHandler.java`

Refresh Token 블랙리스트는 Redis를 사용합니다.

---

## 6) 실행 방법(로컬)

### 필수 구성

- Java 21
- Redis (블랙리스트/로그아웃 사용 시)

### 실행

- 빌드/테스트: `./gradlew test`
- 실행: `./gradlew bootRun`

### OAuth2 활성화(선택)

OAuth2 provider 등록정보는 `src/main/resources/application-oauth2.yaml`에 있으며,
`oauth2` 프로필이 활성화될 때만 적용됩니다.

- 프로필 활성화: `SPRING_PROFILES_ACTIVE=oauth2`
- 필요한 환경 변수: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`
- 예시 템플릿: `.env.example` (주의: Spring Boot가 `.env`를 자동 로드하는 것은 아니므로, 실행 환경에 export가 필요합니다.)

---

## 7) 더 보기

- 구조 문서: `docs/security-structure.md`
- 쿠키 정책: `docs/security-cookie-policy.md`
- 모듈 평가/개선 포인트: `docs/security-module-review.md`

