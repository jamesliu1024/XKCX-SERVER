package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectMessage extends BaseMessage {
    private ConnectData data;

    @Data
    public static class ConnectData {
        private String firmwareVersion;
        private String ip;
    }
} 