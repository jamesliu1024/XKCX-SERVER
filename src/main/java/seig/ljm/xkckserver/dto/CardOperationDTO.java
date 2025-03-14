package seig.ljm.xkckserver.dto;

import lombok.Data;

@Data
public class CardOperationDTO {
    private Integer reservationId;  // 预约ID
    private Integer adminId;        // 管理员ID
    private String operationType;   // 操作类型：issue/return/add
    private String uid;            // 卡片UID（从设备返回后填充）
    private Integer cardId;        // 卡片ID
    private String deviceId;       // 设备ID
    private String remarks;        // 备注信息
} 