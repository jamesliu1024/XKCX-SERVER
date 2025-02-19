package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;

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
@TableName("prediction")
@Schema(name = "Prediction", description = "")
public class Prediction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "prediction_id", type = IdType.AUTO)
    private Integer predictionId;

    @TableField("predict_date")
    private LocalDate predictDate;

    @TableField("predicted_count")
    private Integer predictedCount;

    @TableField("accuracy")
    private BigDecimal accuracy;

    @TableField("actual_count")
    private Integer actualCount;

    @TableField("confidence")
    private BigDecimal confidence;

    @TableField("factors")
    @Schema(description = "影响因素（JSON格式）")
    private String factorsStr;

    @TableField(exist = false)
    private JSONObject factors;

    @TableField("model_version")
    private String modelVersion;

    @TableField("generate_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime generateTime;

    @TableField("update_time")
    @JsonFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN, timezone = TimeZoneConstant.ZONE_NAME)
    private ZonedDateTime updateTime;

    public JSONObject getFactors() {
        if (this.factorsStr != null) {
            return JSON.parseObject(this.factorsStr);
        }
        return null;
    }

    public void setFactors(JSONObject factors) {
        this.factors = factors;
        if (factors != null) {
            this.factorsStr = factors.toJSONString();
        } else {
            this.factorsStr = null;
        }
    }
}
