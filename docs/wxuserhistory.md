## 1. 查询患者全院历史就诊记录

**接口描述**：获取指定患者在医院所有的历史就诊记录，不限科室、不限医生。该接口主要用于医生工作台查看患者的既往全院病史。

- **测试URL**: `http://localhost:8089/api/makeOrder/getAllHistory?currentPage=1&pageSize=10&visitUserId=7`
- **Method**: `GET`
- **权限要求**：需要token

### 请求参数 (Query Params)

| 参数名         | 类型 | 必填 | 说明 |
|:------------| :--- | :--- | :--- |
| visitUserId | Integer | **是** | 就诊人ID (对应 `visit_user` 表主键) |
| currentPage | Long | 否 | 当前页码，不传默认为1 |
| pageSize    | Long | 否 | 每页条数，不传默认为10 |
| status(可选)  | String | 否 | 就诊状态 (1:已就诊)，不传默认查询已就诊 |

### 响应示例 (Success)

```json
{
  "msg": "查询全院记录成功",
  "code": 200,
  "data": {
    "records": [
      {
        "makeId": 15880,
        "scheduleId": 355,
        "userId": 2,
        "visitUserId": 7,
        "doctorId": 25,
        "times": "2025-11-25",
        "timesArea": "0",
        "week": "星期二",
        "createTime": "2025-11-22",
        "price": 50.00,
        "address": "门诊楼2层205室",
        "status": "1",
        "hasCall": "0",
        "hasVisit": "1",
        "visitname": "成",
        "deptName": "儿科",
        "nickName": "李开",
        "geetest": null,
        "cancelled": false,
        "pendingVisit": false,
        "morningAppointment": true,
        "formattedAppointmentTime": "星期二 2025-11-25(上午)"
      }...
    ]
  }
}
```
### 响应参数说明 (Response Parameters)

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| code | Integer | 响应状态码 (200表示成功) |
| msg | String | 响应提示信息 |
| data | Object | 数据主体 |
| **data.records** | **Array** | **就诊/预约记录列表** |
| ├─ makeId | Integer | 预约记录ID (主键) |
| ├─ visitname | String | **就诊人姓名** (患者) |
| ├─ nickName | String | **接诊医生姓名** |
| ├─ deptName | String | **所属科室名称** |
| ├─ formattedAppointmentTime | String | 格式化后的就诊时间展示 |
| ├─ address | String | 就诊地址/诊室位置 |
| ├─ price | Decimal | 挂号/诊疗费用 |
| ├─ times | String | 预约日期 (yyyy-MM-dd) |
| ├─ week | String | 星期 |
| ├─ timesArea | String | 时段标识 (0:上午 1:下午) |
| ├─ morningAppointment | Boolean | 是否为上午时段 (前端辅助字段) |
| ├─ hasVisit | String | **是否已就诊** (0:未就诊 1:已就诊) |
| ├─ status | String | 订单状态 (1:正常/已支付 2:取消) |
| ├─ hasCall | String | 是否已叫号 (0:未叫号 1:已叫号) |
| ├─ cancelled | Boolean | 是否已取消 |
| ├─ pendingVisit | Boolean | 是否待就诊 |
| ├─ createTime | String | 订单创建时间 |
| ├─ visitUserId | Integer | 就诊人ID |
| ├─ userId | Integer | 预约操作人ID (账号ID) |
| ├─ doctorId | Integer | 医生ID |
| ├─ scheduleId | Integer | 排班ID |
| └─ geetest | Object | 验证码相关对象 (通常为null) |
| **data.total** | Integer | 总记录数 |
| **data.size** | Integer | 每页显示条数 |
| **data.current** | Integer | 当前页码 |
| **data.pages** | Integer | 总页数 |
---
## 2. 查询患者在当前科室的历史记录

