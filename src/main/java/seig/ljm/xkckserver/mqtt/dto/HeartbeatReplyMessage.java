package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeartbeatReplyMessage extends BaseMessage {
    private String status;
    private HeartbeatReplyData data;

    public HeartbeatReplyMessage() {
        this.setType("heartbeat_reply");
        this.setTimestamp(System.currentTimeMillis() / 1000);
    }

    @Data
    public static class HeartbeatReplyData {
        private Long serverTime;
    }
} 