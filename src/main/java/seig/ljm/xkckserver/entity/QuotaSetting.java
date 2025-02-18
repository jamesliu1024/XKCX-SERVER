package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("quota_setting")
@Schema(name = "QuotaSetting", description = "")
public class QuotaSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "quota_id", type = IdType.AUTO)
    private Integer quotaId;

    @TableField("date")
    private LocalDate date;

    @TableField("max_quota")
    private Integer maxQuota;

    @TableField("current_count")
    private Integer currentCount;

    @TableField("special_event")
    private String specialEvent;

    @TableField("is_holiday")
    private Boolean isHoliday;

    @TableField("create_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime updateTime;
}
