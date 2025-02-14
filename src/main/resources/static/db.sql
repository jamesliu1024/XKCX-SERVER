-- 数据库 xkck：高校门禁预约系统数据库，采用 utf8mb4 字符集
DROP DATABASE IF EXISTS `xkck`;
CREATE DATABASE `xkck`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;
USE `xkck`;

-- 访客表：记录用户基本信息
CREATE TABLE Visitor (
    visitor_id INT PRIMARY KEY AUTO_INCREMENT,          -- 访客ID，自增长主键
    name VARCHAR(50) NOT NULL,                            -- 访客姓名
    phone VARCHAR(15) NOT NULL,                           -- 联系电话
    wechat_openid VARCHAR(100),                            -- 微信 OpenID
    id_type ENUM('id_card', 'passport', 'other') DEFAULT 'id_card', -- 证件类型
    id_number VARCHAR(20) NOT NULL,                       -- 证件号码
    reason TEXT NOT NULL,                                 -- 访问事由
    host_department VARCHAR(100),                         -- 被访部门
    host_name VARCHAR(50),                                -- 被访人姓名
    status ENUM('pending', 'approved', 'expired') DEFAULT 'pending', -- 审核状态
    expire_time DATETIME,                                 -- 访问有效期（预约开始时有效，超过该时间后可自由离开）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP        -- 创建时间
);

-- 门禁设备表：记录各个校园门禁设备（分布在校园不同入口或设施）
CREATE TABLE AccessDevice (
    device_id INT PRIMARY KEY AUTO_INCREMENT,           -- 设备ID，自增长主键
    location VARCHAR(100) NOT NULL,                       -- 设备所在位置（如“校园正门”、“西门”、“保安管理处”等）
    ip_address VARCHAR(15),                               -- 设备IP地址，用于网络通信或远程管理
    status ENUM('online', 'offline', 'maintenance') DEFAULT 'online', -- 设备状态
    device_type ENUM('campus_gate', 'facility_gate', 'management') DEFAULT 'campus_gate', -- 设备类型：校园大门、设施门禁、管理设备
    description TEXT                                     -- 设备描述
);

-- 预约表：记录用户预约整个校园通行的有效时间段（进入校园需在有效期内，离开无时限）
CREATE TABLE Reservation (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,       -- 预约记录ID，自增长主键
    visitor_id INT NOT NULL,                              -- 预约的访客ID
    start_time DATETIME NOT NULL,                         -- 预约开始时间（进入校园必须在此时间后，并在有效期内）
    end_time DATETIME NOT NULL,                           -- 预约结束时间（预约有效截止时间）
    host_confirm ENUM('pending', 'confirmed', 'rejected') DEFAULT 'pending', -- 被访人确认状态
    status ENUM('pending', 'confirmed', 'used', 'canceled') DEFAULT 'pending', -- 预约处理状态
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,       -- 预约创建时间
    FOREIGN KEY (visitor_id) REFERENCES Visitor(visitor_id), -- 外键，关联访客表
    INDEX idx_time (start_time, end_time)                  -- 索引，加速时间段查询
);

-- RFID 卡片表：记录发放给访客的门禁卡信息，对应预约记录
CREATE TABLE RFIDCard (
    card_id INT PRIMARY KEY AUTO_INCREMENT,              -- 卡片ID，自增长主键
    uid VARCHAR(50) UNIQUE NOT NULL,                      -- RFID 卡物理 UID，唯一标识
    issue_time DATETIME DEFAULT CURRENT_TIMESTAMP,        -- 发卡时间
    return_time DATETIME DEFAULT NULL,                    -- 回收时间（如卡片回收时记录）
    status ENUM('available', 'issued', 'lost', 'deactivated') DEFAULT 'available', -- 卡片状态
    expiration_time DATETIME,                             -- 卡片失效时间（与预约结束时间保持一致）
    reservation_id INT,                                   -- 关联的预约记录ID
    last_admin_id INT NOT NULL,                           -- 最后操作管理员的 ID
    remarks VARCHAR(255),                                 -- 备注信息
    FOREIGN KEY (reservation_id) REFERENCES Reservation(reservation_id) -- 外键，关联预约表
);

