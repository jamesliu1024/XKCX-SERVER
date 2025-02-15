package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.QuotaSetting;
import seig.ljm.xkckserver.mapper.QuotaSettingMapper;
import seig.ljm.xkckserver.service.QuotaSettingService;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuotaSettingServiceImpl extends ServiceImpl<QuotaSettingMapper, QuotaSetting> implements QuotaSettingService {

    @Autowired
    private QuotaSettingMapper quotaSettingMapper;

    @Override
    public QuotaSetting getQuotaByDate(LocalDate date) {
        return quotaSettingMapper.selectByDate(date);
    }

    @Override
    public boolean isQuotaAvailable(LocalDate date) {
        QuotaSetting quota = getQuotaByDate(date);
        if (quota == null) {
            return false;
        }

        Integer usedQuota = quotaSettingMapper.selectUsedQuota(date);
        return usedQuota < quota.getMaxQuota();
    }

    @Override
    public List<QuotaSetting> getQuotasByDateRange(LocalDate startDate, LocalDate endDate) {
        return quotaSettingMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public Page<QuotaSetting> getQuotaPage(Integer pageNum, Integer pageSize, LocalDate startDate, LocalDate endDate) {
        Page<QuotaSetting> page = new Page<>(pageNum, pageSize);
        QueryWrapper<QuotaSetting> queryWrapper = new QueryWrapper<>();
        
        if (startDate != null && endDate != null) {
            queryWrapper.between("date", startDate, endDate);
        }
        
        queryWrapper.orderByAsc("date");
        return page(page, queryWrapper);
    }
    
    /**
     * 批量保存或更新配额设置
     */
    public boolean batchSaveOrUpdate(List<QuotaSetting> quotaSettings) {
        return saveOrUpdateBatch(quotaSettings);
    }
    
    /**
     * 检查指定日期是否已设置配额
     */
    public boolean hasQuotaSetting(LocalDate date) {
        return getQuotaByDate(date) != null;
    }
}
