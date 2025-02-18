package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
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
@TableName("operation_log")
@Schema(name = "OperationLog", description = "")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Integer logId;

    @TableField("operator_id")
    @Schema(description = "操作员ID")
    private Integer operatorId;

    @TableField("operation_type")
    @Schema(description = "操作类型")
    private String operationType;

    @TableField("target_id")
    @Schema(description = "操作对象ID")
    private Integer targetId;

    @TableField("operation_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    @Schema(description = "操作时间")
    private ZonedDateTime operationTime;

    @TableField(value = "details", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    @Schema(description = "操作详情（JSON格式）")
    private Object details;
}
