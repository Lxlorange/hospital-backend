# 医生请假申请与审批 API 文档

本文档说明医生请假申请的提交流程、审批接口，以及审批通过后对排班与挂号订单的联动处理规则。

- 接口前缀（管理端/平台端）：`http://localhost:8089/api/leaveRequest`
- 手机端改签相关接口前缀：`http://localhost:8089/wxapi/allApi`

---

## 核心概念与状态

- 请假申请（`leave_request`）
  - `status`：`0` 待审核、`1` 通过、`2` 拒绝
- 排班（`schedule_detail`）
  - `type`：`1` 正常出诊、`0` 停诊（审批通过后置为 `0`）
- 挂号订单（`make_order`）
  - `status`：`1` 已预约、`2` 已取消、`3` 待改签（reschedule，审批通过后由 `1` 变更为 `3`）

---

## 1. 提交请假申请

- 路径：`POST /api/leaveRequest/requestLeave`
- Content-Type：`application/json`

### 请求体
| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `doctorId` | String | 是 | 医生ID（字符串形式） |
| `scheduleId` | Long | 是 | 待请假的排班ID（唯一） |
| `reason` | String | 否 | 请假原因 |

### 校验规则
- 医生ID格式与存在性校验。
- 排班存在性校验，且排班必须归属该医生。
- 排班状态校验：仅允许在 `type = "1"`（正常出诊）的排班上发起请假申请。

### 成功返回
```json
{"code":200,"msg":"申请成功","data":null}
```

---

## 2. 获取请假申请列表

- 路径：`GET /api/leaveRequest/list`

### Query 参数
| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `currentPage` | Long | 否 | 默认 `1` |
| `pageSize` | Long | 否 | 默认 `10` |
| `status` | String | 否 | `0`/`1`/`2` 筛选 |
| `doctorId` | String | 否 | 按医生ID筛选 |

### 成功返回
- 分页数据结构，包含请假申请记录列表。

---

## 3. 审批请假申请

- 路径：`POST /api/leaveRequest/approve`
- Content-Type：`application/json`

### 请求体
| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `requestId` | Long | 是 | 申请ID |
| `status` | String | 是 | 审批结果：`"1"` 通过、`"2"` 拒绝 |
| `reviewComment` | String | 否 | 审批意见 |

### 审批通过后的联动行为（事务内执行）
1. 更新请假申请记录的状态与审核信息。
2. 将对应 `schedule_detail` 的 `type` 置为 `"0"`（停诊）。
3. 查询该排班下所有 `make_order.status = "1"` 的订单，批量改为 `"3"`（待改签/reschedule）。

### 成功返回
```json
{"code":200,"msg":"success","data":{"requestId":123,"reviewComment":"同意"}}
```

---

## 4. 改签（待改签订单处理）

当订单因为医生请假而变更为 `status = "3"`（待改签）后，用户需在手机端选择同一医生的其他正常排班进行改签。

- 路径（手机端）：`POST /wxapi/allApi/rescheduleOrder`
- Content-Type：`application/json`

### 请求体
| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `makeId` | Integer | 是 | 原订单ID（状态需为 `3`） |
| `scheduleId` | Integer | 是 | 新排班ID（同一医生，且排班 `type = "1"`） |

### 业务校验
- 仅允许改签 `status = "3"` 的订单。
- 目标排班必须为同一医生，且 `type = "1"`（正常）。
- 同就诊人同排班若已有 `status = "1"` 的有效预约，不允许重复。

### 成功返回
```json
{"code":200,"msg":"改签成功!","data":null}
```

---

## 5. 接口调用示例

### 5.1 发起请假（curl）
```bash
curl -X POST http://localhost:8089/api/leaveRequest/requestLeave \
  -H 'Content-Type: application/json' \
  -d '{
    "doctorId": "24",
    "scheduleId": 227,
    "reason": "临时家中有事"
  }'
```

### 5.2 审批通过（curl）
```bash
curl -X POST http://localhost:8089/api/leaveRequest/approve \
  -H 'Content-Type: application/json' \
  -d '{
    "requestId": 1001,
    "status": "1",
    "reviewComment": "同意，停诊一天"
  }'
```

### 5.3 用户改签（curl，手机端）
```bash
curl -X POST http://localhost:8089/wxapi/allApi/rescheduleOrder \
  -H 'Content-Type: application/json' \
  -d '{
    "makeId": 25,
    "scheduleId": 233
  }'
```

---

## 6. 开发者备注
- 审批通过后的联动逻辑在 `LeaveRequestServiceImpl.approve()` 中实现，并使用事务保障一致性。
- 排班类型：`schedule_detail.type`，`"1"` 正常、`"0"` 停诊。
- 订单状态：`make_order.status`，`"1"` 已预约、`"2"` 已取消、`"3"` 待改签。
- 改签接口位于手机端控制器 `PhoneProjectController`，基路径为 `@RequestMapping("/wxapi/allApi")`。