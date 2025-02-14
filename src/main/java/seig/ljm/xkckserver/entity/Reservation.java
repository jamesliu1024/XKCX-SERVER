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
@TableName("Reservation")
//@ApiModel(value = "Reservation对象", description = "")
@Schema(name = "Reservation", description = "")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "reservationId", description = "")
    @TableId(value = "reservation_id", type = IdType.AUTO)
    private Integer reservationId;

    @Schema(name = "visitorId", description = "")
    @TableField("visitor_id")
    private Integer visitorId;

    @Schema(name = "deviceId", description = "")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(name = "endTime", description = "")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(name = "hostConfirm", description = "")
    @TableField("host_confirm")
    private String hostConfirm;

    @Schema(name = "hostId", description = "")
    @TableField("status")
    private String status;

    @Schema(name = "createTime", description = "")
    @TableField("create_time")
    private LocalDateTime createTime;
}
