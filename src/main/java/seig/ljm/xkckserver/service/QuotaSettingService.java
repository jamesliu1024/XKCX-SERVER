package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.QuotaSetting;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface QuotaSettingService extends IService<QuotaSetting> {
    
    /**
     * 获取指定日期的配额设置
     */
    QuotaSetting getQuotaByDate(LocalDate date);
    
    /**
     * 检查指定日期是否还有剩余配额
     */
    boolean isQuotaAvailable(LocalDate date);
    
    /**
     * 获取日期范围内的配额设置列表
     */
    List<QuotaSetting> getQuotasByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 分页获取配额设置
     */
    Page<QuotaSetting> getQuotaPage(Integer pageNum, Integer pageSize, LocalDate startDate, LocalDate endDate);
}
