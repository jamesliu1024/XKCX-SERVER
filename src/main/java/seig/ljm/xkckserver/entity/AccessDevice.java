package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Getter
@Setter
@ToString
@TableName("AccessDevice")
@Schema(name = "AccessDevice", description = "门禁设备")
public class AccessDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    @TableId(value = "device_id", type = IdType.AUTO)
    private Integer deviceId;

    @Schema(description = "设备位置")
    @TableField("location")
    private String location;

    @Schema(description = "设备IP地址")
    @TableField("ip_address")
    private String ipAddress;

    @Schema(description = "设备状态：online-在线,offline-离线,maintenance-维护中")
    @TableField("status")
    private String status;

    @Schema(description = "设备类型：campus_gate-校园大门,facility_gate-设施门禁,management-管理设备")
    @TableField("device_type")
    private String deviceType;

    @Schema(description = "设备描述")
    @TableField("description")
    private String description;

}
