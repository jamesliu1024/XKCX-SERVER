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
@TableName("visitor")
@Schema(name = "Visitor", description = "")
public class Visitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "visitor_id", type = IdType.AUTO)
    private Integer visitorId;

    @TableField("name")
    private String name;

    @TableField("phone")
    private String phone;

    @TableField("wechat_openid")
    private String wechatOpenid;

    @TableField("id_type")
    private String idType;

    @TableField("id_number")
    private String idNumber;

    @TableField("role")
    private String role;

    @TableField("account_status")
    private String accountStatus;

    @TableField("hidden")
    private Boolean hidden;

    @TableField("password_hash")
    private String passwordHash;

    @TableField("last_login_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime lastLoginTime;

    @TableField("create_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime updateTime;
}
