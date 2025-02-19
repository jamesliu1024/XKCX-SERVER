package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import seig.ljm.xkckserver.common.constant.EnumConstant;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author ljm
 * @since 2025-02-18
 */
@Getter
@Setter
@ToString
@TableName("access_device")
@Schema(name = "AccessDevice", description = "")
public class AccessDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_id", type = IdType.AUTO)
    private Integer deviceId;

    @TableField("location")
    private String location;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("mac_address")
    private String macAddress;

    @TableField("status")
    private String status;

    @TableField("device_type")
    private String deviceType;

    @TableField("last_heartbeat_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime lastHeartbeatTime;

    @TableField("firmware_version")
    private String firmwareVersion;

    @TableField("door_status")
    private String doorStatus;

    @TableField("description")
    private String description;

    // 枚举值转换方法
    public void setStatus(String status) {
        this.status = EnumConstant.AccessDevice.STATUS.getOrDefault(status, status);
    }

    public String getStatus() {
        return EnumConstant.AccessDevice.STATUS_TEXT.getOrDefault(this.status, this.status);
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = EnumConstant.AccessDevice.DEVICE_TYPE.getOrDefault(deviceType, deviceType);
    }

    public String getDeviceType() {
        return EnumConstant.AccessDevice.DEVICE_TYPE_TEXT.getOrDefault(this.deviceType, this.deviceType);
    }

    public void setDoorStatus(String doorStatus) {
        this.doorStatus = EnumConstant.AccessDevice.DOOR_STATUS.getOrDefault(doorStatus, doorStatus);
    }

    public String getDoorStatus() {
        return EnumConstant.AccessDevice.DOOR_STATUS_TEXT.getOrDefault(this.doorStatus, this.doorStatus);
    }
    
}
