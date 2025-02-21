package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@EqualsAndHashCode(callSuper = true)
public class VerifyCardReplyMessage extends BaseMessage {
    private String status;
    private VerifyCardReplyData data;

    public VerifyCardReplyMessage() {
        this.setType("verify_card_reply");
        this.setTimestamp(System.currentTimeMillis() / 1000);
    }

    @Data
    public static class VerifyCardReplyData {
        private boolean allow;
        @JsonProperty("visitor_name")
        private String visitorName;
        private String message;
        private String action; // open_door或deny_access
        @JsonProperty("expire_time")
        private Long expireTime;
    }
} 