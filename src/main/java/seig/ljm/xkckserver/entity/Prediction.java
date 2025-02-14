package seig.ljm.xkckserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
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
//@ApiModel(value = "Prediction对象", description = "")
@Schema(name = "Prediction", description = "")
public class Prediction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "predictionId", description = "")
    @TableId(value = "prediction_id", type = IdType.AUTO)
    private Integer predictionId;

    @Schema(name = "predictDate", description = "")
    @TableField("predict_date")
    private LocalDate predictDate;

    @Schema(name = "predictedCount", description = "")
    @TableField("predicted_count")
    private Integer predictedCount;

    @Schema(name = "actualCount", description = "")
    @TableField("generate_time")
    private LocalDateTime generateTime;
}
