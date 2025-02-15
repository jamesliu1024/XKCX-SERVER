package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.AccessLog;
import seig.ljm.xkckserver.mapper.AccessLogMapper;
import seig.ljm.xkckserver.service.AccessLogService;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccessLogServiceImpl extends ServiceImpl<AccessLogMapper, AccessLog> implements AccessLogService {

    @Override
    public List<AccessLog> queryLogs(Integer visitorId, Integer deviceId, String accessType,
                                   String result, LocalDateTime startTime, LocalDateTime endTime) {
        return lambdaQuery()
                .eq(visitorId != null, AccessLog::getVisitorId, visitorId)
                .eq(deviceId != null, AccessLog::getDeviceId, deviceId)
                .eq(accessType != null, AccessLog::getAccessType, accessType)
                .eq(result != null, AccessLog::getResult, result)
                .ge(startTime != null, AccessLog::getAccessTime, startTime)
                .le(endTime != null, AccessLog::getAccessTime, endTime)
                .orderByDesc(AccessLog::getAccessTime)
                .list();
    }

    @Override
    public Map<String, Object> getDeviceStats(Integer deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.getDeviceStats(deviceId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getDailyStats(LocalDateTime startDate, LocalDateTime endDate) {
        return baseMapper.getDailyStats(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getPeakHours(LocalDateTime date) {
        return baseMapper.getPeakHours(date);
    }

    @Override
    public List<AccessLog> getAbnormalLogs(LocalDateTime startTime, LocalDateTime endTime) {
        return lambdaQuery()
                .eq(AccessLog::getResult, "denied")
                .between(AccessLog::getAccessTime, startTime, endTime)
                .orderByDesc(AccessLog::getAccessTime)
                .list();
    }
}
