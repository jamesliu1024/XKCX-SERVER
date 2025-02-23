package seig.ljm.xkckserver.mqtt.dto;

import lombok.Data;

@Data
public class CardOperationDTO {
    private Integer reservationId;  // 预约ID
    private Integer adminId;        // 管理员ID
    private String operationType;   // 操作类型：issue/return
    private String uid;            // 卡片UID（从设备返回后填充）
    private Integer cardId;        // 卡片ID
    private String deviceId;       // 设备ID
} 