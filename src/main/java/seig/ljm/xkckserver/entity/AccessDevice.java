package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
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
//@ApiModel(value = "AccessDevice对象", description = "")
@Schema(name = "AccessDevice", description = "")
public class AccessDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "deviceId", description = "")
    @TableId(value = "device_id", type = IdType.AUTO)
    private Integer deviceId;

    @Schema(name = "deviceName", description = "")
    @TableField("location")
    private String location;

    @Schema(name = "ipAddress", description = "")
    @TableField("ip_address")
    private String ipAddress;

    @Schema(name = "status", description = "")
    @TableField("status")
    private String status;

    @Schema(name = "deviceType", description = "")
    @TableField("device_type")
    private String deviceType;

    @Schema(name = "description", description = "")
    @TableField("description")
    private String description;

}
