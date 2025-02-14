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
@TableName("MessageLog")
//@ApiModel(value = "MessageLog对象", description = "")
@Schema(name = "MessageLog", description = "")
public class MessageLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "messageId", description = "")
    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;

    @Schema(name = "deviceId", description = "")
    @TableField("device_id")
    private Integer deviceId;

    @Schema(name = "payload", description = "")
    @TableField("payload")
    private String payload;

    @Schema(name = "sendTime", description = "")
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @Schema(name = "status", description = "")
    @TableField("status")
    private String status;
}
