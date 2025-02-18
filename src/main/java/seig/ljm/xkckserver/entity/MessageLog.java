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
@TableName("message_log")
@Schema(name = "MessageLog", description = "")
public class MessageLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "message_id", type = IdType.AUTO)
    private Integer messageId;

    @TableField("device_id")
    private Integer deviceId;

    @TableField("payload")
    private String payload;

    @TableField("receive_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime receiveTime;

    @TableField("status")
    private String status;
}
