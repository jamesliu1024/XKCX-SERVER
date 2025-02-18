package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.Prediction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 人流量预测服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface PredictionService extends IService<Prediction> {
    
    /**
     * 添加预测数据
     *
     * @param prediction 预测数据
     * @return 添加的预测数据
     */
    Prediction addPrediction(Prediction prediction);
    
    /**
     * 分页查询预测数据
     *
     * @param current 当前页
     * @param size 每页大小
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param minAccuracy 最小准确度
     * @param minConfidence 最小置信度
     * @return 分页结果
     */
    IPage<Prediction> getPredictionPage(Integer current, Integer size, LocalDate startDate,
                                      LocalDate endDate, Double minAccuracy, Double minConfidence);
    
    /**
     * 获取指定日期的预测数据
     *
     * @param date 预测日期
     * @return 预测数据
     */
    Prediction getDatePrediction(LocalDate date);
    
    /**
     * 获取日期范围内的预测数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预测数据列表
     */
    List<Prediction> getDateRangePredictions(LocalDate startDate, LocalDate endDate);
    
    /**
     * 更新实际人数
     *
     * @param predictionId 预测ID
     * @param actualCount 实际人数
     * @return 更新后的预测数据
     */
    Prediction updateActualCount(Integer predictionId, Integer actualCount);
    
    /**
     * 获取准确度统计数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getAccuracyStats(LocalDate startDate, LocalDate endDate);
}