-- 进出记录表：记录访客在校园各门禁设备刷卡进出情况
CREATE TABLE AccessLog (
    log_id INT PRIMARY KEY AUTO_INCREMENT,              -- 日志ID，自增长主键
    visitor_id INT NOT NULL,                             -- 访客ID，记录执行进出操作的用户
    device_id INT NOT NULL,                              -- 门禁设备ID，记录在哪个设备刷卡
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP,       -- 刷卡时间
    access_type ENUM('entry', 'exit'),                   -- 进出类型：进门或出门
    result ENUM('allowed', 'denied'),                    -- 访问结果：允许或拒绝
    reason VARCHAR(200),                                 -- 访问说明或拒绝原因
    FOREIGN KEY (visitor_id) REFERENCES Visitor(visitor_id), -- 外键，关联访客表
    FOREIGN KEY (device_id) REFERENCES AccessDevice(device_id), -- 外键，关联设备表
    INDEX idx_device_access_time (device_id, access_time) -- 索引，加速查询
);

-- 每日配额设置表：整个校园每天预约的最大数量
CREATE TABLE QuotaSetting (
    quota_id INT PRIMARY KEY AUTO_INCREMENT,             -- 配额设置ID，自增长主键
    date DATE NOT NULL,                                  -- 配额生效日期
    max_quota INT NOT NULL,                              -- 当天全校最大预约数量
    UNIQUE KEY uniq_date (date)                         -- 保证单日只有一条配额记录
);

-- 预测表：存储预测的全校人流量数据
CREATE TABLE Prediction (
    prediction_id INT PRIMARY KEY AUTO_INCREMENT,        -- 预测记录ID，自增长主键
    predict_date DATE NOT NULL,                          -- 预测日期
    predicted_count INT NOT NULL,                        -- 预测总人流量
    generate_time DATETIME DEFAULT CURRENT_TIMESTAMP,      -- 生成时间
    INDEX idx_predict_date (predict_date)                -- 索引，加速查询
);

-- 操作日志表：记录管理员对预约、门禁卡等操作的详细日志
CREATE TABLE OperationLog (
    log_id INT PRIMARY KEY AUTO_INCREMENT,               -- 操作日志ID，自增长主键
    operator_id INT NOT NULL,                             -- 操作员ID（管理员或其它操作人）
    operation_type VARCHAR(50) NOT NULL,                  -- 操作类型，如 "ISSUE_RFID", "APPROVE_RESERVATION" 等
    target_id INT,                                       -- 操作对象的ID（如预约记录、卡片记录等）
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP,     -- 操作时间
    details JSON,                                        -- 操作详情，记录变更前后信息
    FOREIGN KEY (operator_id) REFERENCES Visitor(visitor_id) -- 外键（管理员记录在 Visitor 表中）
);

-- 消息日志表：记录硬件端与服务端通过 MQTT 通信的消息数据，用于调试追踪
CREATE TABLE MessageLog (
    message_id INT PRIMARY KEY AUTO_INCREMENT,           -- 消息日志ID，自增长主键
    device_id INT NOT NULL,                               -- 发起通信的门禁设备ID
    payload TEXT,                                       -- 消息内容（JSON格式）
    receive_time DATETIME DEFAULT CURRENT_TIMESTAMP,       -- 消息接收时间
    status VARCHAR(50),                                   -- 消息处理状态或描述
    FOREIGN KEY (device_id) REFERENCES AccessDevice(device_id) -- 外键，关联设备表
);

-- 插入访客信息
INSERT INTO Visitor (name, phone, id_type, id_number, reason, host_department, host_name, status, expire_time)
VALUES 
('张三', '13800138001', 'id_card', '110101199001011234', '业务洽谈', '信息中心', '李主任', 'approved', '2024-12-20 23:59:59'),
('李四', '13800138002', 'passport', 'E12345678', '学术交流', '国际处', '王老师', 'pending', NULL),
('王五', '13800138003', 'id_card', '110101199002021234', '参观校园', '招生办', '张主任', 'approved', '2024-12-21 23:59:59'),
('赵六', '13800138004', 'other', 'TEMP_001', '临时访问', '保卫处', '刘科长', 'expired', '2024-12-19 23:59:59'),
('孙七', '13800138005', 'id_card', '110101199003031234', '面试', '人事处', '赵主任', 'approved', '2025-01-15 23:59:59'),
('周八', '13800138006', 'passport', 'E87654321', '参观实验室', '实验室', '陈教授', 'approved', '2025-01-20 23:59:59');

