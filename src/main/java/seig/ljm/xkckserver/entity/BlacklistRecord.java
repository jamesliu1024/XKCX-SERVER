package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
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
@TableName("blacklist_record")
@Schema(name = "BlacklistRecord", description = "")
public class BlacklistRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Integer recordId;

    @TableField("visitor_id")
    @Schema(description = "访客ID")
    private Integer visitorId;

    @TableField("reason")
    @Schema(description = "加入黑名单原因")
    private String reason;

    @TableField("start_time")
    @Schema(description = "生效开始时间")
    private Instant startTime;

    @TableField("end_time")
    @Schema(description = "生效结束时间，为null表示永久")
    private Instant endTime;

    @TableField("operator_id")
    @Schema(description = "操作人ID")
    private Integer operatorId;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private Instant createTime;
}
