package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("AccessLog")
//@ApiModel(value = "AccessLog对象", description = "")
@Schema(name = "AccessLog", description = "")
public class AccessLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "logId", description = "")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    @Schema(name = "visitorId", description = "")
    @TableField("visitor_id")
    private Integer visitorId;

    @Schema(name = "deviceId", description = "")
    @TableField("device_id")
    private Integer deviceId;

    @Schema(name = "accessTime", description = "")
    @TableField("access_time")
    private LocalDateTime accessTime;

    @Schema(name = "accessType", description = "")
    @TableField("access_type")
    private String accessType;

    @Schema(name = "result", description = "")
    @TableField("result")
    private String result;

    @Schema(name = "reason", description = "")
    @TableField("reason")
    private String reason;
}
