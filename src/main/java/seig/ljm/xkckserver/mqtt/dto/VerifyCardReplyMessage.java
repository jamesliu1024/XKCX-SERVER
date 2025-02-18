package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
        private String visitorName;
        private String message;
        private String action; // open_door或deny_access
        private Long expireTime;
    }
} 