# 接口文档

**接口通用前缀**: `http://localhost:8089/wxapi/allapi`

---

## 查询医生排班详情

### 接口描述
查询指定医生从当天起（包含当天）的所有有效排班信息。

- **接口路径**: `/getDoctor`
- **请求方式**: `GET`

### 请求参数

#### Headers
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | 是 | 用户登录凭证, `Bearer <token>` |

#### Query Parameters
| 参数名 | 类型 | 是否必galax | 描述 |
| :--- | :--- | :--- | :--- |
| `userId` | String | 是 | 当前小程序用户的ID (后端暂未使用，但建议传递) |
| `doctorId` | String | 是 | **需要查询的医生的ID** |

### 请求示例

假设今天要查询 `doctorId` 为 `24` 的医生的排班信息：

`http://localhost:8089/wxapi/allapi/getDoctor?userId=2&doctorId=24`

### 返回结果

#### 成功响应

```json
{
  "msg": "成功",
  "code": 200,
  "data": [
    {
      "scheduleId": 293,
      "doctorId": 20,
      "doctorName": "常弘",
      "times": "2025-11-07",
      "week": "星期五",
      "witchWeek": null,
      "timeSlot": 1,
      "levelName": "专家号",
      "price": 80,
      "amount": 8,
      "lastAmount": 8,
      "type": "1",
      "deptId": null,
      "deptName": null,
      "fullyBooked": false
    },
    {
      "scheduleId": 294,
      "doctorId": 20,
      "doctorName": "常弘",
      "times": "2025-11-07",
      "week": "星期五",
      "witchWeek": null,
      "timeSlot": 1,
      "levelName": "特需号",
      "price": 500,
      "amount": 2,
      "lastAmount": 2,
      "type": "1",
      "deptId": null,
      "deptName": null,
      "fullyBooked": false
    }//...

    ]
  }
```