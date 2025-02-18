-- 数据库 xkck：高校门禁预约系统数据库，采用 utf8mb4 字符集
DROP DATABASE IF EXISTS `xkck`;
CREATE DATABASE `xkck`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;
USE `xkck`;

-- 访客表：记录用户基本信息
CREATE TABLE visitor (
    visitor_id INT PRIMARY KEY AUTO_INCREMENT,          -- 访客ID，自增长主键
    name VARCHAR(50) NOT NULL,                            -- 访客姓名
    phone VARCHAR(15) NOT NULL,                           -- 联系电话
    wechat_openid VARCHAR(100),                            -- 微信 OpenID
    id_type ENUM('school_id','id_card', 'passport', 'other') 
    DEFAULT 'id_card', -- 证件类型(学校身份号、身份证、护照、其它)
    id_number VARCHAR(20) NOT NULL,                       -- 证件号码
    role ENUM('admin', 'visitor') DEFAULT 'visitor',    -- 用户角色：管理员/访客
    account_status ENUM('normal', 'disabled', 'blacklist') DEFAULT 'normal', -- 账号状态 正常/禁用/黑名单
    hidden BOOLEAN DEFAULT FALSE,                         -- 是否隐藏（删除）用户
    password_hash VARCHAR(255) NOT NULL, -- 密码哈希
    last_login_time TIMESTAMP,          -- 新增：最后登录时间
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 更新时间
);

-- 门禁设备表：记录各个校园门禁设备（分布在校园不同入口或设施）
CREATE TABLE access_device (
    device_id INT PRIMARY KEY AUTO_INCREMENT,           -- 设备ID，自增长主键
    location VARCHAR(100) NOT NULL,                       -- 设备所在位置（如“校园正门”、“西门”、“保安管理处”等）
    ip_address VARCHAR(15),                               -- 设备IP地址，用于网络通信或远程管理
    mac_address VARCHAR(20),                              -- 设备MAC地址，用于网络通信或远程管理
    status ENUM('online', 'offline', 'maintenance') DEFAULT 'online', -- 设备状态
    device_type ENUM('campus_gate', 'facility_gate', 'management') DEFAULT 'campus_gate', -- 设备类型：校园大门、设施门禁、管理设备
    last_heartbeat_time TIMESTAMP,      -- 最后心跳时间
    firmware_version VARCHAR(50),       -- 固件版本
    door_status ENUM('open', 'closed') DEFAULT 'closed', -- 门禁状态
    description TEXT,                                     -- 设备描述
    UNIQUE KEY uniq_mac (mac_address)  -- 新增：MAC地址唯一索引
);

-- 预约表：记录用户预约整个校园通行的有效时间段（进入校园需在有效期内，离开无时限）
CREATE TABLE reservation (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,       -- 预约记录ID，自增长主键
    visitor_id INT NOT NULL,                              -- 预约的访客ID
    reason TEXT NOT NULL,                                 -- 访问事由
    host_department VARCHAR(100),                         -- 被访部门
    host_name VARCHAR(50),                                -- 被访人姓名
    start_time TIMESTAMP NOT NULL,                         -- 预约开始时间（进入校园必须在此时间后，并在有效期内）
    end_time TIMESTAMP NOT NULL,                           -- 预约结束时间（预约有效截止时间）
    host_confirm ENUM('pending', 'confirmed', 'rejected') DEFAULT 'pending', -- 被访人确认状态
    status ENUM('pending', 'confirmed', 'used', 'canceled') DEFAULT 'pending', -- 预约处理状态
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,       -- 预约创建时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 预约更新时间
    remarks TEXT,                                -- 备注信息
    hidden BOOLEAN DEFAULT FALSE,                        -- 是否隐藏（删除）预约记录
    FOREIGN KEY (visitor_id) REFERENCES visitor(visitor_id), -- 外键，关联访客表
    INDEX idx_time (start_time, end_time)                  -- 索引，加速时间段查询
);

-- RFID 卡片表：记录发放给访客的门禁卡信息，对应预约记录
CREATE TABLE rfid_card (
    card_id INT PRIMARY KEY AUTO_INCREMENT,
    uid VARCHAR(50) UNIQUE NOT NULL,                     -- 卡片物理UID
    status ENUM('available', 'issued', 'lost', 'deactivated', 'damage') DEFAULT 'available',  -- 卡片状态: 可用、已发放、挂失、注销、损坏
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 卡片登记时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remarks VARCHAR(255)                                -- 卡片备注信息
);

