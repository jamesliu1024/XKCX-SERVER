package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
@EqualsAndHashCode(callSuper = true)
public class HeartbeatMessage extends BaseMessage {
    private HeartbeatData data;

    @Data
    public static class HeartbeatData {
        private String status;
        private Long uptime;
        @JsonProperty("memory_usage")
        private Integer memoryUsage;
    }
} 