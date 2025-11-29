
## 配置：预约查询天数（scheduleQueryDays）

### 接口描述
查询和设置从“今天”起可查询/可预约的天数窗口。该配置会影响医生排班查询范围以及预约下单的日期校验。

- 接口路径（查询）: `/config/scheduleQueryDays`
- 请求方式: `GET`

#### 无需参数

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": 12
}
```

- 接口路径（设置）: `/config/scheduleQueryDays`
- 请求方式: `POST`
- Content-Type: `application/json`

#### Body（示例）
```json
{
  "days": 12
}
```

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": 12
}
```

#### 规则说明
- 取值范围：`1` 到 `60`，超出范围返回错误。
- 该值用于：
  - 医生排班查询 `/getDoctor` 的日期上限（从当天起向后 `scheduleQueryDays` 天）。
  - 预约下单时的日期限制（仅可预约 `scheduleQueryDays` 日内号源）。

---

## 配置：每日放号时间（scheduleQueryTime）

### 接口描述
查询和设置每日开始“放号”的时间点（小时与分钟）。预约下单仅在此时间之后允许进行。

- 接口路径（查询）: `/config/scheduleQueryTime`
- 请求方式: `GET`

#### 无需参数

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": [6, 30]
}
```
说明：`data[0]` 为小时（`0-23`），`data[1]` 为分钟（`0-59`）。

- 接口路径（设置）: `/config/scheduleQueryTime`
- 请求方式: `POST`
- Content-Type: `application/json`

#### Body（示例）
```json
{
  "hours": 7,
  "minutes": 0
}
```

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": [7, 0]
}
```

#### 规则说明
- 预约下单会在当前时间早于设定的每日放号时间时返回错误提示，提示文案如：`当前未到放号时间（每日HH:MM开始）`。
