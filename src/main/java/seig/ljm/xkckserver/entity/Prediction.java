package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("Prediction")
@Schema(name = "Prediction", description = "预测数据")
public class Prediction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预测记录ID")
    @TableId(value = "prediction_id", type = IdType.AUTO)
    private Integer predictionId;

    @Schema(description = "预测日期")
    @TableField("predict_date")
    private LocalDate predictDate;

    @Schema(description = "预测访客数量")
    @TableField("predicted_count")
    private Integer predictedCount;

    @Schema(description = "预测准确度（百分比）")
    @TableField("accuracy")
    private Double accuracy;

    @Schema(description = "预测置信度（百分比）")
    @TableField("confidence")
    private Double confidence;

    @Schema(description = "预测模型版本")
    @TableField("model_version")
    private String modelVersion;

    @Schema(description = "预测生成时间")
    @TableField("generate_time")
    private LocalDateTime generateTime;
}
