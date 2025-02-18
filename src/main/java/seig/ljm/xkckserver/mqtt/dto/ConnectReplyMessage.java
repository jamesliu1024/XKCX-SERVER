package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
        private String deviceId;
        private String location;
        private String deviceType;
        private String description;
    }
} 