# Security 모듈 평가 (리뷰)

대상: `src/main/java/com/study/security/application/security`

이 문서는 현재 구현을 기준으로 **구조/품질/보안 리스크**를 정리하고, 우선순위 기반 개선안을 제안합니다.

## 요약

- 장점: 로컬 로그인/JWT/OAuth2 흐름이 패키지로 잘 분리되어 있고, 공통 응답 유틸(`SecurityResponseSender`)과 쿠키 정책(`SecurityCookieProperties`) 같은 “재사용 지점”이 존재합니다.
- 우려(중요): 일부 설정/예외 처리 타입 불일치로 인해 **인가/인증 실패가 401/403이 아닌 500으로 떨어질 가능성**, 그리고 URL 권한 규칙이 **사실상 무력화될 가능성**이 보입니다.

## 잘 된 점

- **Stateless 구성 명확**: `SecurityConfig`에서 세션/RequestCache 비활성화로 API 성격을 분명히 함. (`src/main/java/com/study/security/application/security/config/SecurityConfig.java`)
- **기능별 모듈화**: JWT(`jwt/`), 로컬 로그인(`local/`), OAuth2(`oauth/`)가 독립적인 provider/filter/handler/service로 분리되어 확장/교체가 용이.
- **공통 응답 전송 통일**: 핸들러/필터가 `SecurityResponseSender`를 통해 JSON 응답 형식을 통일. (`src/main/java/com/study/security/application/security/common/util/SecurityResponseSender.java`)
- **OAuth2 쿠키 정책 검증**: `SameSite=None`일 때 `Secure=true`를 강제하는 검증이 있어 운영 환경 실수를 줄임. (`src/main/java/com/study/security/application/security/config/properties/SecurityCookieProperties.java`)
- **블랙리스트 키 해시화**: Redis 저장 키를 토큰 원문 대신 SHA-256 해시로 구성하여 유출 시 피해를 완화. (`src/main/java/com/study/security/application/security/jwt/repository/impl/RedisTokenBlacklistStore.java`)

## 주요 리스크/개선 포인트

### Critical: URL 권한 규칙이 무력화될 수 있음

- `SecurityConstants.PUBLIC_URLS`에 `"/**"`가 포함되어 있어, `authorizeHttpRequests()`에서 사실상 모든 요청이 `permitAll()`로 처리될 수 있습니다.
  - 위치: `src/main/java/com/study/security/application/security/common/constants/SecurityConstants.java`
  - 영향: `SECURE_URLS`/`ADMIN_URLS`/`anyRequest().authenticated()` 규칙이 기대대로 동작하지 않을 가능성이 큼.
  - 권장: `PUBLIC_URLS`에서 `"/**"` 제거 후 실제 공개 엔드포인트만 명시(예: Swagger, 로그인/회원가입, OAuth2 엔드포인트 등).

### High: 필터 체인 예외 처리에서 예외 타입이 Spring Security와 불일치

- `FilterChainExceptionFilter`가 다음 타입을 사용하고 있습니다.
  - `javax.security.sasl.AuthenticationException`
  - `java.nio.file.AccessDeniedException`
- 그러나 Spring Security에서 주로 발생하는 타입은 아래입니다.
  - `org.springframework.security.core.AuthenticationException`
  - `org.springframework.security.access.AccessDeniedException`
- 결과적으로 인증/인가 예외가 “재던지기” catch에 걸리지 않고, 일반 `Exception` catch로 떨어져 **500 응답**으로 마무리될 수 있습니다.
  - 위치: `src/main/java/com/study/security/application/security/exception/filter/FilterChainExceptionFilter.java`
  - 영향: 401/403 규격 응답이 깨지고, 프론트/클라이언트의 인증 플로우(리다이렉트/토큰 갱신/로그아웃 처리)가 흔들릴 수 있음.
  - 권장: Spring Security 예외 타입으로 catch를 정정하고, 현재의 401/403 처리(`CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`)가 정상 작동하도록 경로를 보장.

### Medium: JWT 인증 필터의 실패 처리 정책이 “로그만 남기고 통과”

- `JwtAuthenticationFilter`는 `JwtException` 등을 catch한 뒤 로그만 남기고 다음 필터로 진행합니다.
  - 위치: `src/main/java/com/study/security/application/security/jwt/filter/JwtAuthenticationFilter.java`
  - 영향:
    - 보호 엔드포인트에서는 결국 401/403이 나겠지만, “잘못된 토큰을 보낸 요청”과 “토큰이 없는 요청”이 동일하게 익명 처리됩니다.
    - 보안상 치명적이진 않더라도, 디버깅/관측 측면에서 구분이 어려움.
  - 권장(선택): 정책을 명확히 결정
    - A) invalid token이면 즉시 401 JSON 응답
    - B) 지금처럼 익명 처리(단, 관측/메트릭/로그를 명확히)

### Medium: Refresh Token 쿠키 SameSite=Strict 고정의 운영 적합성 확인 필요

- `JwtCookieProvider`는 refresh token 쿠키를 `SameSite=Strict`로 고정하고 있습니다.
  - 위치: `src/main/java/com/study/security/application/security/jwt/provider/JwtCookieProvider.java`
  - 영향:
    - 프론트가 **다른 오리진**(예: `localhost:3000`)에서 API 호출을 하는 구조라면, 브라우저가 쿠키를 보내지 않아 refresh 플로우가 깨질 수 있습니다(요청 컨텍스트에 따라 상이).
  - 권장:
    - 배포 토폴로지(프론트/백엔드 same-site 여부)를 기준으로 `Strict/Lax/None`을 결정하고, OAuth2처럼 프로퍼티 기반으로 조정 가능하게 만드는 것을 고려.

### Low: 로그인 필터에서 ObjectMapper 직접 생성

- `CustomLoginAuthenticationFilter`가 `new ObjectMapper()`를 내부에서 직접 생성합니다.
  - 위치: `src/main/java/com/study/security/application/security/local/filter/CustomLoginAuthenticationFilter.java`
  - 영향: 글로벌 Jackson 설정(날짜/모듈/보안 설정 등)과 불일치할 수 있음.
  - 권장: 애플리케이션 컨텍스트의 `ObjectMapper` 주입 사용(필요 시).

## 개선 우선순위(추천)

1. `PUBLIC_URLS`에서 `"/**"` 제거 및 실제 공개 URL만 허용하도록 정리.
2. `FilterChainExceptionFilter`에서 Spring Security 예외 타입으로 정정하여 401/403/500 경계가 깨지지 않게 함.
3. Refresh Token 쿠키 SameSite 정책을 배포 구조에 맞게 재검토(동일 사이트면 Strict 유지 가능, 교차 사이트면 None+Secure 또는 Lax 검토).
4. JWT invalid token 처리 정책 결정(즉시 401 vs 익명 통과) 후 로그/메트릭 정리.
5. (선택) 커스텀 로그인 필터의 `ObjectMapper` 주입으로 일관성 확보.

## 참고 문서

- 구조 문서: `docs/security-structure.md`
- OAuth2 쿠키 정책: `docs/security-cookie-policy.md`

