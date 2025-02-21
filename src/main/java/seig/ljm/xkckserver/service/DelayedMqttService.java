package seig.ljm.xkckserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.mqtt.MQTTGateway;

@Slf4j
@Service
public class DelayedMqttService {
    
    private final MQTTGateway mqttGateway;
    
    @Autowired
    public DelayedMqttService(MQTTGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }
    
    @Async("mqttExecutor")
    public void sendDelayedMessage(String topic, String payload, long delayMillis) {
        try {
            Thread.sleep(delayMillis);
            mqttGateway.sendToMqtt(topic, payload);
            log.debug("Sent delayed MQTT message to topic: {}", topic);
        } catch (InterruptedException e) {
            log.error("Error while sending delayed MQTT message: ", e);
            Thread.currentThread().interrupt();
        }
    }
} 