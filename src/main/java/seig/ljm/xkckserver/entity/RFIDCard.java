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
@Schema(name = "RFIDCard", description = "")
public class RFIDCard implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Schema(name = "status", description = "")
    @TableField("status")
    private String status;

    @Schema(name = "expirationTime", description = "")
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
}
