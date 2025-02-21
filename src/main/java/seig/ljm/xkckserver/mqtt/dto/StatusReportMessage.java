package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@EqualsAndHashCode(callSuper = true)
public class StatusReportMessage extends BaseMessage {
    private StatusReportData data;

    @Data
    public static class StatusReportData {
        private String status;
        @JsonProperty("door_status")
        private String doorStatus; // open或closed
        @JsonProperty("last_card_read")
        private String lastCardRead;
        @JsonProperty("error_code")
        private Integer errorCode;
    }
} 