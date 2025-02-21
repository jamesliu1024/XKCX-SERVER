package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        @JsonProperty("server_time")
        private Long serverTime;
    }
} 