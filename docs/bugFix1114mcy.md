# 接口文档

**接口通用前缀**: `http://localhost:8089/wxapi/allApi`

---

## 查询医生排班详情

### 接口描述
查询指定医生从当天起（包含当天）的所有有效排班信息。

- **接口路径**: `/getDoctor`
- **请求方式**: `GET`

### 请求参数
#### Query Parameters
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `userId` | String | 是 | 当前小程序用户的ID (后端暂未使用，但建议传递) |
| `doctorId` | String | 是 | **需要查询的医生的ID** |

### 请求示例

假设今天要查询 `doctorId` 为 `24` 的医生的排班信息：

`http://localhost:8089/wxapi/allApi/getDoctor?userId=2&doctorId=24`

### 返回结果

#### 成功响应

返回一个JSON数组，数组中的每个对象代表一个具体的排班时段。

```json
{
    "code": 200,
    "msg": "成功",
    "data": [
        {
            "schedule_id": 227,
            "doctor_id": 24,
            "doctor_name": "刘涛",
            "times": "2025-11-02",
            "week": "星期日",
            "witch_week": 44,
            "level_name": "专家号",
            "time_slot": 0,
            "price": 80,
            "amount": 20,
            "type": "1",
            "last_amount": 15
        },
        {
            "schedule_id": 228,
            "doctor_id": 24,
            "doctor_name": "刘涛",
            "times": "2025-11-03",
            "week": "星期一",
            "witch_week": 45,
            "level_name": "普通号",
            "time_slot": 1,
            "price": 50,
            "amount": 15,
            "type": "1",
            "last_amount": 10
        }
    ]
}
```
字段说明:
- schedule_id: 排班的唯一ID (主键)
- doctor_id:   医生ID
- doctor_name: 医生姓名
- times:       排班日期 (格式: YYYY-MM-DD)
- week:        星期几
- witch_week:  当年的第几周
- level_name:  号别名称 (如: 普通号, 专家号等)
- time_slot:   时间段 (0: 上午, 1: 下午)
- price:       挂号费 (单位: 元)
- amount:      该时段总号源数量
- type:        排班类型 (1: 上班, 0: 休息)
- last_amount: 剩余号源数量


---

## 查询用户历史就诊记录

### 接口描述
分页查询指定用户的**历史就诊记录**列表，返回结果包含了详细的就诊信息、医生、科室以及原始的挂号费用和地址。

- **接口路径**: `/getVisitOrderList`
- **请求方式**: `GET`

### 请求参数

#### Query Parameters
| 参数名 | 类型 | 是否必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `userId` | Integer | 是 | **要查询的用户的ID** |
| `currentPage` | Integer | 是 | 当前页码, 从1开始 |
| `pageSize` | Integer | 是 | 每页记录数 |

### 请求示例
查询 `userId` 为 `2` 的用户的第一页历史就诊记录 (每页6条):

`http://localhost:8089/wxapi/allapi/getVisitOrderList?userId=2&currentPage=1&pageSize=6`

### 返回结果

#### 成功响应
返回一个分页对象，其中 `records` 数组包含了详细的就诊记录列表。

```json
{
    "msg": "成功",
    "code": 200,
    "data": {
        "records": [
            {
                "visitId": 10,
                "makeId": 56,
                "userId": 2,
                "visitUserId": 7,
                "doctorId": 25,
                "times": "2025-11-04",
                "timesArea": "0",
                "week": "星期二",
                "hasVisit": "1",
                "hasLive": "1",
                "advice": "检查结果显示需要住院进一步观察和治疗，请立即办理住院手续。",
                "visitTime": "2025-11-04",
                "createTime": "2025-11-04",
                "visitname": "成",
                "deptName": "儿科",
                "nickName": "李开",
                "collapsed": true,
                "address": "门诊楼2层205室",//地址
                "price": 50.00,//价格
                "appointmentSchedule": "2025-11-04 (星期二) - 上午",
                "visitRecordComplete": true
            },
            {
                "visitId": 11,
                "makeId": 58,
                "userId": 2,
                "visitUserId": 7,
                "doctorId": 24,
                "times": "2025-11-03",
                "timesArea": "0",
                "week": "星期一",
                "hasVisit": "1",
                "hasLive": "0",
                "advice": "轻微感冒症状，建议服用感冒灵颗粒，多通风，避免去人多场所。",
                "visitTime": "2025-11-03",
                "createTime": "2025-11-03",
                "visitname": "成",
                "deptName": "儿科",
                "nickName": "刘波",
                "collapsed": true,
                "address": "305",
                "price": 50.00,
                "appointmentSchedule": "2025-11-03 (星期一) - 上午",
                "visitRecordComplete": true
            }
        ],
        "total": 4,
        "size": 6,
        "current": 1,
        "pages": 1
    }
}
```
字段说明 (records数组中的对象):
- visitId:             就诊记录的唯一ID
- makeId:              对应的原始预约订单ID
- userId:              预约人（小程序用户）的ID
- visitUserId:         就诊人的ID
- doctorId:            就诊医生的ID
- times:               预约日期
- timesArea:           预约时段 (0: 上午, 1: 下午)
- week:                星期几
- hasVisit:            就诊状态 (1: 已就诊)
- hasLive:             是否需要住院 (1: 需要, 0: 不需要)
- advice:              医生的医嘱内容
- visitTime:           实际就诊日期
- createTime:          该条就诊记录的创建日期
- visitname:           就诊人的姓名
- deptName:            就诊科室的名称
- nickName:            就诊医生的姓名
- collapsed:           (UI辅助字段) 用于前端控制列表项是否折叠，默认为true
- address:             就诊地址
- price:               挂号费用
- appointmentSchedule: (UI辅助字段) 格式化后的、对用户友好的预约时间字符串
- visitRecordComplete: (UI辅助字段) 标记就诊记录是否完整（例如是否有医嘱），布尔值

分页信息说明 (data对象):
- total:   总记录数
- size:    每页显示的条数
- current: 当前页码
- pages:   总页数