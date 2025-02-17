# 校口常开——高校门禁预约系统

## 项目简介
本项目是一个基于SpringBoot开发的高校门禁预约管理系统，集成了用户预约、RFID门禁控制、管理员后台等功能模块。系统采用前后端分离架构,通过MQTT协议实现硬件设备与服务端的实时通信。

## 系统架构
### 硬件端
- 主控：STM32
- WiFi模块：ESP8266 
- 外设：RFID读卡器、舵机、OLED显示屏
- 通信：MQTT协议

### 服务端 
- 框架：SpringBoot、MyBatis-Plus、OpenAPI
- 数据库：MySQL
- 消息中间件：MQTT Broker
- 机器学习模块：用于人流量预测

### 客户端
- 微信小程序
- Web端(SpringBoot + Vue)
- 管理员后台(SpringBoot + Vue)

## 核心功能
### 用户端
1. 用户注册与登录
2. 在线预约申请
   - 选择参观时间
   - 填写个人信息
   - 查看预约状态
3. 实时查看预约进度
4. 查看历史预约记录

### 管理员端
1. 用户管理
   - 用户信息审核
   - RFID卡片管理与分配
2. 预约管理
   - 预约申请审核
   - 每日预约数量设置
   - 预约统计查看
3. 门禁控制
   - 紧急开关门控制
   - 门禁状态监控
4. 数据分析
   - 人流量统计
   - 机器学习预测分析
   - 数据可视化展示

### 硬件端
1. RFID卡片识别
2. 门禁控制
3. 状态显示
4. 实时通信

## 业务流程
1. 用户通过微信小程序/Web端注册并提交预约申请
2. 管理员审核预约申请
3. 预约通过后，用户到校门管理处领取RFID卡片
4. 用户在预约时间内使用RFID卡片进入各门禁
5. 参观结束后，用户归还RFID卡片
6. 系统记录全过程数据用于分析

## 技术特点
1. 实时通信：采用MQTT协议实现硬件设备与服务端的实时数据交互
2. 可扩展性：模块化设计，支持快速扩展新功能
3. 智能预测：集成机器学习算法，实现人流量智能预测
4. 高可用性：采用分布式架构，确保系统稳定运行

## 开发环境
- JDK: 17
- Maven: 3.6+
- MySQL: 8
- SpringBoot: 3.4.2
- MyBatis-Plus: 3.5.10
- MQTT Broker: EMQX
- IDE: IntelliJ IDEA、VS Code、Keil

## 端口号
1. MySQL
   - 50001:3306
   - 50002:33060
2. Springboot
   - 
3. Nginx
   - 
4. MQTT
   - 50005:1883 
   - 50006:18083 

## 部署说明
1. 克隆项目到本地
2. 配置application.properties
3. 初始化数据库
4. 启动MQTT Broker
5. 运行SpringBoot应用

