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
@TableName("Visitor")
//@ApiModel(value = "Visitor对象", description = "")
@Schema(name = "Visitor", description = "")
public class Visitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "visitorId", description = "")
    @TableId(value = "visitor_id", type = IdType.AUTO)
    private Integer visitorId;

    @Schema(name = "name", description = "")
    @TableField("name")
    private String name;

    @Schema(name = "phone", description = "")
    @TableField("phone")
    private String phone;

    @Schema(name = "wechatOpenid", description = "")
    @TableField("wechat_openid")
    private String wechatOpenid;

    @Schema(name = "idType", description = "")
    @TableField("id_type")
    private String idType;

    @Schema(name = "idNumber", description = "")
    @TableField("id_number")
    private String idNumber;

    @Schema(name = "reason", description = "")
    @TableField("reason")
    private String reason;

    @Schema(name = "hostDepartment", description = "")
    @TableField("host_department")
    private String hostDepartment;

    @Schema(name = "hostName", description = "")
    @TableField("host_name")
    private String hostName;

    @Schema(name = "hostPhone", description = "")
    @TableField("status")
    private String status;

    @Schema(name = "expireTime", description = "")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    @Schema(name = "createTime", description = "")
    @TableField("create_time")
    private LocalDateTime createTime;
}
