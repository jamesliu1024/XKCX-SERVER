package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.QuotaSetting;

import java.time.LocalDate;
import java.util.List;

/**
 * 配额设置服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface QuotaSettingService extends IService<QuotaSetting> {
    
    /**
     * 添加配额设置
     *
     * @param quotaSetting 配额设置
     * @return 添加的配额设置
     */
    QuotaSetting addQuotaSetting(QuotaSetting quotaSetting);
    
    /**
     * 更新配额设置
     *
     * @param quotaSetting 配额设置
     * @return 更新后的配额设置
     */
    QuotaSetting updateQuotaSetting(QuotaSetting quotaSetting);
    
    /**
     * 分页查询配额设置
     *
     * @param current 当前页
     * @param size 每页大小
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param isHoliday 是否节假日
     * @return 分页结果
     */
    IPage<QuotaSetting> getQuotaPage(Integer current, Integer size, LocalDate startDate,
                                    LocalDate endDate, Boolean isHoliday);
    
    /**
     * 获取指定日期的配额设置
     *
     * @param date 日期
     * @return 配额设置
     */
    QuotaSetting getDateQuota(LocalDate date);
    
    /**
     * 获取日期范围内的配额设置
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 配额设置列表
     */
    List<QuotaSetting> getDateRangeQuotas(LocalDate startDate, LocalDate endDate);
    
    /**
     * 增加当前预约数
     *
     * @param quotaId 配额ID
     * @return 操作结果
     */
    Boolean incrementCurrentCount(Integer quotaId);
    
    /**
     * 减少当前预约数
     *
     * @param quotaId 配额ID
     * @return 操作结果
     */
    Boolean decrementCurrentCount(Integer quotaId);
    
    /**
     * 检查指定日期是否还有可用配额
     *
     * @param date 日期
     * @return 是否有可用配额
     */
    Boolean checkQuotaAvailable(LocalDate date);
    
    /**
     * 批量设置配额
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param maxQuota 最大配额
     * @param isHoliday 是否节假日
     * @param specialEvent 特殊事件说明
     * @return 操作结果
     */
    Boolean batchSetQuota(LocalDate startDate, LocalDate endDate, Integer maxQuota,
                         Boolean isHoliday, String specialEvent);
}