**接口描述**：获取指定患者在**当前操作医生所属科室**的所有就诊记录。该接口会自动根据传入的医生ID查询其所在科室，并筛选出该患者在本科室（包含本科室其他医生接诊）的历史记录。

- **测试URL**: `http://localhost:8089/api/makeOrder/getDeptHistory?currentPage=1&pageSize=10&visitUserId=7&doctorId=25`
- **Method**: `GET`
- **权限要求**：需要token

### 请求参数 (Query Params)

| 参数名         | 类型 | 必填 | 说明 |
|:------------| :--- | :--- | :--- |
| visitUserId | Integer | **是** | 就诊人ID (患者ID，对应 `visit_user` 表主键) |
| doctorId    | Long    | **是** | **当前操作医生的ID** (用于获取当前科室信息) |
| currentPage | Long    | 否 | 当前页码，不传默认为1 |
| pageSize    | Long    | 否 | 每页条数，不传默认为10 |
| status(可选) | String  | 否 | 就诊状态 (1:已就诊)，不传默认查询已就诊 |

### 响应示例 (Success)

```json
{
  "msg": "查询本科室记录成功",
  "code": 200,
  "data": {
    "records": [
      {
        "makeId": 15880,
        "scheduleId": 355,
        "userId": 2,
        "visitUserId": 7,
        "doctorId": 25,
        "times": "2025-11-25",
        "timesArea": "0",
        "week": "星期二",
        "createTime": "2025-11-22",
        "price": 50.00,
        "address": "门诊楼2层205室",
        "status": "1",
        "hasCall": "0",
        "hasVisit": "1",
        "visitname": "成",
        "deptName": "消化内科",
        "nickName": "李开",
        "geetest": null,
        "cancelled": false,
        "pendingVisit": false,
        "morningAppointment": true,
        "formattedAppointmentTime": "星期二 2025-11-25(上午)"
      }...
```
### 响应参数说明 (Response Parameters)

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| code | Integer | 响应状态码 (200表示成功) |
| msg | String | 提示信息 |
| data | Object | 返回的数据主体 |
| data.total | Integer | 总记录数 |
| data.size | Integer | 每页显示条数 |
| data.current | Integer | 当前页码 |
| data.pages | Integer | 总页数 |
| **data.records** | **Array** | **就诊记录列表** |
| records.makeId | Integer | 预约/就诊记录ID (主键) |
| records.visitUserId | Integer | 就诊人ID |
| records.visitname | String | **就诊人姓名** |
| records.doctorId | Integer | 医生ID |
| records.nickName | String | **接诊医生姓名** |
| records.deptName | String | **所属科室名称** |
| records.advice | String | **医嘱/诊断结果** (如果有) |
| records.visitTime | String | 实际就诊时间 |
| records.formattedAppointmentTime | String | 格式化的预约时间展示 |
| records.address | String | 就诊地址 |
| records.price | Decimal | 挂号费/诊查费 |
| records.times | String | 预约日期 (yyyy-MM-dd) |
| records.week | String | 星期 |
| records.timesArea | String | 时段 (0:上午 1:下午) |
| records.status | String | 订单状态 (1:已预约/正常 2:取消) |
| records.hasVisit | String | **就诊状态** (0:未就诊 1:已就诊) |
| records.hasCall | String | 叫号状态 (0:未叫号 1:已叫号) |
| records.createTime | String | 订单创建时间 |
---
## 3. 查询患者在当前医生处的历史记录

**接口描述**：获取指定患者在**当前操作医生本人**处的所有就诊记录。该接口用于医生复诊场景，快速回顾自己之前给该患者开具的医嘱、诊断及用药情况。

- **测试URL**: `http://localhost:8089/api/makeOrder/getMyHistory?currentPage=1&pageSize=10&visitUserId=7&doctorId=25`
- **Method**: `GET`
- **权限要求**：需要token

### 请求参数 (Query Params)

