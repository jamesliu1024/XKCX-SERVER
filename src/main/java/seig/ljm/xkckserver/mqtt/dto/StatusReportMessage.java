package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StatusReportMessage extends BaseMessage {
    private StatusReportData data;

    @Data
    public static class StatusReportData {
        private String status;
        private String doorStatus; // open或closed
        private String lastCardRead;
        private Integer errorCode;
    }
} 