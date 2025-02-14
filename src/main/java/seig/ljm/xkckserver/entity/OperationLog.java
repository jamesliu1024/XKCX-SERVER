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
@TableName("OperationLog")
//@ApiModel(value = "OperationLog对象", description = "")
@Schema(name = "OperationLog", description = "")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "logId", description = "")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    @Schema(name = "operatorId", description = "")
    @TableField("operator_id")
    private Integer operatorId;

    @Schema(name = "operationType", description = "")
    @TableField("operation_type")
    private String operationType;

    @Schema(name = "targetId", description = "")
    @TableField("target_id")
    private Integer targetId;

    @Schema(name = "operationTime", description = "")
    @TableField("operation_time")
    private LocalDateTime operationTime;

    @Schema(name = "details", description = "")
    @TableField("details")
    private String details;
}
