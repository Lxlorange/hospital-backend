# 医生信息更新审批 API 文档

本文档描述医生个人主页信息更新的审批流程及相关 API，包含医生端提交与查询、管理员端审批与列表查询。当前系统使用自定义请求头 `token` 进行登录态传递（不要使用 `Authorization: Bearer`）。

## 概览
- 审批流程：医生提交更新申请 → 管理员审核（通过/拒绝） → 通过后变更正式生效。
- 去重策略：同一医生存在“待审核”申请时再次提交会更新该申请内容，不会生成重复记录。
- 安全建议：仅使用 `POST /api/doctorProfile/updateMyProfile` 进行更新申请。旧接口 `POST /api/doctorProfile/updateDoctorProfile` 直接改库，存在越权风险，勿对外使用。

## 认证说明
- 请求头携带认证信息：`token: {登录后获取的token}`
- 后端拦截器从请求头 `token` 或查询参数 `token` 读取令牌；不要使用 `Authorization` 头。

## 状态值
- `status = "0"`：待审核
- `status = "1"`：已通过（系统应用更改）
- `status = "2"`：已拒绝

## 医生端接口

### 1) 获取医生个人主页信息
- URL：`GET /api/doctorProfile/getDoctorProfile?doctorId={id}`
- 权限：公开
- 响应：`DoctorProfileVo`（来源 `SysUser`，含科室名称）

示例：
```
GET /api/doctorProfile/getDoctorProfile?doctorId=25
token: {可选}
```

### 2) 获取当前登录医生的个人主页信息
- URL：`GET /api/doctorProfile/getMyProfile`
- 权限：医生登录

示例：
```
GET /api/doctorProfile/getMyProfile
token: {医生登录token}
```

### 3) 提交个人信息更新申请（走审批）
- URL：`POST /api/doctorProfile/updateMyProfile`
- 权限：医生登录
- 说明：仅以“当前登录用户”为准，前端无需传 `userId`；如存在待审申请，将更新该申请内容。
- 请求体（JSON）：
```
{
  "introduction": "擅长心血管疾病诊疗",
  "visitAddress": "门诊楼2层205室",
  "goodAt": "高血压、冠心病",
  "price": 200
}
```
- 成功响应：`{"code":200, "msg":"更新申请已提交，等待管理员审核"}`

curl 示例：
```
curl -X POST \
  http://localhost:8080/api/doctorProfile/updateMyProfile \
  -H 'Content-Type: application/json' \
  -H 'token: {医生登录token}' \
  -d '{
    "introduction": "擅长心血管疾病诊疗",
    "visitAddress": "门诊楼2层205室",
    "goodAt": "高血压、冠心病",
    "price": 200
  }'
```

### 4) 医生查看自己的更新申请列表
- URL：`GET /api/doctorUpdateRequest/my`
- 权限：医生登录
- 查询参数：`currentPage`（默认1）、`pageSize`（默认10）、`status`（可选）

curl 示例：
```
curl -X GET 'http://localhost:8080/api/doctorUpdateRequest/my?currentPage=1&pageSize=10&status=0' \
  -H 'token: {医生登录token}'
```

## 管理端接口

### 1) 管理员查看申请列表
- URL：`GET /api/doctorUpdateRequest/list`
- 权限：管理员（`hasAuthority('sys:admin')`）
- 查询参数：`currentPage`（默认1）、`pageSize`（默认10）、`status`（可选、0/1/2）

curl 示例：
```
curl -X GET 'http://localhost:8080/api/doctorUpdateRequest/list?currentPage=1&pageSize=10&status=0' \
  -H 'token: {管理员登录token}'
```

### 2) 管理员查看申请详情
- URL：`GET /api/doctorUpdateRequest/detail/{requestId}`
- 权限：管理员

curl 示例：
```
curl -X GET http://localhost:8080/api/doctorUpdateRequest/detail/1 \
  -H 'token: {管理员登录token}'
```

### 3) 管理员审核申请（通过/拒绝）
- URL：`GET api/doctorUpdateRequest/review`
- 权限：管理员
- 表单参数：
  - `requestId`：申请ID（必填）
  - `status`：审核状态（`1`通过、`2`拒绝）
  - `reviewComment`：审核意见（可选）

curl 示例（通过）：
```
curl -G 'http://localhost:8089/api/doctorUpdateRequest/review' -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYwNzY2NzMyLCJ1c2VySWQiOiIxIiwiaWF0IjoxNzYwNTg2NzMyLCJ1c2VybmFtZSI6ImFkbWluIn0.oPb5WqRkZjMQL5nZkihWDFLeohOTKWhWHJPUtiZo0B4" --data-urlencode "requestId=1" --data-urlencode "status=1" --data-urlencode "reviewComment=审核通过"
```

curl 示例（拒绝）：
```
curl -X POST http://localhost:8080/api/doctorUpdateRequest/review \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'token: {管理员登录token}' \
  -d 'requestId=1&status=2&reviewComment=信息不完整，请补充介绍'
```

## 返回结构约定
- 统一返回：`{ code, msg, data }`
  - `code=200` 表示成功；其他为失败
  - `msg` 为文字说明；`data` 为具体数据或 `null`

## 数据库表结构（doctor_update_request）
| 字段 | 类型 | 说明 |
| --- | --- | --- |
| request_id | bigint | 主键，申请ID |
| doctor_id | bigint | 医生ID（关联 `sys_user.user_id`） |
| username | varchar(50) | 医生用户名 |
| nick_name | varchar(50) | 医生昵称 |
| introduction | text | 医生简介（申请值） |
| visit_address | varchar(255) | 就诊地址（申请值） |
| good_at | varchar(255) | 擅长领域（申请值） |
| price | decimal(10,2) | 挂号价格（申请值） |
| status | char(1) | 状态：0-待审核，1-已通过，2-已拒绝 |
| review_comment | varchar(255) | 审核意见 |
| reviewer_id | bigint | 审核人ID（关联 `sys_user.user_id`） |
| review_time | datetime | 审核时间 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

## 使用建议与注意事项
- 医生端：重复提交会覆盖“待审核”申请的内容，不会生成多条待审记录。
- 管理端：通过后系统会将申请的字段（简介/地址/擅长/价格）同步到医生信息表。
- 安全：请确保仅对管理员开放审批接口；医生端仅可提交与查询自身申请。
- 令牌传递：务必使用请求头 `token`；若使用 `Authorization: Bearer` 会导致认证失败。

## 快速测试流程
1. 医生登录，调用 `POST /api/doctorProfile/updateMyProfile` 提交申请。
2. 管理员登录，调用 `GET /api/doctorUpdateRequest/list` 查看待审核。
3. 管理员调用 `POST /api/doctorUpdateRequest/review` 审核通过。
4. 医生调用 `GET /api/doctorProfile/getMyProfile` 或 `GET /api/doctorProfile/getDoctorProfile?doctorId={id}` 验证信息已更新。