package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.Prediction;
import seig.ljm.xkckserver.mapper.PredictionMapper;
import seig.ljm.xkckserver.service.PredictionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人流量预测服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
public class PredictionServiceImpl extends ServiceImpl<PredictionMapper, Prediction> implements PredictionService {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @Override
    public Prediction addPrediction(Prediction prediction) {
        // 设置生成时间
        ZonedDateTime now = ZonedDateTime.now(ZONE_ID);
        prediction.setGenerateTime(now);
        prediction.setUpdateTime(now);
        
        // 保存预测数据
        save(prediction);
        return prediction;
    }

    @Override
    public IPage<Prediction> getPredictionPage(Integer current, Integer size, LocalDate startDate,
                                             LocalDate endDate, Double minAccuracy, Double minConfidence) {
        LambdaQueryWrapper<Prediction> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (startDate != null) {
            wrapper.ge(Prediction::getPredictDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Prediction::getPredictDate, endDate);
        }
        if (minAccuracy != null) {
            wrapper.ge(Prediction::getAccuracy, BigDecimal.valueOf(minAccuracy));
        }
        if (minConfidence != null) {
            wrapper.ge(Prediction::getConfidence, BigDecimal.valueOf(minConfidence));
        }
        
        // 按预测日期倒序排序
        wrapper.orderByDesc(Prediction::getPredictDate);
        
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public Prediction getDatePrediction(LocalDate date) {
        LambdaQueryWrapper<Prediction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prediction::getPredictDate, date);
        return getOne(wrapper);
    }

    @Override
    public List<Prediction> getDateRangePredictions(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Prediction> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Prediction::getPredictDate, startDate, endDate)
               .orderByAsc(Prediction::getPredictDate);
        return list(wrapper);
    }

    @Override
    public Prediction updateActualCount(Integer predictionId, Integer actualCount) {
        Prediction prediction = getById(predictionId);
        if (prediction == null) {
            throw new RuntimeException("预测记录不存在");
        }
        
        // 更新实际人数
        prediction.setActualCount(actualCount);
        
        // 计算准确度
        if (prediction.getPredictedCount() != null && actualCount != null) {
            BigDecimal accuracy = calculateAccuracy(prediction.getPredictedCount(), actualCount);
            prediction.setAccuracy(accuracy);
        }
        
        // 更新记录
        updateById(prediction);
        return prediction;
    }

    @Override
    public Map<String, Object> getAccuracyStats(LocalDate startDate, LocalDate endDate) {
        List<Prediction> predictions = getDateRangePredictions(startDate, endDate);
        
        Map<String, Object> stats = new HashMap<>();
        if (predictions.isEmpty()) {
            return stats;
        }
        
        // 计算平均准确度
        BigDecimal avgAccuracy = predictions.stream()
                .filter(p -> p.getAccuracy() != null)
                .map(Prediction::getAccuracy)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(predictions.size()), 2, RoundingMode.HALF_UP);
        
        // 计算平均置信度
        BigDecimal avgConfidence = predictions.stream()
                .filter(p -> p.getConfidence() != null)
                .map(Prediction::getConfidence)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(predictions.size()), 2, RoundingMode.HALF_UP);
        
        // 统计有实际数据的预测数量
        long actualDataCount = predictions.stream()
                .filter(p -> p.getActualCount() != null)
                .count();
        
        stats.put("totalPredictions", predictions.size());
        stats.put("averageAccuracy", avgAccuracy);
        stats.put("averageConfidence", avgConfidence);
        stats.put("actualDataCount", actualDataCount);
        
        return stats;
    }
    
    /**
     * 计算预测准确度
     *
     * @param predicted 预测人数
     * @param actual 实际人数
     * @return 准确度（百分比）
     */
    private BigDecimal calculateAccuracy(int predicted, int actual) {
        if (actual == 0) {
            return predicted == 0 ? BigDecimal.valueOf(100.0) : BigDecimal.ZERO;
        }
        
        BigDecimal predictedValue = BigDecimal.valueOf(predicted);
        BigDecimal actualValue = BigDecimal.valueOf(actual);
        BigDecimal difference = predictedValue.subtract(actualValue).abs();
        
        return BigDecimal.valueOf(100.0)
                .multiply(BigDecimal.ONE.subtract(difference.divide(actualValue, 4, RoundingMode.HALF_UP)))
                .setScale(2, RoundingMode.HALF_UP)
                .max(BigDecimal.ZERO)
                .min(BigDecimal.valueOf(100.0));
    }
}
