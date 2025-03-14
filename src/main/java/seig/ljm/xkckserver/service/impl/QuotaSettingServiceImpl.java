package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.QuotaSetting;
import seig.ljm.xkckserver.mapper.QuotaSettingMapper;
import seig.ljm.xkckserver.service.QuotaSettingService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 配额设置服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaSettingServiceImpl extends ServiceImpl<QuotaSettingMapper, QuotaSetting> implements QuotaSettingService {

    private final QuotaSettingMapper quotaSettingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaSetting setQuota(QuotaSetting quotaSetting) {
        // 检查是否已存在该日期的配额设置
        LambdaQueryWrapper<QuotaSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuotaSetting::getDate, quotaSetting.getDate());
        
        QuotaSetting existingQuota = getOne(wrapper);
        if (existingQuota != null) {
            // 更新现有配额设置
            existingQuota.setMaxQuota(quotaSetting.getMaxQuota());
            existingQuota.setIsHoliday(quotaSetting.getIsHoliday());
            existingQuota.setSpecialEvent(quotaSetting.getSpecialEvent());
            updateById(existingQuota);
            
            log.info("Updated quota setting for date {}", quotaSetting.getDate());
            return existingQuota;
        } else {
            // 创建新的配额设置
            save(quotaSetting);
            
            log.info("Created new quota setting for date {}", quotaSetting.getDate());
            return quotaSetting;
        }
    }

    @Override
    public List<QuotaSetting> listQuotas(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<QuotaSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(QuotaSetting::getDate, startDate)
               .le(QuotaSetting::getDate, endDate)
               .orderByAsc(QuotaSetting::getDate);
        
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaSetting addQuotaSetting(QuotaSetting quotaSetting) {
        // 检查日期是否已存在配额设置
        QuotaSetting existingSetting = getDateQuota(quotaSetting.getDate());
        if (existingSetting != null) {
            throw new RuntimeException("该日期已存在配额设置");
        }

        // 设置初始值
        quotaSetting.setCurrentCount(0);
        quotaSetting.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        
        // 保存配额设置
        save(quotaSetting);
        return quotaSetting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuotaSetting updateQuotaSetting(QuotaSetting quotaSetting) {
        // 检查配额设置是否存在
        QuotaSetting existingSetting = getById(quotaSetting.getQuotaId());
        if (existingSetting == null) {
            throw new RuntimeException("配额设置不存在");
        }

        // 更新时间
        quotaSetting.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));

        // 更新配额设置
        updateById(quotaSetting);
        return getById(quotaSetting.getQuotaId());
    }

    @Override
    public IPage<QuotaSetting> getQuotaPage(Integer current, Integer size, LocalDate startDate,
                                          LocalDate endDate, Boolean isHoliday) {
        LambdaQueryWrapper<QuotaSetting> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (startDate != null) {
            wrapper.ge(QuotaSetting::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(QuotaSetting::getDate, endDate);
        }
        if (isHoliday != null) {
            wrapper.eq(QuotaSetting::getIsHoliday, isHoliday);
        }
        
        // 按日期升序排序
        wrapper.orderByAsc(QuotaSetting::getDate);
        
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public QuotaSetting getDateQuota(LocalDate date) {
        LambdaQueryWrapper<QuotaSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuotaSetting::getDate, date);
        return getOne(wrapper);
    }

    @Override
    public List<QuotaSetting> getDateRangeQuotas(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<QuotaSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(QuotaSetting::getDate, startDate, endDate)
               .orderByAsc(QuotaSetting::getDate);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean incrementCurrentCount(Integer quotaId) {
        return quotaSettingMapper.incrementCurrentCount(quotaId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean decrementCurrentCount(Integer quotaId) {
        return quotaSettingMapper.decrementCurrentCount(quotaId) > 0;
    }

    @Override
    public Boolean checkQuotaAvailable(LocalDate date) {
        QuotaSetting quotaSetting = getDateQuota(date);
        if (quotaSetting == null) {
            return false;
        }
        return quotaSetting.getCurrentCount() < quotaSetting.getMaxQuota();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchSetQuota(LocalDate startDate, LocalDate endDate, Integer maxQuota,
                               Boolean isHoliday, String specialEvent) {
        List<QuotaSetting> quotaSettings = new ArrayList<>();
        LocalDate currentDate = startDate;
        ZonedDateTime now = ZonedDateTime.now(TimeZoneConstant.ZONE_ID);
        
        while (!currentDate.isAfter(endDate)) {
            // 检查日期是否已存在配额设置
            if (getDateQuota(currentDate) == null) {
                QuotaSetting quotaSetting = new QuotaSetting();
                quotaSetting.setDate(currentDate);
                quotaSetting.setMaxQuota(maxQuota);
                quotaSetting.setCurrentCount(0);
                quotaSetting.setIsHoliday(isHoliday);
                quotaSetting.setSpecialEvent(specialEvent);
                quotaSetting.setCreateTime(now);
                quotaSettings.add(quotaSetting);
            }
            currentDate = currentDate.plusDays(1);
        }
        
        // 批量保存配额设置
        return !quotaSettings.isEmpty() && saveBatch(quotaSettings);
    }
}
