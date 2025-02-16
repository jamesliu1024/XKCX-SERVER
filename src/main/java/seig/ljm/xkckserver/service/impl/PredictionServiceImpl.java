package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import seig.ljm.xkckserver.entity.Prediction;
import seig.ljm.xkckserver.mapper.PredictionMapper;
import seig.ljm.xkckserver.service.PredictionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 预测服务实现类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Service
public class PredictionServiceImpl extends ServiceImpl<PredictionMapper, Prediction> implements PredictionService {
    private PredictionMapper predictionMapper;
    @Autowired
    public PredictionServiceImpl(PredictionMapper predictionMapper) {
        this.predictionMapper = predictionMapper;
    }

    @Override
    public Prediction getPredictionByDate(LocalDate date) {
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("predict_date", date);
        return getOne(queryWrapper);
    }

    @Override
    public List<Prediction> getPredictionsByDateRange(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("predict_date", startDate, endDate)
                   .orderByAsc("predict_date");
        return list(queryWrapper);
    }

    @Override
    public Page<Prediction> getPredictionPage(Integer pageNum, Integer pageSize, LocalDate startDate, LocalDate endDate) {
        Page<Prediction> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        
        if (startDate != null && endDate != null) {
            queryWrapper.between("predict_date", startDate, endDate);
        }
        queryWrapper.orderByDesc("predict_date");
        
        return page(page, queryWrapper);
    }

    @Override
    public Double getAverageAccuracy(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("predict_date", startDate, endDate)
                   .isNotNull("accuracy");
        List<Prediction> predictions = list(queryWrapper);
        
        if (predictions.isEmpty()) {
            return 0.0;
        }
        
        double sum = predictions.stream()
                .mapToDouble(Prediction::getAccuracy)
                .sum();
        return sum / predictions.size();
    }

    @Override
    public List<Prediction> getPredictionsByModelVersion(String modelVersion) {
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model_version", modelVersion)
                   .orderByDesc("predict_date");
        return list(queryWrapper);
    }

    @Override
    public void updatePredictionAccuracy(Integer predictionId, Double accuracy, Double confidence) {
        Prediction prediction = getById(predictionId);
        if (prediction != null) {
            prediction.setAccuracy(accuracy);
            prediction.setConfidence(confidence);
            updateById(prediction);
        }
    }
}
