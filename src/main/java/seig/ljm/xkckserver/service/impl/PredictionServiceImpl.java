package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import seig.ljm.xkckserver.entity.Prediction;
import seig.ljm.xkckserver.mapper.PredictionMapper;
import seig.ljm.xkckserver.service.PredictionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
}
