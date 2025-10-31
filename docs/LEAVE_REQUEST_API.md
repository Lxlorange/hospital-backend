# 医生请假申请 API 文档

本文档描述医生请假申请的提交流程、列表查询与审批接口，包含三类接口：提交请假、查询列表、审批通过/拒绝。返回结构采用统一 `{ code, message, data }` 形式。

## 概览
- 场景：医生提交请假，管理员或授权人员审批；审批通过后，业务可根据需要对排班进行停诊或调整（本接口不直接改排班）。
- 状态值：`status = "0"` 待审核，`status = "1"` 已通过，`status = "2"` 已拒绝。
- 认证说明：与现有系统一致，登录后端通过请求头 `token` 或相关机制鉴权；若你的环境使用 `Authorization: Bearer`，请按需调整。

## 数据结构
- 表：`leave_request`
- 字段：
  - `requestId`：申请ID（自增主键）
  - `doctorId`：医生ID（示例：`D001`），字符串类型
  - `nickName`：医生昵称
  - `startDate` / `endDate`：请假开始/结束日期（`YYYY-MM-DD` 字符串）
  - `startTime` / `endTime`：时段（`0=上午`，`1=下午`）
  - `reason`：请假原因
  - `status`：`0=待审核`，`1=通过`，`2=拒绝`
  - `reviewComment`：审批意见
  - `reviewerId`：审批人ID（可空）
  - `reviewTime`：审批时间
  - `createTime` / `updateTime`：创建/更新时间

## 接口定义

### 1) 申请请假
- URL：`POST /api/requestLeave`
- 请求体（JSON）：
```json
{
  "doctorId": "D001",
  "nickName": "张三",
  "startDate": "2025-10-30",
  "endDate": "2025-11-01",
  "startTime": "0",
  "endTime": "1",
  "reason": "身体不适，需要休息两天"
}
```
- 成功响应：
```json
{
  "code": 200,
  "message": "申请成功",
  "data": {
    "requestId": 12,
    "status": "0"
  }
}
```
- 失败响应示例：`{"code":500,"message":"参数不完整","data":null}`

curl 示例：
```bash
curl -X POST "${BASE_URL}/api/requestLeave" \
  -H 'Content-Type: application/json' \
  -H 'token: <医生登录token>' \
  -d '{
    "doctorId":"D001",
    "nickName":"张三",
    "startDate":"2025-10-30",
    "endDate":"2025-11-01",
    "startTime":"0",
    "endTime":"1",
    "reason":"身体不适，需要休息两天"
  }'
```

### 2) 获取请假申请列表
- URL：`GET /api/leaveRequest/list`
- 查询参数：
  - `currentPage`（默认1）
  - `pageSize`（默认10）
  - `status`（可选，`0/1/2`）
  - `doctorId`（可选，字符串）
- 成功响应（分页对象，示例）：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "requestId": 1,
        "doctorId": "D001",
        "nickName": "张三",
        "startDate": "2025-10-30",
        "endDate": "2025-11-01",
        "startTime": "0",
        "endTime": "1",
        "reason": "身体不适",
        "status": "0",
        "reviewComment": null,
        "reviewerId": null,
        "reviewTime": null,
        "createTime": "2025-10-28T09:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

curl 示例：
```bash
curl -sS "${BASE_URL}/api/leaveRequest/list?currentPage=1&pageSize=10&status=0&doctorId=D001" \
  -H 'token: <管理员或医生登录token>'
```

### 3) 审批请假（通过/拒绝）
- URL：`POST /api/leaveRequest/approve`
- 请求体（JSON）：
```json
{
  "requestId": 1,
  "status": 1,
  "reviewComment": "信息有效，批准请假"
}
```
- 成功响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "requestId": 1,
    "reviewComment": "信息有效，批准请假"
  }
}
```
- 失败响应示例：`{"code":500,"message":"审核失败","data":null}`

curl 示例（通过）：
```bash
curl -X POST "${BASE_URL}/api/leaveRequest/approve" \
  -H 'Content-Type: application/json' \
  -H 'token: <管理员登录token>' \
  -d '{"requestId":1,"status":1,"reviewComment":"信息有效，批准请假"}'
```

curl 示例（拒绝）：
```bash
curl -X POST "${BASE_URL}/api/leaveRequest/approve" \
  -H 'Content-Type: application/json' \
  -H 'token: <管理员登录token>' \
  -d '{"requestId":1,"status":2,"reviewComment":"请假时间过长，无法批准"}'
```

## 返回结构约定
- 成功：`{"code":200, "message": "...", "data": ...}`
- 失败：`{"code":500, "message": "...", "data": null}` 或项目内约定的错误码

## 设计建议与注意事项
- 参数校验：`doctorId/nickName/startDate/endDate/startTime/endTime` 为必填；`reason` 建议必填，便于审批。
- 权限：如需严格控制审批权限，可参考“医生信息更新审批”模块，对审批接口增加管理员校验（查询当前登录用户是否为管理员）。
- 数据类型：为与前端示例对齐，`startDate/endDate/startTime/endTime` 按字符串处理；若改用日期/枚举类型，请同步调整实体/表结构。
- 审批后续：通过后可由业务对接排班停诊（例如将对应天的 `ScheduleDetail.type` 置为休息或减少号源）；本接口不直接改动排班。