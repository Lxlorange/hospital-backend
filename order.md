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
  "makeId": 25
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

  

## 医生加号功能
当某位医生的排班号源已满时，医生可提交“加号申请”，由管理员审核。审核通过后系统为指定就诊人创建预约单（不占用原剩余号源）。

### 1) 提交加号申请（医生）
- URL: `/api/addSlotRequest/submit`
- Method: `POST`
- Body(JSON):
  - `scheduleId` 排班ID（必填）
  - `userId` 患者用户ID（必填）
  - `visitUserId` 就诊人ID（必填）
  - `address` 就诊地址（可选）
  - `reason` 加号原因（可选）
- 返回：
  - 成功：`{"code":200,"msg":"加号申请已提交，等待管理员审核"}`
  - 失败：`{"code":500,"msg":"提交失败或当前仍有号源无需加号"}`

说明：
- 系统会校验排班是否已满（`lastAmount <= 0`）。若仍有号源，将直接返回失败提示。
- 若同一医生、同一排班、同一就诊人已存在“待审核”申请，则更新原申请而不重复创建。

### 2) 加号申请列表（管理员）
- URL: `/api/addSlotRequest/list`
- Method: `GET`
- Query:
  - `currentPage` 当前页，默认 `1`
  - `pageSize` 每页大小，默认 `10`
  - `status` 状态筛选（可选：`0`待审核、`1`已通过、`2`已拒绝）
- 返回：分页列表数据

权限：仅管理员可访问。

### 3) 加号申请详情（管理员）
- URL: `/api/addSlotRequest/detail/{requestId}`
- Method: `GET`
- Path:
  - `requestId` 申请ID
- 返回：申请详情

权限：仅管理员可访问。

### 4) 审核加号申请（管理员）
- URL: `/api/addSlotRequest/review`
- Method: `POST`
- Query/Form:
  - `requestId` 申请ID（必填）
  - `status` 审核状态（`1`通过、`2`拒绝）（必填）
  - `reviewComment` 审核意见（可选）
- 返回：
  - 成功：`{"code":200,"msg":"审核完成"}`
  - 失败：`{"code":500,"msg":"审核失败"}`

审核通过行为：
- 系统根据申请信息创建一条预约订单 `make_order`：
  - `status` 设为 `1`（已预约）、`hasVisit` 为 `0`、`hasCall` 为 `0`
  - `price` 等关键字段以排班为准，防止前端修改
- 不执行 `subCount(scheduleId)`，即不消耗原剩余号源（用于加号场景）。

防重复：
- 若同一用户在同一排班已存在状态为 `1` 的订单，则不再重复创建订单，但审核状态仍会按操作更新。

### 5) 我的加号申请（医生）
- URL: `/api/addSlotRequest/my`
- Method: `GET`
- Query:
  - `currentPage` 当前页，默认 `1`
  - `pageSize` 每页大小，默认 `10`
  - `status` 状态筛选（可选）
- 返回：当前登录医生的加号申请分页列表

## 数据结构说明（简要）
- `doctor_add_slot_request`（新增表）
  - `requestId` 主键
  - `scheduleId` 排班ID
  - `doctorId` 医生ID（由当前登录医生自动填充）
  - `userId` 患者用户ID
  - `visitUserId` 就诊人ID
  - `times` 日期（由排班复制）
  - `timesArea` 上午/下午（由排班复制，`0`上午、`1`下午）
  - `price`、`week`、`address`
  - `reason` 申请原因
  - `status` 审核状态（`0`待审、`1`通过、`2`拒绝）
  - `reviewComment`、`reviewerId`、`reviewTime`
  - `createTime`、`updateTime`

- 审核通过后会创建 `make_order` 订单一条，用于完成加号预约。
