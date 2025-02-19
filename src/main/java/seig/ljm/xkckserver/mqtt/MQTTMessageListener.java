package seig.ljm.xkckserver.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import seig.ljm.xkckserver.service.MQTTMessageService;

@Slf4j
@Component
public class MQTTMessageListener {

    @Autowired
    private MQTTMessageService mqttMessageService;

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) {
                try {
                    String topic = message.getHeaders().get("mqtt_topic", String.class);
                    String payload;
                    
                    // 处理不同类型的payload
                    Object rawPayload = message.getPayload();
                    if (rawPayload instanceof byte[]) {
                        payload = new String((byte[]) rawPayload);
                    } else if (rawPayload instanceof String) {
                        payload = (String) rawPayload;
                    } else {
                        log.warn("Unexpected payload type: {}", rawPayload.getClass());
                        payload = rawPayload.toString();
                    }
                    
                    log.debug("Message headers: {}", message.getHeaders());
                    log.info("Received MQTT message - Topic: {}, Payload: {}", topic, payload);
                    
                    mqttMessageService.handleMessage(topic, payload);
                    
                    log.debug("Successfully processed MQTT message");
                } catch (Exception e) {
                    log.error("Error handling MQTT message: {}", e.getMessage(), e);
                }
            }
        };
    }
}