-- 插入门禁设备
INSERT INTO AccessDevice (location, ip_address, status, device_type, description)
VALUES 
('校园正门', '192.168.1.101', 'online', 'campus_gate', '校园主入口门禁'),
('校园西门', '192.168.1.102', 'online', 'campus_gate', '校园侧门门禁'),
('图书馆东门', '192.168.1.103', 'online', 'facility_gate', '图书馆主入口门禁'),
('体育馆西门', '192.168.1.104', 'online', 'facility_gate', '体育馆侧门门禁'),
('行政楼大厅', '192.168.1.105', 'online', 'management', 'RFID卡发放设备'),
('实验楼南门', '192.168.1.106', 'offline', 'facility_gate', '实验楼主入口门禁');

-- 插入预约记录
INSERT INTO Reservation (visitor_id, start_time, end_time, host_confirm, status)
VALUES 
(1, '2024-12-20 14:00:00', '2024-12-20 16:00:00', 'confirmed', 'confirmed'),  -- 张三预约校园正门
(3, '2024-12-21 10:00:00', '2024-12-21 12:00:00', 'confirmed', 'confirmed'),  -- 王五预约校园西门
(2, '2024-12-22 09:00:00', '2024-12-22 11:00:00', 'pending', 'pending'),       -- 李四预约校园正门（待确认）
(5, '2025-01-15 09:00:00', '2025-01-15 17:00:00', 'confirmed', 'confirmed'),  -- 孙七预约校园正门
(6, '2025-01-20 10:00:00', '2025-01-20 18:00:00', 'confirmed', 'confirmed');  -- 周八预约校园西门

-- 插入RFID卡
INSERT INTO RFIDCard (uid, status, expiration_time, reservation_id, last_admin_id, remarks)
VALUES 
('RFID_001', 'issued', '2024-12-20 23:59:59', 1, 1, '发放给张三'),  -- 绑定到张三的预约
('RFID_002', 'available', '2025-12-31 23:59:59', NULL, 1, '未发放'),  -- 未绑定
('RFID_003', 'issued', '2024-12-21 23:59:59', 2, 1, '发放给王五'),  -- 绑定到王五的预约
('RFID_004', 'lost', '2025-12-31 23:59:59', NULL, 1, '卡片丢失'),   -- 丢失卡片
('RFID_005', 'issued', '2025-01-15 23:59:59', 4, 1, '发放给孙七'),  -- 绑定到孙七的预约
('RFID_006', 'issued', '2025-01-20 23:59:59', 5, 1, '发放给周八');  -- 绑定到周八的预约

-- 插入进出记录：记录访客刷卡进出操作，并对 access_time 明确指定时间
INSERT INTO AccessLog (visitor_id, device_id, access_time, access_type, result, reason)
VALUES 
-- 张三尝试提前进入（预约开始时间为2024-12-20 14:00:00，实际刷卡13:55:00 → 应拒绝）
(1, 1, '2024-12-20 13:55:00', 'entry', 'denied', '未到预约开始时间'),
-- 张三预约内正常进入（14:05:00进入）
(1, 1, '2024-12-20 14:05:00', 'entry', 'allowed', '预约内进入'),
-- 张三离开时超出预约有效期（16:05:00离校 → 允许）
(1, 1, '2024-12-20 16:05:00', 'exit', 'allowed', '离校允许'),
-- 王五预约（预约开始2024-12-21 10:00:00）提前进入（09:50:00 → 拒绝）
(3, 2, '2024-12-21 09:50:00', 'entry', 'denied', '未到预约开始时间'),
-- 王五预约内正常进入（10:05:00进入）
(3, 2, '2024-12-21 10:05:00', 'entry', 'allowed', '预约内进入'),
-- 李四预约（预约开始2024-12-22 09:00:00）直接进入（09:10:00进入，尽管状态pending）
(2, 1, '2024-12-22 09:10:00', 'entry', 'allowed', '预约内进入'),
-- 李四离校（11:15:00离校）
(2, 1, '2024-12-22 11:15:00', 'exit', 'allowed', '离校允许'),
-- 孙七进入（预约2025-01-15 09:00:00开始，09:05:00进入）
(5, 1, '2025-01-15 09:05:00', 'entry', 'allowed', '预约内进入'),
-- 孙七离校（17:05:00离校）
(5, 1, '2025-01-15 17:05:00', 'exit', 'allowed', '离校允许'),
-- 周八进入（预约2025-01-20 10:00:00开始，10:10:00进入）
(6, 2, '2025-01-20 10:10:00', 'entry', 'allowed', '预约内进入'),
-- 周八离校（18:10:00离校）
(6, 2, '2025-01-20 18:10:00', 'exit', 'allowed', '离校允许');

