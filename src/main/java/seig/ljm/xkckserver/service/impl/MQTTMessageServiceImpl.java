package seig.ljm.xkckserver.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.mqtt.MQTTGateway;
import seig.ljm.xkckserver.mqtt.dto.*;
import seig.ljm.xkckserver.service.MQTTMessageService;
import seig.ljm.xkckserver.service.MessageLogService;
import seig.ljm.xkckserver.service.RFIDCardService;

@Slf4j
@Service
public class MQTTMessageServiceImpl implements MQTTMessageService {
    private ObjectMapper objectMapper;
    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    private MessageLogService messageLogService;
    @Autowired
    public void setMessageLogService(MessageLogService messageLogService) {
        this.messageLogService = messageLogService;
    }
    
    private RFIDCardService rfidCardService;
    @Autowired
    public void setRFIDCardService(RFIDCardService rfidCardService) {
        this.rfidCardService = rfidCardService;
    }

    @Override
    public void handleMessage(String topic, String payload) {
        try {
            // 记录接收到的消息
            MessageLog messageLog = new MessageLog();
            messageLog.setPayload(payload);
            // ...设置其他字段...
            messageLogService.save(messageLog);

            // 解析消息类型
            BaseMessage baseMessage = objectMapper.readValue(payload, BaseMessage.class);
            
            switch (baseMessage.getType()) {
                case "connect":
                    handleConnect(payload);
                    break;
                case "verify_card":
                    handleVerifyCard(payload);
                    break;
                case "heartbeat":
                    handleHeartbeat(payload);
                    break;
                case "status_report":
                    handleStatusReport(payload);
                    break;
                default:
                    log.warn("Unknown message type: {}", baseMessage.getType());
            }
        } catch (Exception e) {
            log.error("Error handling MQTT message: ", e);
        }
    }

    public void handleConnect(String payload) {
        // 处理设备连接消息
        // ...
    }

    public void handleVerifyCard(String payload) {
        // 处理卡片验证消息
        // ...
    }

    public void handleHeartbeat(String payload) {
        // 处理心跳消息
        // ...
    }

    public void handleStatusReport(String payload) {
        // 处理状态报告消息
        // ...
    }
}
