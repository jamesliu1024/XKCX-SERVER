package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.Prediction;

//import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * 人流量预测Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface PredictionMapper extends BaseMapper<Prediction> {

    /**
     * 获取指定日期范围内的预测数据
     */
    @Select("SELECT * FROM prediction " +
            "WHERE predict_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY predict_date ASC")
    List<Prediction> getDateRangePredictions(@Param("startDate") ZonedDateTime startDate,
                                           @Param("endDate") ZonedDateTime endDate);

    /**
     * 获取指定日期的预测数据
     */
    @Select("SELECT * FROM prediction WHERE predict_date = #{date}")
    Prediction getDatePrediction(@Param("date") ZonedDateTime date);

    /**
     * 更新实际人数和准确度
     */
    @Update("UPDATE prediction SET " +
            "actual_count = #{actualCount}, " +
            "accuracy = #{accuracy}, " +
            "update_time = NOW() " +
            "WHERE prediction_id = #{predictionId}")
    int updateActualCount(@Param("predictionId") Integer predictionId,
                         @Param("actualCount") Integer actualCount,
                         @Param("accuracy") java.math.BigDecimal accuracy);

    /**
     * 获取准确度统计数据
     */
    @Select("SELECT " +
            "COUNT(*) as total_count, " +
            "AVG(accuracy) as avg_accuracy, " +
            "AVG(confidence) as avg_confidence, " +
            "COUNT(actual_count) as actual_data_count " +
            "FROM prediction " +
            "WHERE predict_date BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> getAccuracyStats(@Param("startDate") ZonedDateTime startDate,
                                        @Param("endDate") ZonedDateTime endDate);

    /**
     * 删除指定日期之前的预测数据
     */
    @Delete("DELETE FROM prediction WHERE predict_date < #{date}")
    int deleteOldPredictions(@Param("date") ZonedDateTime date);
}

