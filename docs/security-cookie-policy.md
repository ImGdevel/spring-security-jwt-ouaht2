# Security Cookie 정책

## OAuth2 쿠키는 `SameSite=Lax`가 기본

OAuth2 로그인은 일반적으로 다음 흐름을 탑니다.

1. 우리 서버가 OAuth2 인가 요청 정보를 쿠키에 저장
2. 브라우저가 OAuth Provider(구글/깃허브 등)로 이동
3. Provider가 브라우저를 다시 우리 서버의 콜백 URL로 리다이렉트(Top-level GET)

이때 3번은 “외부 사이트에서 시작된 내비게이션(cross-site)” 컨텍스트가 되기 쉬워서,
`SameSite=Strict` 쿠키는 브라우저가 전송하지 않는 경우가 많습니다.  
결과적으로 인가 요청 쿠키가 빠져 OAuth2 플로우가 실패할 수 있어 기본값을 `Lax`로 둡니다.

## 설정

- `spring.security.cookie.secure`
  - `true`: HTTPS 환경 권장(운영)
  - `false`: HTTP 개발 환경(로컬)에서 사용
- `spring.security.cookie.oauth2-same-site`
  - 기본값 `Lax`
  - `None`을 사용할 경우 브라우저 정책상 `Secure`가 필수이므로 `spring.security.cookie.secure=true`가 필요합니다.
