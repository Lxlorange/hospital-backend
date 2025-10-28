# 挂号预约系统接口文档 (前端测试版)

本文档提供创建预约、取消预约和查询预约列表三个核心接口的调用说明，方便前端进行功能测试。

**接口通用前缀**: `http://localhost:8089/wxapi/allApi`

---

## 1. 创建预约订单

### 接口描述
用户选择某个医生的排班后，调用此接口创建一个新的预约订单。

- **接口路径**: `http://localhost:8089/wxapi/allApi/makeOrderAdd`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

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

## 2. 取消预约订单

### 接口描述
对一个“已预约”状态的订单进行取消操作。

- **接口路径**: `http://localhost:8089/wxapi/allApi/cancelOrder`
- **请求方式**: `POST`
- **Content-Type**: `application/json`

#### Body
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `makeId` | Integer | 是 | **预约订单的唯一ID** |


**请求示例:**
```json
{
  "makeId": 25 //替换成可以用的
}
```
返回结果
-   **成功响应**:
    ```json
    {"code": 200, "msg": "取消成功", "data": null}
    ```
-   **时间限制**:
-   ```json
    {"code": 500, "msg": "已临近就诊时间（少于1天），无法取消预约!", "data": null}
    ```
-   **重复取消**:
-  ```json
    {"code": 500, "msg": "订单已经取消，请勿重复操作!", "data": null}
    ```
-   **订单不存在**:
-  ```json
    {"code": 500, "msg": "订单不存在!", "data": null}
    ```
---

## 3. 查询指定用户的预约列表

### 接口描述
分页查询指定用户的预约挂号列表，支持按订单状态筛选。

- **接口路径**: `http://localhost:8089/wxapi/allApi/getOrderList`
- **请求方式**: `GET`
- **Content-Type**: `application/json`

#### Body
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- |:-----| :--- |
| `userId` | Integer | 是    | **要查询的用户的ID** |
| `currentPage` | Integer | 是    | **当前页码, 从1开始** |
| `pageSize` | Integer | 是    | **每页记录数** |
| `status` | String | 否    | **订单状态。"1": 已预约, "2": 已取消。不传则查询所有。** |


**请求示例:**
查询 userId 为 2 的用户的“已取消”的第一页订单 (每页10条):
`http://localhost:8089/wxapi/allApi/getOrderList?userId=2&currentPage=1&pageSize=10&status=2`

返回结果
-   **成功响应**:
    ```json
    {
    "msg": "查询成功",
    "code": 200,
    "data": {
        "records": [
            {
                "makeId": 32,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 7,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "成",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 31,
                "scheduleId": 258,
                "userId": 2,
                "visitUserId": 7,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "成",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 30,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 8,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "王",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 29,
                "scheduleId": 258,
                "userId": 2,
                "visitUserId": 8,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "王",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 28,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 7,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "成",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 27,
                "scheduleId": 258,
                "userId": 2,
                "visitUserId": 7,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "成",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 26,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 8,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "王",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 25,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 8,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "王",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 24,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 8,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "王",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            },
            {
                "makeId": 23,
                "scheduleId": 227,
                "userId": 2,
                "visitUserId": 8,
                "doctorId": 24,
                "times": "2025-10-26",
                "timesArea": "0",
                "week": "星期日",
                "createTime": "2025-10-23",
                "price": 80.00,
                "address": "305",
                "status": "2",
                "hasCall": "0",
                "hasVisit": "0",
                "visitname": "王",
                "deptName": "儿科",
                "nickName": "刘波",
                "formattedAppointmentTime": "星期日 2025-10-26(上午)",
                "morningAppointment": true,
                "pendingVisit": false,
                "cancelled": true
            }
        ],
        "total": 17,
        "size": 10,
        "current": 1,
        "pages": 2
    }
    }
    ``` 
---