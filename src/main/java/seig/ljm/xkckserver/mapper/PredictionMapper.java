package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.Prediction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 预测数据 Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface PredictionMapper extends BaseMapper<Prediction> {
    
    /**
     * 获取指定日期的预测数据
     */
    @Select("SELECT * FROM Prediction WHERE predict_date = #{date}")
    Prediction selectByDate(@Param("date") LocalDate date);

    /**
     * 获取日期范围内的平均预测人数
     */
    @Select("SELECT AVG(predicted_count) FROM Prediction " +
            "WHERE predict_date BETWEEN #{startDate} AND #{endDate}")
    Integer selectAvgPredictedCount(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);

    /**
     * 获取指定日期范围内的所有预测数据
     */
    @Select("SELECT * FROM Prediction " +
            "WHERE predict_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY predict_date")
    List<Prediction> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);

    /**
     * 更新预测准确度
     */
    @Update("UPDATE Prediction SET accuracy = #{accuracy} " +
            "WHERE predict_date = #{date}")
    int updateAccuracy(@Param("date") LocalDate date, 
                      @Param("accuracy") Double accuracy);

    /**
     * 插入新的预测数据
     */
    @Insert("INSERT INTO Prediction (predict_date, predicted_count, confidence, model_version) " +
            "VALUES (#{predictDate}, #{predictedCount}, #{confidence}, #{modelVersion})")
    int insertPrediction(Prediction prediction);
}

