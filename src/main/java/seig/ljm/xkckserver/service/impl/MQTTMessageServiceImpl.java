package seig.ljm.xkckserver.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.entity.AccessLog;
import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.entity.RfidCard;
import seig.ljm.xkckserver.entity.RfidCardRecord;
import seig.ljm.xkckserver.mqtt.MQTTGateway;
import seig.ljm.xkckserver.mqtt.dto.*;
import seig.ljm.xkckserver.service.*;

import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MQTTMessageServiceImpl implements MQTTMessageService {
    private final ObjectMapper objectMapper;
    private final MessageLogService messageLogService;
    private final RfidCardService rfidCardService;
    private final RfidCardRecordService rfidCardRecordService;
    private final AccessDeviceService accessDeviceService;
    private final MQTTGateway mqttGateway;
    private final AccessLogService accessLogService;
    private final DelayedMqttService delayedMqttService;

    @Autowired
    public MQTTMessageServiceImpl(
            ObjectMapper objectMapper,
            MessageLogService messageLogService,
            RfidCardService rfidCardService,
            RfidCardRecordService rfidCardRecordService,
            @Lazy AccessDeviceService accessDeviceService,
            MQTTGateway mqttGateway,
            AccessLogService accessLogService,
            DelayedMqttService delayedMqttService) {
        this.objectMapper = objectMapper;
        this.messageLogService = messageLogService;
        this.rfidCardService = rfidCardService;
        this.rfidCardRecordService = rfidCardRecordService;
        this.accessDeviceService = accessDeviceService;
        this.mqttGateway = mqttGateway;
        this.accessLogService = accessLogService;
        this.delayedMqttService = delayedMqttService;
    }

    @Override
    public void handleMessage(String topic, String payload) {
        try {
            // 记录接收到的消息
            MessageLog messageLog = new MessageLog();
            messageLog.setPayload(payload);
            messageLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            messageLog.setStatus("received");

            // 解析简单文本格式消息
            String[] parts = payload.split("\\|");
            if (parts.length < 2) {
                log.error("Invalid message format: {}", payload);
                messageLog.setStatus("error");
                messageLogService.save(messageLog);
                return;
            }

            String type = parts[0];
            String deviceId = parts[1];
            messageLog.setDeviceId(Integer.parseInt(deviceId));
            
            try {
                switch (type) {
                    case "connect":
                        handleConnect(payload);
                        messageLog.setStatus("processed");
                        break;
                    case "verify_card":
                        handleVerifyCard(payload);
                        messageLog.setStatus("processed");
                        break;
                    case "heartbeat":
                        handleHeartbeat(payload);
                        messageLog.setStatus("processed");
                        break;
                    case "status_report":
                        handleStatusReport(payload);
                        messageLog.setStatus("processed");
                        break;
                    default:
                        log.warn("Unknown message type: {}", type);
                        messageLog.setStatus("unknown_type");
                }
            } catch (Exception e) {
                log.error("Error processing message: ", e);
                messageLog.setStatus("error");
            }
            
            messageLogService.save(messageLog);
        } catch (Exception e) {
            log.error("Error handling MQTT message: ", e);
        }
    }

    @Override
    public void handleConnect(String payload) {
        try {
            // 解析简单文本格式的连接消息
            String[] parts = payload.split("\\|");
            if (parts.length < 2 || !parts[0].equals("connect")) {
                log.error("Invalid connect message format: {}", payload);
                return;
            }

            Integer deviceId = Integer.parseInt(parts[1]);
            
            // 查找或创建设备记录
            AccessDevice device = accessDeviceService.getById(deviceId);
            if (device == null) {
                device = new AccessDevice();
                device.setDeviceId(deviceId);
                device.setDeviceType("campus_gate"); // 默认设置为校园门禁
                device.setDescription("New device");
            }
            
            // 更新设备信息
            device.setStatus("online");
            accessDeviceService.saveOrUpdate(device);
            
            // 构建响应消息
            String replyPayload = String.format("connect_reply|%d|OK", deviceId);
            String topic = "xkck/device/" + deviceId + "/command";
            
            // 使用延迟发送服务
            delayedMqttService.sendDelayedMessage(topic, replyPayload, 2000);
            
            // 记录响应消息
            MessageLog replyLog = new MessageLog();
            replyLog.setDeviceId(deviceId);
            replyLog.setPayload(replyPayload);
            replyLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            replyLog.setStatus("sent");
            messageLogService.save(replyLog);
            
            log.info("Device {} connected successfully", deviceId);
        } catch (Exception e) {
            log.error("Error handling connect message: ", e);
        }
    }

    @Override
    public void handleVerifyCard(String payload) {
        try {
            // 解析卡片验证消息
            VerifyCardMessage verifyMessage = objectMapper.readValue(payload, VerifyCardMessage.class);
            Integer deviceId = Integer.parseInt(verifyMessage.getDeviceId());
            String uid = verifyMessage.getData().getUid();
            String action = verifyMessage.getData().getAction();
            
            // 查找卡片信息
            RfidCard card = rfidCardService.getCardByUid(uid);
            VerifyCardReplyMessage replyMessage = new VerifyCardReplyMessage();
            replyMessage.setDeviceId(String.valueOf(deviceId));
            replyMessage.setStatus("success");
            
            VerifyCardReplyMessage.VerifyCardReplyData replyData = new VerifyCardReplyMessage.VerifyCardReplyData();
            
            // 验证卡片
            if (card == null) {
                // 卡片不存在
                replyData.setAllow(false);
                replyData.setMessage("Invalid Card");
                replyData.setAction("deny_access");
                log.warn("Invalid card UID: {} at device: {}", uid, deviceId);
            } else if (!card.getStatus().equals("issued")) {
                // 卡片状态不正确
                replyData.setAllow(false);
                replyData.setMessage("Invalid Card Status: " + card.getStatus());
                replyData.setAction("deny_access");
                log.warn("Invalid card status: {} for card: {} at device: {}", card.getStatus(), uid, deviceId);
            } else {
                // 获取卡片最新的发放记录
                RfidCardRecord latestRecord = rfidCardRecordService.getLatestCardRecord(card.getCardId());
                
                if (latestRecord == null || !latestRecord.getOperationType().equals("issue")) {
                    // 没有有效的发放记录
                    replyData.setAllow(false);
                    replyData.setMessage("Card Not Issued");
                    replyData.setAction("deny_access");
                    log.warn("No valid issue record for card: {} at device: {}", uid, deviceId);
                } else if (latestRecord.getExpirationTime().isBefore(ZonedDateTime.now(TimeZoneConstant.ZONE_ID))) {
                    // 卡片已过期
                    replyData.setAllow(false);
                    replyData.setMessage("Card Expired");
                    replyData.setAction("deny_access");
                    log.warn("Expired card: {} at device: {}", uid, deviceId);
                } else {
                    // 卡片验证通过
                    replyData.setAllow(true);
                    replyData.setMessage("Access Granted");
                    replyData.setAction("open_door");
                    replyData.setExpireTime(latestRecord.getExpirationTime().toEpochSecond());
                    log.info("Card verification successful: {} at device: {}", uid, deviceId);
                }
            }
            
            replyMessage.setData(replyData);
            
            // 发送响应
            String replyPayload = objectMapper.writeValueAsString(replyMessage);
            mqttGateway.sendToMqtt("xkck/device/" + deviceId + "/command", replyPayload);
            
            // 记录访问日志
            AccessLog accessLog = new AccessLog();
            accessLog.setDeviceId(deviceId);
            accessLog.setAccessTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            accessLog.setAccessType(action);
            if (card != null) {
                RfidCardRecord latestRecord = rfidCardRecordService.getLatestCardRecord(card.getCardId());
                if (latestRecord != null) {
                    accessLog.setVisitorId(latestRecord.getReservationId());
                }
            }
            accessLog.setResult(replyData.isAllow() ? "allowed" : "denied");
            accessLog.setReason(replyData.getMessage());
            accessLogService.save(accessLog);
            
            // 记录响应消息
            MessageLog replyLog = new MessageLog();
            replyLog.setDeviceId(deviceId);
            replyLog.setPayload(replyPayload);
            replyLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            replyLog.setStatus("sent");
            messageLogService.save(replyLog);
            
        } catch (Exception e) {
            log.error("Error handling verify card message: ", e);
        }
    }

    @Override
    public void handleHeartbeat(String payload) {
        try {
            // 解析心跳消息
            HeartbeatMessage heartbeatMessage = objectMapper.readValue(payload, HeartbeatMessage.class);
            Integer deviceId = Integer.parseInt(heartbeatMessage.getDeviceId());
            
            // 更新设备状态
            AccessDevice device = accessDeviceService.getById(deviceId);
            if (device != null) {
                device.setStatus(heartbeatMessage.getData().getStatus());
                accessDeviceService.updateById(device);
                log.debug("Updated device {} status to: {}", deviceId, device.getStatus());
            } else {
                log.warn("Received heartbeat from unknown device: {}", deviceId);
            }
            
            // 构建响应消息
            HeartbeatReplyMessage replyMessage = new HeartbeatReplyMessage();
            replyMessage.setDeviceId(String.valueOf(deviceId));
            replyMessage.setStatus("success");
            
            HeartbeatReplyMessage.HeartbeatReplyData replyData = new HeartbeatReplyMessage.HeartbeatReplyData();
            replyData.setServerTime(System.currentTimeMillis() / 1000);
            replyMessage.setData(replyData);
            
            // 发送响应
            String replyPayload = objectMapper.writeValueAsString(replyMessage);
            mqttGateway.sendToMqtt("xkck/device/" + deviceId + "/command", replyPayload);
            
            // 记录心跳日志
            MessageLog heartbeatLog = new MessageLog();
            heartbeatLog.setDeviceId(deviceId);
            heartbeatLog.setPayload(payload);
            heartbeatLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            heartbeatLog.setStatus("processed");
            messageLogService.save(heartbeatLog);
            
            // 记录响应日志
            MessageLog replyLog = new MessageLog();
            replyLog.setDeviceId(deviceId);
            replyLog.setPayload(replyPayload);
            replyLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            replyLog.setStatus("sent");
            messageLogService.save(replyLog);
            
        } catch (Exception e) {
            log.error("Error handling heartbeat message: ", e);
        }
    }

    @Override
    public void handleStatusReport(String payload) {
        try {
            // 解析状态报告消息
            StatusReportMessage statusMessage = objectMapper.readValue(payload, StatusReportMessage.class);
            Integer deviceId = Integer.parseInt(statusMessage.getDeviceId());
            
            // 更新设备状态
            AccessDevice device = accessDeviceService.getById(deviceId);
            if (device != null) {
                // 更新设备状态信息
                device.setStatus(statusMessage.getData().getStatus());
                accessDeviceService.updateById(device);
                
                // 记录设备状态
                MessageLog statusLog = new MessageLog();
                statusLog.setDeviceId(deviceId);
                statusLog.setPayload(payload);
                statusLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
                statusLog.setStatus("processed");
                messageLogService.save(statusLog);
                
                // 检查是否有异常状态需要报警
                StatusReportMessage.StatusReportData data = statusMessage.getData();
                if (data.getErrorCode() != 0) {
                    log.warn("Device {} reported error: {}", deviceId, data.getErrorCode());
                }
                
                // 检查最近一次刷卡记录
                if (data.getLastCardRead() != null) {
                    RfidCard card = rfidCardService.getCardByUid(data.getLastCardRead());
                    if (card != null) {
                        log.info("Device {} last card read: {} at {}", deviceId, card.getUid(), 
                            ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
                    }
                }
                
                log.debug("Device {} status updated - Door: {}", 
                    deviceId, 
                    data.getDoorStatus());
            } else {
                log.warn("Received status report from unknown device: {}", deviceId);
            }
            
        } catch (Exception e) {
            log.error("Error handling status report message: ", e);
        }
    }

    @Override
    public boolean sendEmergencyControl(Integer deviceId, String action, String reason) {
        try {
            // 构建紧急控制消息
            BaseMessage emergencyMessage = new BaseMessage();
            emergencyMessage.setType("emergency_control");
            emergencyMessage.setDeviceId(String.valueOf(deviceId));
            
            // 构建消息数据
            EmergencyControlData data = new EmergencyControlData();
            data.setAction(action);
            data.setReason(reason);
            emergencyMessage.setData(data);
            
            // 转换为JSON并发送
            String payload = objectMapper.writeValueAsString(emergencyMessage);
            mqttGateway.sendToMqtt("xkck/device/" + deviceId + "/command", payload);
            
            // 记录消息日志
            MessageLog messageLog = new MessageLog();
            messageLog.setDeviceId(deviceId);
            messageLog.setPayload(payload);
            messageLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            messageLog.setStatus("sent");
            messageLogService.save(messageLog);
            
            return true;
        } catch (Exception e) {
            log.error("Error sending emergency control message: ", e);
            return false;
        }
    }

    @Data
    private static class EmergencyControlData {
        private String action;
        private String reason;
    }
}
