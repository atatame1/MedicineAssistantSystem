# MedicineAssistantSystem 前端界面文档

## 约定

1. HTTP 基地址：`import.meta.env.VITE_API_BASE_URL`，未配置时默认 `http://localhost:8080`
2. 后端统一返回 `Result<T>`：`{ success, message, data }`
3. 本项目前端使用 `Vite + Vue3 + Vue Router + Pinia`

## 页面与后端接口映射

### Projects

`/projects`
- 请求：`GET /api/projects?status=1|2|3|4`
- 页面：`src/pages/projects/ProjectsList.vue`

`/projects/create`
- 请求：`POST /api/projects/create`
- body：`Project` JSON
- 关键字段：`aiAssess` 必填（后端校验）
- 页面：`src/pages/projects/ProjectsCreate.vue`

### Literatures

`/literatures`
- 请求：`GET /api/literatures?keyword=xxx`
- 页面：`src/pages/literatures/LiteraturesList.vue`

`/literatures/create`
- 请求：`POST /api/literatures/create`（`multipart/form-data`）
- parts：
  - `file`：PDF 文件二进制
  - `metadata`：`Literature` JSON（`Content-Type: application/json`）
- 页面：`src/pages/literatures/LiteraturesCreate.vue`

`/literatures/:literatureId/summary`
- 请求：`GET /api/literatures/summary?literatureId=...`（SSE）
- 输出：
  - 多次推送：chunk 字符串
  - 最后推送：`LiteratureSummaryResponse`（JSON 字符串）
- 页面：`src/pages/literatures/LiteratureSummaryStream.vue`

### User

`/user/:userId/settings`
- 请求：
  - `GET /api/user/{userId}/settings`
  - `POST /api/user/{userId}/settings/update`，body：`{ preferences: string }`
- 页面：`src/pages/user/UserSettings.vue`

`/user/:userId/favorites`
- 请求：
  - `GET /api/user/{userId}/favorites`
  - `GET /api/user/{userId}/favorites/statistics`
- 页面：`src/pages/user/UserFavorites.vue`

### Regulations

`/regulations`
- 请求：`GET /api/regulations?keyword=xxx`
- 页面：`src/pages/regulations/RegulationsList.vue`