-- 插入每日配额设置：日期使用具体日期
INSERT INTO QuotaSetting (date, max_quota)
VALUES 
('2024-12-20', 100),  -- 2024-12-20全校预约限额
('2024-12-21', 100),  -- 2024-12-21全校预约限额
('2024-12-22', 100),  -- 2024-12-22全校预约限额
('2025-01-15', 150),  -- 2025-01-15全校预约限额
('2025-01-20', 200);  -- 2025-01-20全校预约限额

-- 插入操作日志：为每条操作记录指定明确的操作时间
INSERT INTO OperationLog (operator_id, operation_type, target_id, operation_time, details)
VALUES 
(1, 'ISSUE_RFID', 1, '2024-12-20 13:00:00', '{"card_uid": "RFID_001", "visitor_id": 1}'),  -- 张三RFID发卡操作
(1, 'APPROVE_RESERVATION', 1, '2024-12-20 13:05:00', '{"reservation_id": 1, "status": "confirmed"}'),  -- 批准张三的预约
(1, 'ISSUE_RFID', 3, '2024-12-21 09:30:00', '{"card_uid": "RFID_003", "visitor_id": 3}'),  -- 王五RFID发卡操作
(1, 'APPROVE_RESERVATION', 2, '2024-12-21 09:35:00', '{"reservation_id": 2, "status": "confirmed"}'),  -- 批准王五的预约
(1, 'ISSUE_RFID', 4, '2025-01-15 08:50:00', '{"card_uid": "RFID_005", "visitor_id": 5}'),  -- 孙七RFID发卡操作
(1, 'APPROVE_RESERVATION', 4, '2025-01-15 08:55:00', '{"reservation_id": 4, "status": "confirmed"}'),  -- 批准孙七的预约
(1, 'ISSUE_RFID', 5, '2025-01-20 09:50:00', '{"card_uid": "RFID_006", "visitor_id": 6}'),  -- 周八RFID发卡操作
(1, 'APPROVE_RESERVATION', 5, '2025-01-20 09:55:00', '{"reservation_id": 5, "status": "confirmed"}');  -- 批准周八的预约

-- 插入预测数据：为每天生成预测数据时指定具体生成时间
INSERT INTO Prediction (predict_date, predicted_count, generate_time)
VALUES 
('2024-12-20', 120, '2024-12-19 18:00:00'),  -- 2024-12-20预测数据
('2024-12-21', 80, '2024-12-20 18:00:00'),   -- 2024-12-21预测数据
('2025-01-15', 150, '2025-01-14 18:00:00'),  -- 2025-01-15预测数据
('2025-01-20', 200, '2025-01-19 18:00:00');  -- 2025-01-20预测数据

-- 插入消息日志：为每条消息日志指定具体消息接收时间
INSERT INTO MessageLog (device_id, payload, receive_time, status)
VALUES 
(1, '{"uid": "RFID_001", "action": "entry", "timestamp": "2024-12-20 14:05:00"}', '2024-12-20 14:05:05', 'processed'),  -- 张三进门
(1, '{"uid": "RFID_001", "action": "exit", "timestamp": "2024-12-20 16:05:00"}', '2024-12-20 16:05:05', 'processed'),   -- 张三离门
(2, '{"uid": "RFID_003", "action": "entry", "timestamp": "2024-12-21 10:05:00"}', '2024-12-21 10:05:05', 'processed'),  -- 王五进门
(1, '{"uid": "RFID_005", "action": "entry", "timestamp": "2025-01-15 09:05:00"}', '2025-01-15 09:05:05', 'processed'),  -- 孙七进门
(1, '{"uid": "RFID_005", "action": "exit", "timestamp": "2025-01-15 17:05:00"}', '2025-01-15 17:05:05', 'processed'),   -- 孙七离门
(2, '{"uid": "RFID_006", "action": "entry", "timestamp": "2025-01-20 10:10:00"}', '2025-01-20 10:10:05', 'processed'),  -- 周八进门
(2, '{"uid": "RFID_006", "action": "exit", "timestamp": "2025-01-20 18:10:00"}', '2025-01-20 18:10:05', 'processed');   -- 周八离门