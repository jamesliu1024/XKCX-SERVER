package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import seig.ljm.xkckserver.entity.Prediction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 预测服务接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface PredictionService extends IService<Prediction> {
    
    /**
     * 根据日期获取预测数据
     *
     * @param date 预测日期
     * @return 预测数据
     */
    Prediction getPredictionByDate(LocalDate date);

    /**
     * 获取指定日期范围内的预测数据列表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预测数据列表
     */
    List<Prediction> getPredictionsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询预测数据
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页预测数据
     */
    Page<Prediction> getPredictionPage(Integer pageNum, Integer pageSize, LocalDate startDate, LocalDate endDate);
}
