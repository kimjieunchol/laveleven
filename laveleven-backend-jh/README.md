# API 명세서 (Backend Spring Boot)

## 기본 정보

- **Base URL**: `/api`
- **인증**: 모든 보호된 엔드포인트는 `Authorization: Bearer <JWT>` 헤더 필요
- **Content-Type**: `application/json` (파일 업로드는 `multipart/form-data`)
- **응답 형식**: JSON

---

## 1. 인증 API (`/api/auth`)

### 로그인

- **POST** `/auth/login`
- **Request Body**:

```json
{
  "username": "string",
  "password": "string"
}
```

- **Response** (200 OK):

```json
{
  "token": "string",
  "userId": "string",
  "username": "string",
  "role": "string"
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "string"
}
```

### 사용자명 중복 체크

- **GET** `/auth/check-duplicate?username={username}`
- **Response** (200 OK):

```json
{
  "exists": true
}
```

### 토큰 갱신

- **POST** `/auth/refresh`
- **Request Body**:

```json
{
  "refreshToken": "string"
}
```

- **Response** (200 OK):

```json
{
  "message": "Refresh token logic not implemented yet"
}
```

- **Note**: 현재 구현 예정

### 로그아웃

- **POST** `/auth/logout`
- **Response** (200 OK):

```json
{
  "message": "Logged out successfully"
}
```

- **Note**: JWT는 stateless이므로 클라이언트에서 토큰 삭제 필요

---

## 2. 사용자 관리 API (`/api/users`)

**인증 필요**: JWT Required  
**권한**: 조회는 모든 권한 가능 (권한별 자동 필터링), 생성/수정/삭제는 `SUPER_ADMIN`, `ADMIN`만 가능

### 사용자 목록 조회

- **GET** `/users`
- **Response** (200 OK):

```json
[
  {
    "id": 1,
    "userId": "string",
    "username": "string",
    "role": "string",
    "team": "string",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

- **권한별 필터링**:
  - `SUPER_ADMIN`: 모든 사용자 조회
  - `ADMIN`: 자신의 팀 사용자만 조회
  - `USER`: 본인만 조회

### 사용자 단건 조회

- **GET** `/users/{id}`
- **Path Parameters**: `id` (Long, required) - 사용자 ID
- **Response** (200 OK):

```json
{
  "id": 1,
  "userId": "string",
  "username": "string",
  "role": "string",
  "team": "string",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

- **Response** (403 Forbidden): 권한 없음

### 사용자 생성

- **POST** `/users`
- **권한**: `SUPER_ADMIN`, `ADMIN`
- **Request Body**:

```json
{
  "userId": "string",
  "password": "string",
  "username": "string",
  "role": "USER | ADMIN | SUPER_ADMIN",
  "team": "string"
}
```

- **Response** (200 OK):

```json
{
  "id": 1,
  "userId": "string",
  "username": "string",
  "role": "string",
  "team": "string",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "이미 존재하는 사용자명입니다"
}
```

### 사용자 수정

- **PUT** `/users/{id}`
- **권한**: `SUPER_ADMIN`, `ADMIN`
- **Path Parameters**: `id` (Long, required) - 사용자 ID
- **Request Body**:

```json
{
  "username": "string",
  "role": "string",
  "team": "string",
  "isActive": true
}
```

- **Response** (200 OK):

```json
{
  "id": 1,
  "userId": "string",
  "username": "string",
  "role": "string",
  "team": "string",
  "isActive": true,
  "updatedAt": "2024-01-01T00:00:00"
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "권한이 없습니다"
}
```

### 사용자 삭제 (비활성화)

- **DELETE** `/users/{id}`
- **권한**: `SUPER_ADMIN`, `ADMIN`
- **Path Parameters**: `id` (Long, required) - 사용자 ID
- **Response** (200 OK):

```json
{
  "message": "사용자가 비활성화되었습니다."
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "권한이 없습니다"
}
```

---

## 3. 아이템 관리 API (`/api/items`)

**인증 필요**: JWT Required  
**권한**: 조회/검색은 모든 권한 가능 (권한별 자동 필터링), 생성/수정/삭제는 `SUPER_ADMIN`, `ADMIN`만 가능

### 아이템 목록 조회

- **GET** `/items`
- **Response** (200 OK):

```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "team": "string",
    "status": "string",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

- **권한별 필터링**:
  - `SUPER_ADMIN`: 모든 아이템 조회
  - `ADMIN`: 자신의 팀 아이템만 조회
  - `USER`: 자신의 팀 아이템만 조회

### 아이템 단건 조회

- **GET** `/items/{id}`
- **Path Parameters**: `id` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "team": "string",
  "status": "string",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "접근 권한이 없습니다"
}
```

### 아이템 검색

- **GET** `/items/search?keyword={keyword}`
- **Query Parameters**: `keyword` (String, required) - 검색 키워드
- **Response** (200 OK):

```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "team": "string",
    "status": "string"
  }
]
```

- **Note**: 권한별 자동 필터링 적용

### 아이템 생성

- **POST** `/items`
- **권한**: `SUPER_ADMIN`, `ADMIN`
- **Request Body**:

```json
{
  "name": "string",
  "description": "string",
  "team": "string",
  "status": "string"
}
```

- **Response** (200 OK):

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "team": "string",
  "status": "string",
  "createdAt": "2024-01-01T00:00:00"
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "권한이 없습니다"
}
```

### 아이템 수정

- **PUT** `/items/{id}`
- **권한**: `SUPER_ADMIN`, `ADMIN`
- **Path Parameters**: `id` (String, required) - 아이템 ID
- **Request Body**:

```json
{
  "name": "string",
  "description": "string",
  "status": "string"
}
```

- **Response** (200 OK):

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "team": "string",
  "status": "string",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### 아이템 삭제

- **DELETE** `/items/{id}`
- **권한**: `SUPER_ADMIN`, `ADMIN`
- **Path Parameters**: `id` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
{
  "message": "아이템이 삭제되었습니다."
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "권한이 없습니다"
}
```

