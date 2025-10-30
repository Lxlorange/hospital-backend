# 邮件相关接口

本文档提供邮件相关接口的调用说明，方便前端进行功能测试。

**接口通用前缀**: `http://localhost:8089/wxapi/allApi/add`

---

## 1. 新的用户注册接口

### 接口描述
提供用户名/密码注册，支持可选手机号和可选邮箱。后端会校验用户名唯一，若提供手机号或邮箱则校验唯一性；密码在保存前做 MD5 加密，邮箱格式做简单校验。

- **接口路径**: `http://localhost:8089/wxapi/allApi/add`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 请求参数

#### Body
- userName (string) — 必填，登录账号名，必须唯一。
- password (string) — 必填，明文密码，后台会 MD5 加密后保存。
- nickName (string) — 选填，昵称。
- phone (string) — 选填，若提供将校验唯一性（若已被注册则返回错误）。
- email (string) — 选填，若提供将做格式校验并校验唯一性（若已被注册则返回错误）。格式校验使用简单正则：^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$。
- name, image 等其它字段按需要传入。

**请求示例:**
```json
{
  "userName": "2330",
  "password": "666",
  "nickName": "张三",
  "phone": "13800138000",       // 可选
  "email": "example@163.com",   // 可选
  "name": "张三",
  "image": "http://.../avatar.jpg"
}
```
返回结果
-   **成功响应**:
    ```json
    {
    "code": 200,
    "msg": "成功!",
    "data": null
    }
    ```

---

## 2. 创建预约订单

### 接口描述
用户选择某个医生的排班后，调用此接口创建一个新的预约订单。创建成功后，系统会发邮件通知用户预约成功的详情。

- **接口路径**: `http://localhost:8089/wxapi/allApi/makeOrderAdd`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

### 邮件设置
- 将make_order表里的对应测试客户信息中的邮件改为测试者的邮箱。

### 请求参数

#### Headers
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | 是 | 用户登录凭证, `Bearer <token>` |

#### Body
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `userId` | Integer | 是 | **发起预约的用户ID** |
| `scheduleId` | Integer | 是 | 医生排班的唯一ID |
| `visitUserId` | Integer | 是 | 就诊人的唯一ID |
| `doctorId` | Integer | 是 | 医生ID |
| `times` | String | 是 | 预约日期, 格式: `YYYY-MM-DD` |
| `timesArea` | String | 是 | 预约时段, `"0"`: 上午, `"1"`: 下午 |
| `week` | String | 是 | 星期, 例如 "星期三" |
| `address` | String | 是 | 就诊地址 |

**请求示例:**
```json
{
  "scheduleId": 227,
  "userId": 2,
  "doctorId": 24,
  "times": "2025-10-26",
  "week": "星期日",
  "address": "305",
  "timesArea": "0",
  "visitUserId": 8
}
```
返回结果
-   **成功响应**:
    ```json
    {
      "code": 200,
      "msg": "保存成功",
      "data": null
    }
    ```
-   **号源不足**:
-   ```json
    {
      "code": 500,
      "msg": "今日号数已经被预约完，请选择其他排班!",
      "data": null
    }
    ```
-   **排班无效**:
-  ```json
    {
      "code": 500,
      "msg": "无效的排班信息!",
      "data": null
    }
    ```
-   **重复约号**:
-  ```json
    {
      "code": 500,
      "msg": "您已预约过该时段，请勿重复挂号!",
      "data": null
    }
    ```
-   **其他失败**:
- ```json
    {
      "code": 500,
      "msg": "预约失败，请稍后重试!",
      "data": null
    }
    ```

---