package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.BlacklistRecord;
import seig.ljm.xkckserver.mapper.BlacklistRecordMapper;
import seig.ljm.xkckserver.service.BlacklistRecordService;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * 黑名单记录服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
public class BlacklistRecordServiceImpl extends ServiceImpl<BlacklistRecordMapper, BlacklistRecord> implements BlacklistRecordService {

    @Override
    public BlacklistRecord addToBlacklist(BlacklistRecord record) {
        // 设置创建时间和开始时间
        record.setCreateTime(Instant.now());
        record.setStartTime(Instant.now());
        
        // 保存记录
        save(record);
        return record;
    }

    @Override
    public Boolean removeFromBlacklist(Integer recordId) {
        BlacklistRecord record = getById(recordId);
        if (record == null) {
            return false;
        }

        // 设置结束时间为当前时间
        record.setEndTime(Instant.now());
        return updateById(record);
    }

    @Override
    public IPage<BlacklistRecord> getRecordPage(Integer current, Integer size, Integer visitorId,
                                                ZonedDateTime startTime, ZonedDateTime endTime) {
        Page<BlacklistRecord> page = new Page<>(current, size);
        QueryWrapper<BlacklistRecord> wrapper = new QueryWrapper<>();

        // 添加查询条件
        if (visitorId != null) {
            wrapper.eq("visitor_id", visitorId);
        }
        if (startTime != null) {
            wrapper.ge("start_time", startTime);
        }
        if (endTime != null) {
            wrapper.le("start_time", endTime);
        }

        // 按创建时间倒序排序
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }

    @Override
    public BlacklistRecord getVisitorRecord(Integer visitorId) {
        QueryWrapper<BlacklistRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("visitor_id", visitorId)
               .isNull("end_time")  // 未结束的记录
               .orderByDesc("create_time")
               .last("LIMIT 1");    // 最新的一条记录
        return getOne(wrapper);
    }

    @Override
    public Boolean isInBlacklist(Integer visitorId) {
        QueryWrapper<BlacklistRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("visitor_id", visitorId)
               .isNull("end_time");  // 未结束的记录
        return count(wrapper) > 0;
    }
}