| 参数名         | 类型 | 必填 | 说明 |
|:------------| :--- | :--- | :--- |
| visitUserId | Integer | **是** | 就诊人ID (患者ID，对应 `visit_user` 表主键) |
| doctorId    | Long    | **是** | **当前操作医生的ID** (用于筛选该医生本人的接诊记录) |
| currentPage | Long    | 否 | 当前页码，不传默认为1 |
| pageSize    | Long    | 否 | 每页条数，不传默认为10 |
| status(可选) | String  | 否 | 就诊状态 (1:已就诊)，不传默认查询已就诊 |

### 响应示例 (Success)

```json
{
  "msg": "查询本人接诊记录成功",
  "code": 200,
  "data": {
    "records": [
      {
        "makeId": 15880,
        "scheduleId": 355,
        "userId": 2,
        "visitUserId": 7,
        "doctorId": 25,
        "times": "2025-11-25",
        "timesArea": "0",
        "week": "星期二",
        "createTime": "2025-11-22",
        "price": 50.00,
        "address": "门诊楼2层205室",
        "status": "1",
        "hasCall": "0",
        "hasVisit": "1",
        "visitname": "成",
        "deptName": "消化内科",
        "nickName": "李开",
        "advice": "复诊检查情况良好，继续保持饮食控制...",
        "visitTime": "2025-11-25 10:30:00",
        "geetest": null,
        "cancelled": false,
        "pendingVisit": false,
        "morningAppointment": true,
        "formattedAppointmentTime": "星期二 2025-11-25(上午)"
      },
      {
        "makeId": 923,
        "scheduleId": 334,
        "userId": 2,
        "visitUserId": 7,
        "doctorId": 25,
        "times": "2025-11-18",
        "timesArea": "0",
        "week": "星期二",
        "createTime": "2025-11-14",
        "price": 60.00,
        "address": "门诊楼2层205室",
        "status": "1",
        "hasCall": "0",
        "hasVisit": "1",
        "visitname": "成",
        "deptName": "消化内科",
        "nickName": "李开",
        "advice": "初诊：腹痛待查，建议做胃镜",
        "visitTime": "2025-11-18 09:15:00",
        "geetest": null,
        "cancelled": false,
        "pendingVisit": false,
        "morningAppointment": true,
        "formattedAppointmentTime": "星期二 2025-11-18(上午)"
      }
    ],
    "total": 2,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```
### 响应参数说明 (Response Parameters)

| 参数名 | 类型 | 说明 |
| :--- | :--- | :--- |
| code | Integer | 响应状态码 (200表示成功) |
| msg | String | 提示信息 |
| data | Object | 返回的数据主体 |
| data.total | Integer | 总记录数 |
| data.size | Integer | 每页显示条数 |
| data.current | Integer | 当前页码 |
| data.pages | Integer | 总页数 |
| **data.records** | **Array** | **就诊记录列表** |
| records.makeId | Integer | 预约/就诊记录ID (主键) |
| records.visitUserId | Integer | 就诊人ID |
| records.visitname | String | **就诊人姓名** |
| records.doctorId | Integer | 医生ID |
| records.nickName | String | **接诊医生姓名** |
| records.deptName | String | **所属科室名称** |
| records.advice | String | **医嘱/诊断结果** (如果有) |
| records.visitTime | String | 实际就诊时间 |
| records.formattedAppointmentTime | String | 格式化的预约时间展示 |
| records.address | String | 就诊地址 |
| records.price | Decimal | 挂号费/诊查费 |
| records.times | String | 预约日期 (yyyy-MM-dd) |
| records.week | String | 星期 |
| records.timesArea | String | 时段 (0:上午 1:下午) |
| records.status | String | 订单状态 (1:已预约/正常 2:取消) |
| records.hasVisit | String | **就诊状态** (0:未就诊 1:已就诊) |
| records.hasCall | String | 叫号状态 (0:未叫号 1:已叫号) |
| records.createTime | String | 订单创建时间 |
