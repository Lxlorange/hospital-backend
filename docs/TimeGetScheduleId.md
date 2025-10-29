### 接口详细说明

#### **接口地址**
`/api/home/getScheduleId`

#### **请求方式**
`GET`

#### **接口描述**
根据指定的医生ID、日期和时间段（上午/下午），查询并返回对应的唯一排班ID (`scheduleId`)。该接口主要用于在用户选择了具体的医生和预约时间后，获取进行下一步操作（如创建预约订单）所需的排班标识。

#### **请求参数 (Query Parameters)**

| 参数名    | 类型    | 是否必填 | 描述                                       | 示例         |
| :-------- | :------ | :------- | :----------------------------------------- | :----------- |
| `date`    | String  | 是       | 查询的日期，格式必须为 `yyyy-MM-dd`。      | `2025-11-05` |
| `timeSlot`| Integer | 是       | 时间段。`0` 代表上午，`1` 代表下午。      | `0`          |
| `doctorId`| Integer | 是       | 医生的唯一ID。                             | `101`        |

#### **响应数据**

**成功响应 - 找到排班**
* **HTTP 状态码**: `200 OK`
* **Body (JSON)**:
    ```json
    {
        "code": 200,
        "msg": "排班ID获取成功",
        "data": 12345 
    }
    ```
    * `data` 字段为整型的 `scheduleId`。

**成功响应 - 未找到排班**
* **HTTP 状态码**: `200 OK`
* **Body (JSON)**:
    ```json
    {
        "code": 200,
        "msg": "未找到指定的排班信息",
        "data": null
    }
    ```
    * 当没有与查询条件匹配的排班时，`data` 字段为 `null`。

**失败响应 - 参数错误**
* **HTTP 状态码**: `200 OK` (根据您项目 `ResultUtils` 的封装逻辑，业务错误也可能返回200)
* **Body (JSON)**:
    ```json
    {
        "code": 500,
        "msg": "日期格式不正确，请使用 yyyy-MM-dd 格式",
        "data": null
    }
    ```

    ### 接口测试用例

| 用例ID | 测试目的 | 请求URL及参数 | 前置条件 | 预期HTTP状态码 | 预期的响应Body (JSON) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TC-001 | **成功获取** - 查询存在的上午排班 | `/api/home/getScheduleId?date=2025-11-05&timeSlot=1&doctorId=37` | 数据库中存在 doctorId=37 在 2025-11-05 下午的排班记录，其 scheduleId 为 300。 | 200 | `{"code": 200, "msg": "排班ID获取成功", "data": 300 |
| TC-002 | **成功获取** - 查询存在的下午排班 | `/api/home/getScheduleId?date=2025-11-06&timeSlot=1&doctorId=102` | 数据库中存在 doctorId=102 在 2025-11-06 下午的排班记录，其 scheduleId 为 12388。 | 200 | `{"code": 200, "msg": "排班ID获取成功", "data": 12388}` |
| TC-003 | **逻辑为空** - 查询当天不存在的排班 | `/api/home/getScheduleId?date=2025-11-05&timeSlot=1&doctorId=101` | 数据库中 doctorId=101 在 2025-11-05 只有上午排班，没有下午排班。 | 200 | `{"code": 200, "msg": "未找到指定的排班信息", "data": null}` |
| TC-004 | **逻辑为空** - 查询不存在的医生排班 | `/api/home/getScheduleId?date=2025-11-05&timeSlot=0&doctorId=999` | 数据库中不存在 doctorId 为 999 的医生。 | 200 | `{"code": 200, "msg": "未找到指定的排班信息", "data": null}` |
| TC-005 | **参数错误** - 日期格式不正确 | `/api/home/getScheduleId?date=05-11-2025&timeSlot=0&doctorId=101` | 无 | 200 | `{"code": 500, "msg": "日期格式不正确，请使用 yyyy-MM-dd 格式", "data": null}` |
| TC-006 | **参数错误** - 缺少 `doctorId` 参数 | `/api/home/getScheduleId?date=2025-11-05&timeSlot=0` | 无 | 400 或 200 | `{"code": 500, "msg": "缺少必要的参数：date, timeSlot, doctorId", "data": null}` |
| TC-007 | **参数错误** - 缺少 `date` 参数 | `/api/home/getScheduleId?timeSlot=0&doctorId=101` | 无 | 400 或 200 | `{"code": 500, "msg": "缺少必要的参数：date, timeSlot, doctorId", "data": null}` |
| TC-008 | **参数错误** - `timeSlot` 值非法 | `/api/home/getScheduleId?date=2025-11-05&timeSlot=2&doctorId=101` | 数据库中 `timeSlot` 只有 0 和 1 两种值。 | 200 | `{"code": 200, "msg": "未找到指定的排班信息", "data": null}` |