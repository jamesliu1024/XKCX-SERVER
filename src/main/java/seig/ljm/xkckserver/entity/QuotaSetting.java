package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;

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
@TableName("QuotaSetting")
//@ApiModel(value = "QuotaSetting对象", description = "")
@Schema(name = "QuotaSetting", description = "")
public class QuotaSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "quotaId", description = "")
    @TableId(value = "quota_id", type = IdType.AUTO)
    private Integer quotaId;

    @Schema(name = "date", description = "")
    @TableField("date")
    private LocalDate date;

    @Schema(name = "maxQuota", description = "")
    @TableField("max_quota")
    private Integer maxQuota;
}
