# LLM 模块说明

## 概述
- 目的：为用户提供常见问题的快捷提问入口，同时支持自主提问；回答聚焦本系统的功能与用法。
- 依赖：SiliconFlow Chat Completions API。

## 数据库表
- 表名：`llmstore`
- 字段：
  - `id` BIGINT PK 自增
  - `prompt` TEXT（system 提示，可空）
  - `message` TEXT（用户问题内容，必填）
  - `create_time` DATETIME（默认当前时间）
  - `update_time` DATETIME（更新自动刷新）

> 如果你的数据库采用下划线命名（如 `llm_store`），建议在实体 `LLMstore` 上添加 `@TableName("llm_store")` 保持映射一致。

### 建库 SQL
- 文件位置：`docs/sql/LLMstore.sql`
- 执行方式：在 MySQL 中直接运行该文件内容。

## 接口列表
- `GET /api/LLM/getCommonQuestions`
  - 返回常见问题集合，包含 `questionId` 与 `question`。
- `POST /api/LLM/ask`
  - 两种用法：
    - 点击常见问题：`?questionId=1`，请求体可空或空字符串；后端用该条 `prompt` 作为 system，`message` 作为 user。
    - 自主提问：纯文本请求体，例如：`"如何预约挂号？"`；后端使用默认 system prompt。
- `POST /api/LLM/admin/addCommonQuestion`
  - 管理员新增常见问题；请求体为 `LLMstore` JSON，需包含 `message`，`prompt` 可选。

## 请求/响应示例
### 新增常见问题（管理员）
请求：
```json
POST /api/LLM/admin/addCommonQuestion
Content-Type: application/json
{
  "message": "如何预约挂号？",
  "prompt": "你是医院后台的智能助手，只回答与本系统功能和使用相关的问题。"
}
```
响应：
```json
{
  "code": 200,
  "msg": "新增成功",
  "data": {
    "id": 1,
    "message": "如何预约挂号？",
    "prompt": "你是医院后台的智能助手，只回答与本系统功能和使用相关的问题。"
  }
}
```

### 获取常见问题
请求：`GET /api/LLM/getCommonQuestions`
响应（示例）：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    { "questionId": 1, "question": "如何预约挂号？" },
    { "questionId": 2, "question": "怎么查看医生的排班？" }
  ]
}
```


## curl 示例
```
# 新增常见问题（管理员）
curl -s -X POST http://localhost:8089/api/LLM/admin/addCommonQuestion \
  -H "Content-Type: application/json" \
  -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYzNzU1NTI1LCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYzNTc1NTI1LCJ1c2VybmFtZSI6ImFkbWluIn0.a5MEwwZZL5zSmTi-nwGl3d4C0mAJ5XG0orx0xIj1dps" \
  -d '{"message":"如何预约挂号？","prompt":"你是医院后台的智能助手，只回答与本系统功能和使用相关的问题。"}' | jq

# 获取常见问题
curl -s http://localhost:8089/api/LLM/getCommonQuestions -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYzNzU1NTI1LCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYzNTc1NTI1LCJ1c2VybmFtZSI6ImFkbWluIn0.a5MEwwZZL5zSmTi-nwGl3d4C0mAJ5XG0orx0xIj1dps" | jq


# 自主提问
curl -s -X POST 'http://localhost:8080/api/LLM/ask' -H 'Content-Type: application/json' -d '"怎么查看医生排班？"' | jq
```

## 配置项
- `llm.siliconflow.apiKey`：必填，SiliconFlow API Key。
- `llm.siliconflow.model`：可选，默认 `Qwen/Qwen3-30B-A3B-Thinking-2507`。
- `llm.defaultSystemPrompt`：可选，默认聚焦本系统功能与用法。

## 行为说明
- 当携带 `questionId`：
  - 使用该记录的 `prompt` 作为 system 提示；
  - 使用该记录的 `message` 作为用户问题；
- 当仅有自由文本 `message`：
  - 使用默认 system 提示；
- 返回统一结构：`{ code, msg, data }`，`data` 为 LLM 文本答案或列表数据。

## 更新说明（接口支持 GET/POST）
- 为了兼容 GET 场景，`/api/LLM/ask` 现支持 GET 与 POST 两种方式：
  - 点击常见问题（仅 `questionId`）：
    - GET：`/api/LLM/ask?questionId=1`
  - 自主提问（传 `message`）：
    - POST（推荐）：请求体为纯文本字符串，例如 `"如何预约挂号？"`

示例：
```
# GET 常见问题
curl -s -G 'http://localhost:8089/api/LLM/ask?questionId=1' --data-urlencode 'questionId=1' -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYzNzU1NTI1LCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYzNTc1NTI1LCJ1c2VybmFtZSI6ImFkbWluIn0.a5MEwwZZL5zSmTi-nwGl3d4C0mAJ5XG0orx0xIj1dps" | jq

# POST 自主提问
curl -s -X POST 'http://localhost:8089/api/LLM/ask' -H 'Content-Type: application/json' -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYzNzU1NTI1LCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYzNTc1NTI1LCJ1c2VybmFtZSI6ImFkbWluIn0.a5MEwwZZL5zSmTi-nwGl3d4C0mAJ5XG0orx0xIj1dps" -d '"怎么查看医生排班？"' | jq


认证：在请求头携带 `token: <JWT>` 或 `Authorization: Bearer <JWT>` 均可。