-- RFID卡使用记录表：记录卡片使用历史，包括进出记录、卡片状态变更等
CREATE TABLE rfid_card_record (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    card_id INT NOT NULL,                              -- 关联卡片ID
    reservation_id INT NOT NULL,                       -- 关联预约记录
    admin_id INT NOT NULL,                            -- 操作管理员
    operation_type ENUM('issue', 'return', 'report_lost', 'deactivate') 
    NOT NULL, -- 操作类型：发卡、还卡、挂失、注销
    issue_time TIMESTAMP,                              -- 发卡时间
    return_time TIMESTAMP,                             -- 归还时间
    expiration_time TIMESTAMP,                -- 有效期
    remarks VARCHAR(255),                             -- 操作备注
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- 记录创建时间
    hidden BOOLEAN DEFAULT FALSE,                     -- 是否隐藏（删除）记录
    FOREIGN KEY (card_id) REFERENCES rfid_card(card_id),
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id),
    FOREIGN KEY (admin_id) REFERENCES visitor(visitor_id),
    INDEX idx_card_time (card_id, create_time)
);

-- 进出记录表：记录访客在校园各门禁设备刷卡进出情况
CREATE TABLE access_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,              -- 日志ID，自增长主键
    visitor_id INT NOT NULL,                             -- 访客ID，记录执行进出操作的用户
    device_id INT NOT NULL,                              -- 门禁设备ID，记录在哪个设备刷卡
    card_id INT NOT NULL,                                -- RFID卡片ID
    reservation_id INT NOT NULL,                         -- 关联预约记录ID
    access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,       -- 刷卡时间
    access_type ENUM('entry', 'exit'),                   -- 进出类型：进门或出门
    result ENUM('allowed', 'denied'),                    -- 访问结果：允许或拒绝
    reason VARCHAR(200),                                 -- 访问说明或拒绝原因
    hidden BOOLEAN DEFAULT FALSE,                        -- 是否隐藏（删除）记录
    FOREIGN KEY (visitor_id) REFERENCES visitor(visitor_id), -- 外键，关联访客表
    FOREIGN KEY (device_id) REFERENCES access_device(device_id), -- 外键，关联设备表
    FOREIGN KEY (card_id) REFERENCES rfid_card(card_id),     -- 外键，关联卡片表
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id), -- 外键，关联预约表
    INDEX idx_device_access_time (device_id, access_time) -- 索引，加速查询
);

-- 每日配额设置表：整个校园每天预约的最大数量
CREATE TABLE quota_setting (
    quota_id INT PRIMARY KEY AUTO_INCREMENT,             -- 配额设置ID，自增长主键
    date DATE NOT NULL,                                  -- 配额生效日期
    max_quota INT NOT NULL,                              -- 当天全校最大预约数量
    current_count INT DEFAULT 0,                         -- 当天已预约数量
    special_event VARCHAR(100),                          -- 特殊事件说明
    is_holiday BOOLEAN DEFAULT FALSE,                    -- 是否节假日
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新时间
    UNIQUE KEY uniq_date (date)                         -- 保证单日只有一条配额记录
);

-- 预测表：存储预测的全校人流量数据
CREATE TABLE prediction (
    prediction_id INT PRIMARY KEY AUTO_INCREMENT,        -- 预测记录ID，自增长主键
    predict_date DATE NOT NULL,                          -- 预测日期
    predicted_count INT NOT NULL,                        -- 预测总人流量
    accuracy DECIMAL(5,2),                               -- 预测准确度（百分比）
    actual_count INT,                                    -- 实际人数（用于对比）
    confidence DECIMAL(5,2),                             -- 预测置信度（百分比）
    factors JSON,                                       -- 预测因素（JSON格式）
    model_version VARCHAR(50),                           -- 预测模型版本
    generate_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- 生成时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新时间
    INDEX idx_predict_date (predict_date)                -- 索引，加速查询
);

-- 操作日志表：记录管理员对预约、门禁卡等操作的详细日志
CREATE TABLE operation_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,               -- 操作日志ID，自增长主键
    operator_id INT NOT NULL,                             -- 操作员ID（管理员或其它操作人）
    operation_type VARCHAR(50) NOT NULL,                  -- 操作类型，如 "ISSUE_RFID", "APPROVE_RESERVATION" 等
    target_id INT,                                       -- 操作对象的ID（如预约记录、卡片记录等）
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- 操作时间
    details JSON,                                        -- 操作详情，记录变更前后信息
    FOREIGN KEY (operator_id) REFERENCES visitor(visitor_id) -- 外键（管理员记录在 Visitor 表中）
);

