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
@TableName("RFIDCard")
//@ApiModel(value = "RFIDCard对象", description = "")
@Schema(name = "RFIDCard", description = "RFID门禁卡")
public class RFIDCard implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_ISSUED = "issued";
    public static final String STATUS_LOST = "lost";
    public static final String STATUS_DEACTIVATED = "deactivated";

    @Schema(name = "cardId", description = "")
    @TableId(value = "card_id", type = IdType.AUTO)
    private Integer cardId;

    @Schema(name = "uid", description = "")
    @TableField("uid")
    private String uid;

    @Schema(name = "issueTime", description = "")
    @TableField("issue_time")
    private LocalDateTime issueTime;

    @Schema(name = "returnTime", description = "")
    @TableField("return_time")
    private LocalDateTime returnTime;

    @Schema(name = "status", description = "卡片状态:available-可用,issued-已发放,lost-丢失,deactivated-已停用")
    @TableField("status")
    private String status;

    @Schema(name = "expireTime", description = "卡片失效时间（一般与预约结束时间一致）")
    @TableField("expiration_time")
    private LocalDateTime expirationTime;

    @Schema(name = "reservationId", description = "")
    @TableField("reservation_id")
    private Integer reservationId;

    @Schema(name = "lastAdminId", description = "")
    @TableField("last_admin_id")
    private Integer lastAdminId;

    @Schema(name = "remarks", description = "")
    @TableField("remarks")
    private String remarks;

    @Schema(name = "updateTime", description = "最后更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
