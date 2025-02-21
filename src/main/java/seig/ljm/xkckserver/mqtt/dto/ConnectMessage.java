package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectMessage extends BaseMessage {
    private ConnectData data;

    @Data
    public static class ConnectData {
        @JsonProperty("firmware_version")
        private String firmwareVersion;
        private String ip;
    }
} 