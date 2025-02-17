package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerStartupMessage extends BaseMessage {
    public ServerStartupMessage() {
        this.setType("server_startup");
        this.setTimestamp(System.currentTimeMillis() / 1000);
    }
}