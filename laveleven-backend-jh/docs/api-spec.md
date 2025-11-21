# API 명세 (Backend Spring)

- Base URL: `/api`
- 인증: 모든 보호된 엔드포인트는 `Authorization: Bearer <JWT>` 헤더 필요
- 콘텐츠 타입: `application/json` (파일 업로드 제외)

## Auth
- `POST /auth/login` → body `{ username, password }` → `{ token, username, name, email, role, isActive }`
- `POST /auth/logout` → 200 OK
- `POST /auth/forgot-username` → `{ email }` → 200 OK
- `POST /auth/forgot-password-request` → `{ email }` → 200 OK
- `POST /auth/reset-password` → `{ email, newPassword, token }` → 200 OK

## User (JWT)
- `GET /user/profile` → `UserResponse { userId, username, email, role, isActive }`
- `PUT /user/password` → `{ currentPassword, newPassword }` → 200 OK
- `GET /user/history` → `HistoryResponse[]` (`{ id, type, fileName, date, time, status, userId }`)
- `DELETE /user/history` → body `[ "item-uuid1", ... ]` (미구현 placeholder)

## Label Pipeline (JWT)
- `POST /label/start-pipeline` → `{ itemName, itemType }` → `{ itemId }`
- `POST /label/log-ocr-history/{itemId}` (multipart) → form field `image` → `{ itemId, ocrResponse }`
- `POST /label/log-structure-history/{itemId}` → `{ language, texts: string[] }` → `{ itemId, structureResponse }`
- `POST /label/log-translate-history/{itemId}` → `{ language, data, targetCountry }` → `{ itemId, translateResponse }`
- `POST /label/transient/generate-html` → `{ country, data }` → `string` (HTML)
- `POST /label/pipeline/{itemId}` → `{ itemName, itemType, originalFileName, ocrResult, structureResult, translateResult, sketchResult }` → 200 OK

## History (JWT)
- `GET /history/{itemId}` → `ItemDetailResponse { itemName, ocrResult, structureResult, translateResult, sketchResult }`
- `GET /history/{itemId}/audit-logs` → `AuditLogResponse[]` (필드: historyId, itemId, stepName, fieldName, actionType, payload, changedByUsername, changedAt)

## Admin (JWT + ROLE_ADMIN)
- `GET /admin/users` → `UserResponse[]`
- `POST /admin/users` → `{ username, email, password, role, departmentId }` → `UserResponse`
- `DELETE /admin/users/{id}` → 200 OK
- `GET /admin/users/{id}` → `UserResponse`
- `GET /admin/users/{id}/history` → `HistoryResponse[]`
- `GET /admin/history` → 전체 히스토리 목록

---

## Swagger / OpenAPI 접근 방법
JWT 때문에 막혀 있을 경우 아래 중 하나 적용:
1) 보안 설정에서 Swagger 경로 허용  
   - `SecurityConfig`의 `authorizeHttpRequests`에 `.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()` 추가.
2) Swagger UI에서 JWT 입력 지원  
   - `springdoc-openapi` 사용 시 `@Bean OpenAPI`에 `http bearer` security scheme 추가 후, Swagger UI 오른쪽 상단 Authorize에 `Bearer <token>` 입력.
3) Swagger 없이 정의 파일만 필요하면: `http GET http://localhost:8080/v3/api-docs` (JWT 헤더 포함)으로 JSON 뽑아 공유.

위 설정을 추가하기 전이라면, Postman/Insomnia에서 본 명세대로 `Authorization: Bearer` 헤더만 붙여 호출하면 됩니다.
