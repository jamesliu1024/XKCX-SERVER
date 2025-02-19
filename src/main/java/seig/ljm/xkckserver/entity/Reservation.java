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
@TableName("reservation")
@Schema(name = "Reservation", description = "")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "reservation_id", type = IdType.AUTO)
    private Integer reservationId;

    @TableField("visitor_id")
    private Integer visitorId;

    @TableField("reason")
    private String reason;

    @TableField("host_department")
    private String hostDepartment;

    @TableField("host_name")
    private String hostName;

    @TableField("start_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime startTime;

    @TableField("end_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime endTime;

    @TableField("host_confirm")
    private String hostConfirm;

    @TableField("status")
    private String status;

    @TableField("create_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime updateTime;

    @TableField("remarks")
    private String remarks;

    @TableField("hidden")
    private Boolean hidden;

    // 枚举值转换方法
    public void setHostConfirm(String hostConfirm) {
        this.hostConfirm = EnumConstant.Reservation.HOST_CONFIRM.getOrDefault(hostConfirm, hostConfirm);
    }

    public String getHostConfirm() {
        return EnumConstant.Reservation.HOST_CONFIRM_TEXT.getOrDefault(this.hostConfirm, this.hostConfirm);
    }

    public void setStatus(String status) {
        this.status = EnumConstant.Reservation.STATUS.getOrDefault(status, status);
    }

    public String getStatus() {
        return EnumConstant.Reservation.STATUS_TEXT.getOrDefault(this.status, this.status);
    }
}