-- 消息日志表：记录硬件端与服务端通过 MQTT 通信的消息数据，用于调试追踪
CREATE TABLE message_log (
    message_id INT PRIMARY KEY AUTO_INCREMENT,           -- 消息日志ID，自增长主键
    device_id INT NOT NULL,                               -- 发起通信的门禁设备ID
    payload TEXT,                                       -- 消息内容（JSON格式）
    receive_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,       -- 消息接收时间
    status VARCHAR(50),                                   -- 消息处理状态或描述
    FOREIGN KEY (device_id) REFERENCES access_device(device_id) -- 外键，关联设备表
);

-- 黑名单记录表：记录被列入黑名单的访客信息
CREATE TABLE blacklist_record (
    record_id INT PRIMARY KEY AUTO_INCREMENT,          -- 黑名单记录ID，自增长主键
    visitor_id INT NOT NULL,                          -- 访客ID
    reason TEXT NOT NULL,                           -- 列入黑名单原因
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 列入黑名单时间
    end_time TIMESTAMP,                               -- NULL表示永久
    operator_id INT NOT NULL,                      -- 操作员ID
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 记录创建时间
    FOREIGN KEY (visitor_id) REFERENCES visitor(visitor_id),
    FOREIGN KEY (operator_id) REFERENCES visitor(visitor_id),
    INDEX idx_visitor (visitor_id),
    INDEX idx_time (start_time, end_time)
);

-- 1. 基础设备和管理员数据
-- 1.1 插入服务器设备
INSERT INTO access_device (device_id, location, ip_address, mac_address, status, device_type, description)
VALUES (-1, '服务器', '0.0.0.0', '00:00:00:00:00:00', 'online', 'management', '系统服务器');

-- 1.2 插入门禁设备
INSERT INTO access_device (location, ip_address, mac_address, status, device_type, description)
VALUES 
('校园正门', '192.168.1.101', '00:1A:2B:3C:4D:5E', 'online', 'campus_gate', '校园主入口门禁'),
('校园西门', '192.168.1.102', '00:1A:2B:3C:4D:5F', 'online', 'campus_gate', '校园侧门门禁'),
('行政楼大厅', '192.168.1.105', '00:1A:2B:3C:4D:62', 'online', 'management', 'RFID卡发放设备');

-- 1.3 插入管理员信息
INSERT INTO visitor (
    name, phone, id_type, id_number, role, account_status, 
    password_hash, create_time
) VALUES (
    '阿明', '13800138001', 'id_card', 'ADMIN001', 
    'admin', 'normal',
    'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', -- SHA256：123
    '2025-01-01 00:00:00'
);

-- 1.4 初始化RFID卡片
INSERT INTO rfid_card (uid, status, create_time, remarks) VALUES 
('RFID_TEST_001', 'available', '2025-01-01 00:00:00', '新登记卡片'),
('RFID_TEST_002', 'available', '2025-01-01 00:00:00', '新登记卡片'),
('RFID_TEST_003', 'available', '2025-01-01 00:00:00', '新登记卡片'),
('RFID_TEST_004', 'available', '2025-01-01 00:00:00', '新登记卡片'),
('RFID_TEST_005', 'available', '2025-01-01 00:00:00', '新登记卡片'),
('RFID_TEST_006', 'available', '2025-01-01 00:00:00', '新登记卡片');

-- 2. 按时序插入测试数据

-- 2.1 管理员设置2025-2-10配额(2025-02-09 09:00:00)
INSERT INTO quota_setting (
    date, max_quota, current_count, create_time
) VALUES (
    '2025-02-10', 100, 0, '2025-02-09 09:00:00'
);

-- 记录配额设置操作
INSERT INTO operation_log (
    operator_id, operation_type, operation_time, details
) VALUES (
    1, 'SET_QUOTA', '2025-02-09 09:00:00',
    JSON_OBJECT(
        'date', '2025-02-10',
        'quota', 100
    )
);

-- 2.2 访客注册(2025-02-09 10:30:00)
INSERT INTO visitor (
    name, phone, id_type, id_number, role, 
    account_status, password_hash, create_time
) VALUES (
    '小明', '13800138099', 'id_card', '440101199001011234',
    'visitor', 'normal',
    'pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=', -- SHA256：123
    '2025-02-09 10:30:00'
);

SET @visitor_id = LAST_INSERT_ID();

