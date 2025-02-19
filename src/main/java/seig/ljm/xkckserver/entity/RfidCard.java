package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@TableName("rfid_card")
@Schema(name = "RfidCard", description = "")
public class RfidCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "card_id", type = IdType.AUTO)
    private Integer cardId;

    @TableField("uid")
    private String uid;

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
}