---

## 4. 이력 관리 API (`/api/histories`)

**인증 필요**: JWT Required

### 전체 이력 조회

- **GET** `/histories`
- **Response** (200 OK):

```json
[
  {
    "id": "string",
    "itemId": "string",
    "stepName": "string",
    "data": {},
    "createdBy": "string",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

- **Note**: 권한별 자동 필터링 적용

### 특정 아이템의 이력 조회

- **GET** `/histories/item/{itemId}`
- **Path Parameters**: `itemId` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
[
  {
    "id": "string",
    "itemId": "string",
    "stepName": "string",
    "data": {},
    "createdBy": "string",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

- **Response** (403 Forbidden):

```json
{
  "error": "접근 권한이 없습니다"
}
```

### 특정 아이템의 특정 단계 이력 조회

- **GET** `/histories/item/{itemId}/step/{stepName}`
- **Path Parameters**:
  - `itemId` (String, required) - 아이템 ID
  - `stepName` (String, required) - 단계 이름
- **Response** (200 OK):

```json
[
  {
    "id": "string",
    "itemId": "string",
    "stepName": "string",
    "data": {},
    "createdBy": "string",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

- **Response** (403 Forbidden):

```json
{
  "error": "접근 권한이 없습니다"
}
```

### 이력 생성

- **POST** `/histories/item/{itemId}`
- **Path Parameters**: `itemId` (String, required) - 아이템 ID
- **Request Body**:

```json
{
  "stepName": "string",
  "data": {}
}
```

- **Response** (200 OK):

```json
{
  "id": "string",
  "itemId": "string",
  "stepName": "string",
  "data": {},
  "createdBy": "string",
  "createdAt": "2024-01-01T00:00:00"
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "접근 권한이 없습니다"
}
```

### 이력 단건 조회

- **GET** `/histories/{id}`
- **Path Parameters**: `id` (String, required) - 이력 ID
- **Response** (200 OK):

```json
{
  "id": "string",
  "itemId": "string",
  "stepName": "string",
  "data": {},
  "createdBy": "string",
  "createdAt": "2024-01-01T00:00:00"
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "접근 권한이 없습니다"
}
```

---

## 5. 파이프라인 API (`/api/pipeline`)

**인증 필요**: JWT Required

### OCR 처리

- **POST** `/pipeline/ocr`
- **Content-Type**: `multipart/form-data`
- **Request**: `file` (MultipartFile, required) - 이미지 파일
- **Response** (200 OK):

```json
{
  "text": "string",
  "confidence": 0.95,
  "language": "string"
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "string"
}
```

### 구조화 처리

- **POST** `/pipeline/structure`
- **Request Body**:

```json
{
  "language": "string",
  "texts": ["string"]
}
```

- **Response** (200 OK):

```json
{
  "structuredData": {}
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "string"
}
```

### 번역 처리

- **POST** `/pipeline/translate`
- **Request Body**:

```json
{
  "targetCountry": "string",
  "data": {}
}
```

- **Response** (200 OK):

```json
{
  "translatedData": {}
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "string"
}
```

### HTML 생성

- **POST** `/pipeline/html`
- **Request Body**:

```json
{
  "country": "string",
  "data": {}
}
```

- **Response** (200 OK):

```json
{
  "html": "string"
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "string"
}
```

### 전체 파이프라인 처리

- **POST** `/pipeline/full`
- **Content-Type**: `multipart/form-data`
- **Request Parameters**:
  - `file` (MultipartFile, required) - 이미지 파일
  - `targetCountry` (String, optional) - 번역 대상 국가
  - `generateHtml` (Boolean, optional, default: false) - HTML 생성 여부
- **Response** (200 OK):

```json
{
  "ocrResult": {},
  "structureResult": {},
  "translateResult": {},
  "htmlResult": "string"
}
```

- **Response** (400 Bad Request):

```json
{
  "error": "string"
}
```

### 스캔 결과 저장

- **POST** `/pipeline/save/scan?itemId={itemId}&imageUrl={imageUrl}`
- **Query Parameters**:
  - `itemId` (String, required) - 아이템 ID
  - `imageUrl` (String, required) - 이미지 URL
- **Request Body**: `OcrResponse` 객체
- **Response** (200 OK):

```json
{
  "success": true
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 스키마 데이터 저장

- **POST** `/pipeline/save/schema?itemId={itemId}`
- **Query Parameters**: `itemId` (String, required) - 아이템 ID
- **Request Body**: `Map<String, Object>`
- **Response** (200 OK):

```json
{
  "success": true
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 번역 데이터 저장

- **POST** `/pipeline/save/translate?itemId={itemId}`
- **Query Parameters**: `itemId` (String, required) - 아이템 ID
- **Request Body**: `Map<String, Object>`
- **Response** (200 OK):

```json
{
  "success": true
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 스케치 데이터 저장

- **POST** `/pipeline/save/sketch?itemId={itemId}`
- **Query Parameters**: `itemId` (String, required) - 아이템 ID
- **Request Body**:

```json
{
  "html": "string"
}
```

- **Response** (200 OK):

```json
{
  "success": true
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 스캔 데이터 조회

- **GET** `/pipeline/get/scan/{itemId}`
- **Path Parameters**: `itemId` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
{
  "data": {}
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 스키마 데이터 조회

- **GET** `/pipeline/get/schema/{itemId}`
- **Path Parameters**: `itemId` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
{
  "data": {}
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 번역 데이터 조회

- **GET** `/pipeline/get/translate/{itemId}`
- **Path Parameters**: `itemId` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
{
  "data": {}
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

### 스케치 데이터 조회

- **GET** `/pipeline/get/sketch/{itemId}`
- **Path Parameters**: `itemId` (String, required) - 아이템 ID
- **Response** (200 OK):

```json
{
  "html": "string"
}
```

- **Response** (403 Forbidden):

```json
{
  "error": "string"
}
```

---

## 6. 통계 API (`/api/stats`)

**인증 필요**: JWT Required

### 팀별 통계 조회

- **GET** `/stats/teams`
- **Response** (200 OK):

```json
[
  {
    "teamName": "string",
    "totalItems": 0,
    "completedItems": 0,
    "inProgressItems": 0
  }
]
```

### 전체 통계 조회

- **GET** `/stats/total`
- **Response** (200 OK):

```json
{
  "totalUsers": 0,
  "totalItems": 0,
  "totalCompleted": 0,
  "totalInProgress": 0
}
```

---

## Swagger / OpenAPI 접근 방법

JWT 인증 때문에 Swagger UI 접근이 제한될 경우 아래 방법 중 하나를 적용:

### 1. Swagger 경로 허용 (SecurityConfig)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

### 2. Swagger UI에서 JWT 입력

1. Swagger UI 접속: `http://localhost:8080/swagger-ui.html`
2. 오른쪽 상단 **Authorize** 버튼 클릭
3. `Bearer <your-jwt-token>` 형식으로 입력
4. **Authorize** 클릭

### 3. OpenAPI JSON 직접 다운로드

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/v3/api-docs > api-docs.json
```

**Note**: 위 설정 적용 전에는 Postman/Insomnia 등의 API 클라이언트에서 `Authorization: Bearer <token>` 헤더를 추가하여 테스트하시기 바랍니다.

---

## 공통 응답 코드

| Status Code | Description                            |
| ----------- | -------------------------------------- |
| 200         | 요청 성공                              |
| 400         | 잘못된 요청 (Bad Request)              |
| 401         | 인증 실패 (Unauthorized)               |
| 403         | 권한 없음 (Forbidden)                  |
| 404         | 리소스를 찾을 수 없음 (Not Found)      |
| 500         | 서버 내부 오류 (Internal Server Error) |

---

## 권한 레벨

| Role          | Description                         |
| ------------- | ----------------------------------- |
| `SUPER_ADMIN` | 모든 리소스에 대한 전체 권한        |
| `ADMIN`       | 자신의 팀 리소스에 대한 관리 권한   |
| `USER`        | 자신의 리소스에 대한 읽기/쓰기 권한 |
