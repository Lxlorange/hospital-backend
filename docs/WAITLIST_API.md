# 候补队列接口（Waitlist API）

> 基础路径：`/wxapi/allApi`

本页文档覆盖新添加的候补相关接口，包括加入候补与手动分配候补两条路径。接口返回统一采用项目的 `ResultVo` 封装。

---

## POST `/waitlist/join`

- 用途：在号源为 0 时，用户为指定排班加入候补队列。
- 入参（JSON Body）：
  - `scheduleId`：Integer，排班 ID（必填）
  - `userId`：Integer，小程序用户 ID（必填）
  - `visitUserId`：Integer，就诊人 ID（必填）
- 前置与校验：
  - 校验排班存在；且排班 `lastAmount == 0` 才允许候补。
  - 防重复：同就诊人在同一排班存在待候补记录（`status=pending`）时视为成功不重复插入。
  - 防已预约：同就诊人在同一排班已有预约订单（`status=1`）则拒绝候补。
- 成功响应：
  ```json
  {"code":200,"msg":"已加入候补队列","data":null}
  ```
- 失败响应（示例）：
  ```json
  {"code":500,"msg":"当前仍有余号，请直接预约，无需候补!","data":null}
  ```
- 示例请求：
  ```bash
  curl -X POST "http://localhost:8089/wxapi/allApi/waitlist/join" \
    -H "Content-Type: application/json" \
    -d '{"scheduleId":300,"userId":1001,"visitUserId":2001}'
  ```

---

## POST `/waitlist/allocate`

- 用途：手动触发为指定排班分配候补（当有余量时自动创建预约）。
- 入参（Query）：
  - `scheduleId`：Integer，排班 ID（必填）
- 行为与副作用：
  - 对排班行级加锁，校验 `lastAmount > 0` 才可分配。
  - 选取该排班候补队列中优先级与创建时间最靠前的一条 `pending` 记录。
  - 再次防重复预约校验（若已存在有效预约，候补记录标记为 `allocated` 并跳过创建）。
  - 创建预约订单（`MakeOrder`），随后 `subCount(scheduleId)` 减少号源。
  - 候补记录状态更新为 `allocated`，并通过 `JavaMailSender` 发送邮件通知。
- 成功响应：
  ```json
  {"code":200,"msg":"已分配一个候补到新余号","data":null}
  ```
- 失败响应（示例）：
  ```json
  {"code":500,"msg":"无候补或当前无余号","data":null}
  ```
- 示例请求：
  ```bash
  curl -X POST "http://localhost:8089/wxapi/allApi/waitlist/allocate?scheduleId=300"
  ```

---

## 相关实体与服务

- `WaitlistEntry`：候补记录实体，关键字段：`scheduleId`、`doctorId`、`userId`、`visitUserId`、`status(pending/allocated/canceled)`、`priority`、`createTime`、`updateTime`。
- `WaitlistService`：候补服务，包含：
  - `joinWaitlist(scheduleId, doctorId, userId, visitUserId)`
  - `allocateFromWaitlistForSchedule(scheduleId)`
  - `listPendingBySchedule(scheduleId, limit)`

---

## 联调建议路径（快速）

1. 获取可用的排班 ID（scheduleId）：
   - 可调用现有接口 `GET /api/home/getScheduleId?date=yyyy-MM-dd&timeSlot=0|1&doctorId=XXX`。
2. 若该排班 `lastAmount == 0`，调用 `POST /waitlist/join` 加入候补。
3. 当出现余号时，调用 `POST /waitlist/allocate?scheduleId=...` 触发分配，观察订单创建与邮件发送。

> 注：邮件发送需在 `application-test.yml` 配置 SMTP，且账号与发件人一致；数据库需支持事务与 `FOR UPDATE` 行级锁。