-- 2.3 访客提交预约(2025-02-09 10:35:00)
INSERT INTO reservation (
    visitor_id, reason, host_department, host_name,
    start_time, end_time, host_confirm, status,
    create_time
) VALUES (
    @visitor_id, '项目洽谈', '计算机学院', '刘老师',
    '2025-02-10 10:00:00', '2025-02-10 19:00:00',
    'pending', 'pending',
    '2025-02-09 10:35:00'
);

SET @reservation_id = LAST_INSERT_ID();

-- 2.4 管理员审核通过(2025-02-09 14:00:00)
UPDATE reservation 
SET status = 'confirmed',
    host_confirm = 'confirmed',
    update_time = '2025-02-09 14:00:00'
WHERE reservation_id = @reservation_id;

-- 记录审核操作
INSERT INTO operation_log (
    operator_id, operation_type, target_id,
    operation_time, details
) VALUES (
    1, 'APPROVE_RESERVATION', @reservation_id,
    '2025-02-09 14:00:00',
    JSON_OBJECT(
        'reservation_id', @reservation_id,
        'status', 'confirmed'
    )
);

-- 2.5 访客领取RFID卡(2025-02-10 09:30:00)
UPDATE rfid_card 
SET status = 'issued',
    update_time = '2025-02-10 09:30:00'
WHERE uid = 'RFID_TEST_001';

-- 记录发卡操作
INSERT INTO rfid_card_record (
    card_id, reservation_id, admin_id,
    operation_type, issue_time, expiration_time,
    create_time
) VALUES (
    1, @reservation_id, 1,
    'issue', '2025-02-10 09:30:00', '2025-02-10 19:00:00',
    '2025-02-10 09:30:00'
);

-- 2.6 访客进出记录

-- 2.6.1 提前到达(2025-02-10 09:45:00，拒绝进入)
INSERT INTO access_log (
    visitor_id, device_id, card_id, reservation_id,
    access_time, access_type, result, reason
) VALUES (
    @visitor_id, 1, 1, @reservation_id,
    '2025-02-10 09:45:00', 'entry', 'denied', 
    '未到预约开始时间'
);

-- 记录MQTT消息
INSERT INTO message_log (
    device_id, payload, receive_time, status
) VALUES (
    1,
    '{"uid":"RFID_TEST_001","action":"entry","timestamp":"2025-02-10 09:45:00"}',
    '2025-02-10 09:45:05',
    'processed'
);

-- 2.6.2 正常进入(2025-02-10 10:05:00)
INSERT INTO access_log (
    visitor_id, device_id, card_id, reservation_id,
    access_time, access_type, result, reason
) VALUES (
    @visitor_id, 1, 1, @reservation_id,
    '2025-02-10 10:05:00', 'entry', 'allowed',
    '预约时间内进入'
);

-- 记录MQTT消息
INSERT INTO message_log (
    device_id, payload, receive_time, status
) VALUES (
    1,
    '{"uid":"RFID_TEST_001","action":"entry","timestamp":"2025-02-10 10:05:00"}',
    '2025-02-10 10:05:05',
    'processed'
);

-- 2.6.3 预约结束后离开(2025-02-10 19:30:00)
INSERT INTO access_log (
    visitor_id, device_id, card_id, reservation_id,
    access_time, access_type, result, reason
) VALUES (
    @visitor_id, 1, 1, @reservation_id,
    '2025-02-10 19:30:00', 'exit', 'allowed',
    '允许离开(预约结束后)'
);

-- 记录MQTT消息
INSERT INTO message_log (
    device_id, payload, receive_time, status
) VALUES (
    1,
    '{"uid":"RFID_TEST_001","action":"exit","timestamp":"2025-02-10 19:30:00"}',
    '2025-02-10 19:30:05',
    'processed'
);

-- 2.7 访客归还RFID卡(2025-02-10 19:35:00)
UPDATE rfid_card 
SET status = 'available',
    update_time = '2025-02-10 19:35:00'
WHERE uid = 'RFID_TEST_001';

-- 记录还卡操作
INSERT INTO rfid_card_record (
    card_id, reservation_id, admin_id,
    operation_type, return_time,
    create_time
) VALUES (
    1, @reservation_id, 1,
    'return', '2025-02-10 19:35:00',
    '2025-02-10 19:35:00'
);

-- 记录归还操作日志
INSERT INTO operation_log (
    operator_id, operation_type, target_id,
    operation_time, details
) VALUES (
    1, 'RETURN_RFID', 1,
    '2025-02-10 19:35:00',
    JSON_OBJECT(
        'card_uid', 'RFID_TEST_001',
        'visitor_id', @visitor_id,
        'status', 'returned'
    )
);
