# 医院排班系统 API 文档 (模板化排班模块)

## 1. 概述

本模块提供了基于医生排班模板，自动化生成、查询和管理具体排班实例的核心功能。所有接口均以 `/api/schedule` 为根路径。

## 2. 认证

除特殊说明外，所有接口均需要通过 JWT 进行认证。请在请求头（Header）中携带认证令牌。

-   **Header Key**: `token`
-   **Header Value**: `登录后获取的Token字符串`

---

## 3. 接口详细说明

### 3.1 获取医生排班模板

获取指定医生的所有排班模板记录。模板定义了医生每周的固定出诊规律。

-   **功能**: 获取医生模板
-   **Method**: `GET`
-   **URL**: `/api/schedule/template/{doctorId}`
-   **URL 参数**:(注意要替换里面的{doctorId})
    -   `{doctorId}` (路径参数, `Long`, **必填**): 要查询的医生ID。
-   **请求体**: 无
-   **成功响应 (200 OK)**:
    ```json
    {
      "code": 200,
      "msg": "查询成功",
      "data": [
        {
          "templateId": 1,
          "doctorId": 19,
          "dayOfWeek": 1,
          "timeSlot": 1,
          "slots": [
            {
              "slotId": 1,
              "templateId": 1,
              "slotType": "普通号",
              "totalAmount": 10,
              "price": 50.00
            }
          ]
        },
        {
          "templateId": 6,
          "doctorId": 19,
          "dayOfWeek": 6,
          "timeSlot": 1,
          "slots": [
            {
              "slotId": 6,
              "templateId": 6,
              "slotType": "专家号",
              "totalAmount": 8,
              "price": 80.00
            }
          ]
        }
      ]
    }
    ```

### 3.2 保存医生排班模板

整体替换并保存一个医生的所有排班模板。后端逻辑为“先删后增”，确保数据完全更新。

-   **功能**: 保存医生模板
-   **Method**: `POST`
-   **URL**: `/api/schedule/template/{doctorId}`
-   **URL 参数**:
    -   `{doctorId}` (路径参数, `Long`, **必填**): 要保存模板的医生ID。
-   **请求体 (Request Body)**: `application/json`
    ```json
    [
      {
        "dayOfWeek": 1,
        "timeSlot": 1,
        "slots": [
          {
            "slotType": "普通号",
            "totalAmount": 20,
            "price": 50.00
          }
        ]
      },
      {
        "dayOfWeek": 5,
        "timeSlot": 1,
        "slots": [
          {
            "slotType": "专家号",
            "totalAmount": 8,
            "price": 80.00
          },
          {
            "slotType": "特需专家号",
            "totalAmount": 2,
            "price": 300.00
          }
        ]
      }
    ]
    ```
-   **成功响应 (200 OK)**:
    ```json
    {
      "code": 200,
      "msg": "保存成功",
      "data": null
    }
    ```

### 3.3 根据模板生成排班实例

遍历系统中所有医生的排班模板，在指定的时间范围内，自动生成每一天的具体排班实例。此接口为后台批量操作，应由管理员调用。

-   **注意**: 如果某天已存在排班实例，系统将自动跳过，不会覆盖。
-   **功能**: 批量生成排班
-   **Method**: `POST`
-   **URL**: `/api/schedule/instance/generate`
-   **请求体 (Request Body)**: `application/json`
    ```json
    {
      "startDate": "2025-11-01",
      "endDate": "2025-11-30"
    }
    ```
-   **成功响应 (200 OK)**:
    ```json
    {
      "code": 200,
      "msg": "排班生成成功",
      "data": null
    }
    ```

### 3.4 查询排班实例列表

根据时间范围、科室、医生等条件，分页查询已生成的具体排班实例。

-   **功能**: 查询排班实例
-   **Method**: `GET`
-   **URL**: `/api/schedule/instance`
-   **查询参数 (Query Parameters)**:
    -   `startDate` (`String`, **必填**): 查询开始日期，格式 "YYYY-MM-DD"。
    -   `endDate` (`String`, **必填**): 查询结束日期，格式 "YYYY-MM-DD"。
    -   `deptId` (`Long`, 可选): 按科室ID筛选。
    -   `doctorId` (`Long`, 可选): 按医生ID筛选。
-   **请求体**: 无
-   **例**:http://localhost:8089/api/schedule/instance?startDate=2025-09-14&endDate=2025-10-01&doctorId=25
-   **成功响应 (200 OK)**:
    ```json
    {
      "code": 200,
      "msg": "查询成功",
      "data": [
        {
          "instanceId": 1,
          "doctorId": 25,
          "scheduleDate": "2025-09-14",
          "timeSlot": 1,
          "status": 1,
          "doctorName": "李开",
          "departmentName": "心血管内科",
          "slots": [
            {
              "instanceSlotId": 1,
              "instanceId": 1,
              "slotType": "特需专家号",
              "totalAmount": 100,
              "availableAmount": 100,
              "price": 300.00
            }
          ]
        }
      ]
    }
    ```

### 3.5 更新排班实例状态

用于对某一个具体的排班实例进行停诊或恢复出诊的操作。

-   **功能**: 停诊/复诊
-   **Method**: `PUT`
-   **URL**: `/api/schedule/instance/{instanceId}/status`
-   **URL 参数**:
    -   `{instanceId}` (路径参数, `Long`, **必填**): 要更新状态的排班实例ID。
-   **请求体 (Request Body)**: `application/json`
    ```json
    {
      "status": 2
    }
    ```
    -   `status` 值说明:
        -   `1`: 正常
        -   `2`: 停诊
-   **成功响应 (200 OK)**:
    ```json
    {
      "code": 200,
      "msg": "状态更新成功",
      "data": null
    }
    ```