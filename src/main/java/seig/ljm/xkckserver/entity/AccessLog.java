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
@TableName("access_log")
@Schema(name = "AccessLog", description = "")
public class AccessLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    @TableField("visitor_id")
    private Integer visitorId;

    @TableField("device_id")
    private Integer deviceId;

    @TableField("card_id")
    private Integer cardId;

    @TableField("reservation_id")
    private Integer reservationId;

    @TableField("access_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime accessTime;

    @TableField("access_type")
    private String accessType;

    @TableField("result")
    private String result;

    @TableField("reason")
    private String reason;

    @TableField("hidden")
    private Boolean hidden;
}
