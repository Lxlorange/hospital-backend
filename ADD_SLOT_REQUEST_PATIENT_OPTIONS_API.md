# 加号患者候选项查询 API

该接口用于在医生加号流程中，根据患者信息快速查询可选择的用户与就诊人候选项，为后续加号申请或审核创建订单提供 `userId` 与 `visitUserId`。

## 接口概览
- 路径：`/api/addSlotRequest/patientOptions`
- 方法：`GET`
- 鉴权：与加号接口同域，通常需要登录用户（医生/管理员）访问
- 查询条件（至少提供一个）：`phone`、`idCard`、`name`
- 查询优先级：`phone` > `idCard` > `name`（同时传入时按此顺序处理）

## 请求参数
- `phone`（可选）：微信用户手机号，精准匹配一个用户，返回其名下所有就诊人
- `idCard`（可选）：就诊人身份证号，精准匹配一个就诊人，返回其所属用户及名下所有就诊人
- `name`（可选）：就诊人姓名，模糊查询，按 `userId` 分组返回各用户的名下就诊人集合

至少提供一个参数，否则返回错误：`请至少提供一个查询条件: phone / idCard / name`。

## 响应数据结构
- 类型：数组，每个元素代表一个用户及其就诊人候选项
- 字段说明：
  - `userId`：用户唯一标识
  - `userDisplayName`：用户展示名（昵称或姓名）
  - `userMaskedPhone`：用户脱敏手机号
  - `visitOptions`：就诊人候选列表（数组）
    - `visitUserId`：就诊人唯一标识
    - `visitname`：就诊人姓名
    - `maskedIdCard`：脱敏身份证号
    - `maskedPhone`：脱敏手机号

### 示例响应
```json
[
  {
    "userId": 10086,
    "userDisplayName": "张三",
    "userMaskedPhone": "138****1234",
    "visitOptions": [
      {
        "visitUserId": 501,
        "visitname": "张三",
        "maskedIdCard": "1101**********0012",
        "maskedPhone": "138****1234"
      },
      {
        "visitUserId": 502,
        "visitname": "张小三",
        "maskedIdCard": "1101**********0034",
        "maskedPhone": "138****5678"
      }
    ]
  }
]
```

## 错误场景
- 未提供查询条件：返回错误提示
- 未找到匹配记录：返回错误 `未找到匹配的患者信息`

## curl 测试示例
> 将 `BASE_URL` 替换为实际服务地址，如 `http://localhost:8080`。
> 若服务启用了鉴权，请在命令中加入必要的 Header（有的环境使用 `Authorization: Bearer <token>`，有的环境使用自定义 `token` 头）。

- 按手机号查询：
```bash
curl -sS "${BASE_URL}/api/addSlotRequest/patientOptions?phone=13800001234"
```

- 按身份证号查询：
```bash
curl -sS "${BASE_URL}/api/addSlotRequest/patientOptions?idCard=110101199001010012"
```

- 按姓名模糊查询：
```bash
curl -sS "${BASE_URL}/api/addSlotRequest/patientOptions?name=张三"
```

- 携带鉴权头（Bearer Token 示例）：
```bash
curl -sS -H "Authorization: Bearer <token>" \
  "${BASE_URL}/api/addSlotRequest/patientOptions?phone=13800001234"
```

- 携带鉴权头（自定义 token 头示例）：
```bash
curl -sS -H "token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpdG1rIiwiZXhwIjoxNzYxNjQyNjQ1LCJ1c2VySWQiOiIzNyIsImlhdCI6MTc2MTQ2MjY0NSwidXNlcm5hbWUiOiIxMTExMTEifQ.jAYVipn67F7iVujjwB8clP_if-YK3BWN6VPB6iUB-bE" \
  "${BASE_URL}/api/addSlotRequest/patientOptions?phone=13800001234"
```

## 设计说明
- 输出为加号场景的精简、脱敏信息，便于医生快速选择患者与就诊人。
- 若需要扩展更多字段（如性别、生日、年龄等），建议在控制器中增加 `detail=true` 作为可选参数，并基于权限动态返回更多详情。