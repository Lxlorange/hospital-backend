# 医生个人主页 API 使用示例

## 1. 获取医生个人主页信息

### 请求
```http
GET /api/doctorProfile/getDoctorProfile?doctorId=1
```

### 响应
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "userId": 1,
    "username": "doctor_zhang",
    "phone": "13800138000",
    "email": "doctor_zhang@hospital.com",
    "sex": "男",
    "nickName": "张医生",
    "deptId": 101,
    "deptName": "内科",
    "education": "医学博士",
    "jobTitle": "主任医师",
    "image": "/images/doctors/zhang.jpg",
    "introduction": "擅长心血管疾病的诊断和治疗，有20年临床经验。",
    "visitAddress": "门诊大楼3楼内科诊室",
    "goodAt": "高血压、冠心病、心力衰竭",
    "price": 50.00,
    "toHome": "1",
    "createTime": "2023-01-15T08:00:00",
    "updateTime": "2024-01-15T10:30:00",
    "enabled": true
  }
}
```

## 2. 更新医生个人主页信息

### 请求
```http
POST /api/doctorProfile/updateDoctorProfile
Content-Type: application/json

{
  "userId": 1,
  "introduction": "擅长心血管疾病的诊断和治疗，有20年临床经验。曾在北京协和医院进修。",
  "goodAt": "高血压、冠心病、心力衰竭、心律失常",
  "visitAddress": "门诊大楼3楼内科专家诊室",
  "price": 60.00
}
```

### 响应
```json
{
  "code": 200,
  "msg": "更新成功!",
  "data": null
}
```

## 3. 获取当前登录医生的个人主页信息

### 请求
```http
GET /api/doctorProfile/getMyProfile
Authorization: Bearer {token}
```

### 响应
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "userId": 1,
    "username": "doctor_zhang",
    "nickName": "张医生",
    "deptName": "内科",
    "jobTitle": "主任医师",
    "image": "/images/doctors/zhang.jpg",
    "introduction": "擅长心血管疾病的诊断和治疗，有20年临床经验。",
    "visitAddress": "门诊大楼3楼内科诊室",
    "goodAt": "高血压、冠心病、心力衰竭",
    "price": 50.00
  }
}
```

## 4. 更新当前登录医生的个人主页信息

### 请求
```http
POST /api/doctorProfile/updateMyProfile
Content-Type: application/json
Authorization: Bearer {token}

{
  "userId":25,"username":"1","phone":"18612351661","email":"","sex":"1","nickName":"李开","deptId":3,"deptName":"儿科","education":"本科","jobTitle":"主任医师 ","image":"/images/A.jpg","introduction":"具有扎实的基本功及良好的服务态度。","visitAddress":"门诊楼1楼401室","goodAt":"具有扎实的基本功及良好的服务态度。","price":10.00,"toHome":"1","createTime":"2025-09-14T15:24:48.000+00:00","updateTime":"2025-09-14T08:55:16.000+00:00","enabled":true,"scheduleInfo":null,"appointmentStats":null
}
```

### 响应
```json
{
  "code": 200,
  "msg": "更新成功!",
  "data": null
}
```



## 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| userId | Long | 医生ID |
| username | String | 用户名 |
| nickName | String | 医生姓名 |
| deptId | Integer | 科室ID |
| deptName | String | 科室名称 |
| education | String | 学历 |
| jobTitle | String | 职称 |
| image | String | 头像URL |
| introduction | String | 医生简介 |
| visitAddress | String | 出诊地址 |
| goodAt | String | 擅长治疗的病症 |
| price | BigDecimal | 挂号费 |
| toHome | String | 是否推荐到首页 |
| createTime | Date | 创建时间 |
| updateTime | Date | 更新时间 |
| enabled | Boolean | 是否启用 |