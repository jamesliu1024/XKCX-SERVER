package seig.ljm.xkckserver.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseMessage {
    private String type;
    @JsonProperty("device_id")
    private String deviceId;
    private Long timestamp;
    private Object data;

    public BaseMessage() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }
}
