package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;

@Data
public class BaseMessage {
    private String type;
    private String deviceId;
    private Long timestamp;
    private Object data;
}
