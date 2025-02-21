package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectReplyMessage extends BaseMessage {
    private String status;
    private ConnectReplyData data;

    public ConnectReplyMessage() {
        this.setType("connect_reply");
        this.setTimestamp(System.currentTimeMillis() / 1000);
    }

    @Data
    public static class ConnectReplyData {
        @JsonProperty("device_id")
        private String deviceId;
        private String location;
        @JsonProperty("device_type")
        private String deviceType;
        private String description;
    }
} 