# 身份认证申请与审批 API 文档

本文档描述身份认证（如学生证等）的提交流程、列表查询与审批接口，包含管理端与小程序端 5 个接口。返回结构采用统一 `{ code, msg/message, data }` 形式。

## 概览
- 流程：小程序提交认证申请 → 管理端审核（通过/拒绝）。
- 状态值：`status = "0"` 待审核，`status = "1"` 已通过，`status = "2"` 已拒绝。
- 资源路径：上传成功返回 `/images/<uuid>.<ext>`，需正确配置静态资源映射。

## 数据结构
- 表：`identity_auth_request`
- 字段：
  - `requestId`：申请ID（自增主键）
  - `userId`：提交用户ID（小程序用户，可空）
  - `username`：提交用户名（展示用，可空）
  - `type`：用户类型（示例：`学生`）
  - `code`：证件号/学号（示例：`23301001`）
  - `frontPhoto`：证件照（正面）
  - `backPhoto`：证件照（反面）
  - `status`：`0/1/2`（待审/通过/拒绝）
  - `reviewComment`：审核意见
  - `reviewerId`：审核人ID（后台用户，可空）
  - `reviewTime`：审核时间
  - `createTime` / `updateTime`：创建 / 更新时间

示例 DDL：
```sql
CREATE TABLE identity_auth_request (
  request_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id      INT NULL,
  username     VARCHAR(64) NULL,
  type         VARCHAR(32) NOT NULL,
  code         VARCHAR(64) NOT NULL,
  front_photo  VARCHAR(255) NOT NULL,
  back_photo   VARCHAR(255) NOT NULL,
  status       CHAR(1) NOT NULL,
  review_comment VARCHAR(255) NULL,
  reviewer_id  BIGINT NULL,
  review_time  DATETIME NULL,
  create_time  DATETIME NOT NULL,
  update_time  DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 管理端接口

### 1) 获取验证身份申请列表
- 路径：`GET /api/auth/list`
- Query 参数：无（按创建时间倒序）
- 成功响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "requestId": 1,
        "userId": 1,
        "username": "李",
        "type": "学生",
        "code": "23301001",
        "createTime": "2025-01-01T00:00:00",
        "frontPhoto": "/images/xxx-front.jpg",
        "backPhoto": "/images/xxx-back.jpg",
        "status": "0",
        "reviewComment": null,
        "reviewerId": null,
        "reviewTime": null,
        "updateTime": "2025-01-01T00:00:00"
      }
    ]
  }
}
```

curl 示例：
```bash
curl -sS "http://localhost:8089/api/auth/list" -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYyNTkwMjAyLCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYyNDEwMjAyLCJ1c2VybmFtZSI6ImFkbWluIn0.gl10XZ2d7l09YiZdYHf7S9nBmn7ceTyJVY8Hu6ZTAhg"
```

### 2) 批准身份认证
- 路径：`POST /api/auth/approve`
- Content-Type：`application/json`
- 请求体：
```json
{ "requestId": "1", "reviewComment": "审核通过" }
```
- 成功响应：
```json
{ "code":200, "message":"success", "data": { "requestId": 1, "reviewComment": "审核通过" } }
```

curl 示例：
```bash
curl -X POST http://localhost:8089/api/auth/approve -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYyNTkwMjAyLCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYyNDEwMjAyLCJ1c2VybmFtZSI6ImFkbWluIn0.gl10XZ2d7l09YiZdYHf7S9nBmn7ceTyJVY8Hu6ZTAhg"\
  -H 'Content-Type: application/json' \
  -d '{"requestId":"1","reviewComment":"审核通过"}'
```

### 3) 驳回身份认证
- 路径：`POST /api/auth/reject`
- Content-Type：`application/json`
- 请求体：
```json
{ "requestId": "1", "reviewComment": "资料不完整" }
```
- 成功响应：
```json
{ "code":200, "message":"success", "data": { "requestId": 1, "reviewComment": "资料不完整" } }
```

curl 示例：
```bash
curl -X POST http://localhost:8089/api/auth/reject \
  -H 'Content-Type: application/json' \
  -d '{"requestId":"1","reviewComment":"资料不完整"}'
```

## 小程序端接口

### 4) 上传身份认证照片
- 路径：`POST /wxapi/allApi/uploadPhoto`
- 请求：`form-data`，字段名为 `file`
- 校验：文件类型白名单（`.jpg/.jpeg/.png/.gif`）
- 成功响应：
```json
{ "code":200, "message":"上传成功", "data":"/images/<uuid>.<ext>" }
```

curl 示例：
```bash
curl -X POST http://localhost:8089/wxapi/allApi/uploadPhoto -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYyNTkwMjAyLCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYyNDEwMjAyLCJ1c2VybmFtZSI6ImFkbWluIn0.gl10XZ2d7l09YiZdYHf7S9nBmn7ceTyJVY8Hu6ZTAhg"\
  -F 'file=@./front.jpg'
```

### 5) 提交身份认证申请
- 路径：`POST /wxapi/allApi/submitAuth`
- Content-Type：`application/json`
- 请求体：
```json
{
  "userType": "学生",
  "cardNo": "23301001",
  "cardFront": "/images/xxx-front.jpg",
  "cardBack": "/images/xxx-back.jpg",
  "userId": 1,
  "username": "李"
}
```
- 成功响应：
```json
{ "code":200, "message":"提交成功", "data": null }
```

curl 示例：
```bash
curl -X POST http://localhost:8089/wxapi/allApi/submitAuth -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYyNTkwMjAyLCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYyNDEwMjAyLCJ1c2VybmFtZSI6ImFkbWluIn0.gl10XZ2d7l09YiZdYHf7S9nBmn7ceTyJVY8Hu6ZTAhg"\
  -H 'Content-Type: application/json' \
  -d '{
    "userType":"学生",
    "cardNo":"23301001",
    "cardFront":"/images/xxx-front.jpg",
    "cardBack":"/images/xxx-back.jpg",
    "userId":1,
    "username":"李"
  }'
```

## 返回结构约定
- 成功：`{"code":200, "msg/message":"...", "data": ...}`
- 失败：`{"code":500, "msg/message":"...", "data": null}`（或项目约定的错误码与信息）

## 配置说明（静态资源与上传目录）
- 控制器读取：`@Value("${web.uploadpath}")` 作为上传目录（相对项目根）。
- 静态映射：`/images/**` 由 `web.load-path` 指向的目录提供静态访问。
- 示例（`application-test.yml`，请根据环境创建并设置）：
```yaml
web:
  uploadpath: uploads/
  load-path: file:${user.dir}/uploads/
```

## 使用建议与注意事项
- 审核人：`reviewerId` 暂为可空；后续可对接后台登录上下文。
- 安全：图片上传需限制白名单类型；如需大小限制可在实现中增加校验（目前参考了 `ImageTransferController` 的模式）。
- 兼容：`/api/auth/list` 当前返回完整列表；如需分页，按项目模式扩展 `currentPage/pageSize` 并使用 MyBatis-Plus 分页。