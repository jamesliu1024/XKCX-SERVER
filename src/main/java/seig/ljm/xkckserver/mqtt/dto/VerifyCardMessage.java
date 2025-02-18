package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VerifyCardMessage extends BaseMessage {
    private VerifyCardData data;

    @Data
    public static class VerifyCardData {
        private String uid;
        private String action; // entry或exit
    }
} 