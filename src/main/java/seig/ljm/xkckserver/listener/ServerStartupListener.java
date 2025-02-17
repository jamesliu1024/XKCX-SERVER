package seig.ljm.xkckserver.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.mqtt.MQTTGateway;
import seig.ljm.xkckserver.mqtt.dto.ServerStartupMessage;
import seig.ljm.xkckserver.service.MessageLogService;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ServerStartupListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private MQTTGateway mqttGateway;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageLogService messageLogService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            ServerStartupMessage startupMessage = new ServerStartupMessage();
            startupMessage.setData(new StartupData(
                event.getApplicationContext().getId(),
                event.getTimestamp()
            ));

            String messageJson = objectMapper.writeValueAsString(startupMessage);
            
            // 发送MQTT消息
            mqttGateway.sendToMqtt("xkck/server/status", messageJson);
            
            // 记录日志
            MessageLog messageLog = new MessageLog();
            messageLog.setDeviceId(-1); // -1 表示服务器
            messageLog.setPayload(messageJson);
            messageLog.setReceiveTime(LocalDateTime.now());
            messageLog.setStatus("success");
            messageLogService.save(messageLog);
            
            // 控制台输出
            log.info("Server started and notified MQTT broker: {}", messageJson);
            
        } catch (Exception e) {
            log.error("Failed to send server startup notification", e);
        }
    }

    @lombok.Data
    private static class StartupData {
        private final String serverId;
        private final long startupTime;
        private final String version = "1.0.0";
    }
}
