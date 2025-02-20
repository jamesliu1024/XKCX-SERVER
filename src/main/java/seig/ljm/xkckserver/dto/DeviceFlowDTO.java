package seig.ljm.xkckserver.dto;

import lombok.Data;

@Data
public class DeviceFlowDTO {
    private String location;    // 设备位置
    private Integer count;      // 当前人数
} 