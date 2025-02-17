package seig.ljm.xkckserver.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MQTTGateway {
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String data);
}