## API文档
详细API文档请参考 [OpenAPI](http://localhost:8123/swagger-ui/index.html#/)

## MQTT通信协议规范

### 主题(Topic)设计
```
xkck/device/{device_id}/status    # 设备状态上报
xkck/device/{device_id}/command   # 服务器命令下发
xkck/device/{device_id}/response  # 设备响应服务器命令
xkck/device/{device_id}/heartbeat # 设备心跳包
xkck/admin/{device_id}/*          # 管理设备专用主题
```

### 消息格式

#### 1. 设备连接确认
- 设备请求 (设备 -> 服务器)
```json
{
    "type": "connect",
    "device_id": "1",
    "data": {
        "firmware_version": "1.0.0",
        "ip": "192.168.1.101"
    }
}
```
- 服务器响应 (服务器 -> 设备)
```json
{
    "type": "connect_reply",
    "timestamp": 1645678902,
    "status": "success",
    "data": {
        "device_id": "1",
        "location": "校园正门",
        "device_type": "campus_gate",
        "description": "校园主入口门禁"
    }
}
```

#### 2. RFID卡片验证
- 设备请求 (设备 -> 服务器)
```json
{
    "type": "verify_card",
    "device_id": "1",
    "timestamp": 1645678905,
    "data": {
        "uid": "RFID_001",
        "action": "entry"  // entry或exit
    }
}
```
- 服务器响应 (服务器 -> 设备)
```json
{
    "type": "verify_card_reply",
    "timestamp": 1645678906,
    "status": "success",
    "data": {
        "allow": true,
        "visitor_name": "张三",
        "message": "预约内进入",
        "action": "open_door", // open_door或deny_access
        "expire_time": 1645692000
    }
}
```

#### 3. 管理设备操作
- 卡片信息查询 (管理设备 -> 服务器)
```json
{
    "type": "query_card",
    "device_id": "5",
    "timestamp": 1645678910,
    "data": {
        "uid": "RFID_001"
    }
}
```
- 服务器响应 (服务器 -> 管理设备)
```json
{
    "type": "query_card_reply",
    "timestamp": 1645678911,
    "status": "success",
    "data": {
        "uid": "RFID_001",
        "status": "issued",
        "visitor_name": "张三",
        "visitor_id": 1,
        "reservation_id": 1,
        "issue_time": "2024-12-20 13:00:00",
        "expiration_time": "2024-12-20 16:00:00",
        "remarks": "发放给张三，用于业务洽谈访问"
    }
}
```

#### 4. 设备心跳包
- 设备发送 (设备 -> 服务器)
```json
{
    "type": "heartbeat",
    "device_id": "1",
    "timestamp": 1645678920,
    "data": {
        "status": "online",
        "uptime": 3600,
        "memory_usage": 75
    }
}
```
- 服务器响应 (服务器 -> 设备)
```json
{
    "type": "heartbeat_reply",
    "timestamp": 1645678921,
    "status": "success",
    "data": {
        "server_time": 1645678921
    }
}
```

#### 5. 紧急控制
- 服务器命令 (服务器 -> 设备)
```json
{
    "type": "emergency_control",
    "timestamp": 1645678930,
    "data": {
        "action": "emergency_open", // emergency_open或emergency_close
        "reason": "消防演练",
        "duration": 3600  // 持续时间(秒)，0表示一直持续到取消命令
    }
}
```
- 设备响应 (设备 -> 服务器)
```json
{
    "type": "emergency_control_reply",
    "device_id": "1",
    "timestamp": 1645678931,
    "status": "success",
    "data": {
        "action": "emergency_open",
        "message": "已执行紧急开门"
    }
}
```

#### 6. 设备状态上报
- 设备发送 (设备 -> 服务器)
```json
{
    "type": "status_report",
    "device_id": "1",
    "timestamp": 1645678940,
    "data": {
        "status": "online",
        "door_status": "closed", // open或closed
        "last_card_read": "RFID_001",
        "error_code": 0,
        "temperature": 25,
        "battery": 90
    }
}
```

### 通信说明
1. 所有时间戳使用UNIX时间戳（秒）
2. 所有消息必须包含type、timestamp字段
3. device_id必须与数据库中的设备ID匹配
4. 设备应当在接收到服务器响应后才执行相应动作
5. 心跳包间隔建议为30秒
6. 所有通信应启用MQTT QoS 1以确保消息送达

### 错误处理
服务器在响应出错时将返回：
```json
{
    "type": "error",
    "timestamp": 1645678999,
    "status": "error",
    "error": {
        "code": 1001,
        "message": "卡片已过期"
    }
}
```

常见错误代码：
- 1001: 卡片过期
- 1002: 卡片无效
- 1003: 设备未授权
- 1004: 超出预约时间
- 1005: 系统维护中

## 数据库设计
详细数据库设计文档请参考 src/main/resources/static/db.sql

## 贡献指南
1. Fork 本仓库
2. 创建新的分支
3. 提交代码
4. 创建 Pull Request

## 版本历史
- v1.0.0: 初始版本
  - 基础预约功能
  - 门禁控制
  - 管理员后台

## 联系方式
如有问题请提交 Issue 或发送邮件至：[管理员邮箱]

## 许可证
MIT License
