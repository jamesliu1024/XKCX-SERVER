package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.entity.AccessLog;
import seig.ljm.xkckserver.mapper.AccessLogMapper;
import seig.ljm.xkckserver.service.AccessDeviceService;
import seig.ljm.xkckserver.service.AccessLogService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 门禁日志服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
public class AccessLogServiceImpl extends ServiceImpl<AccessLogMapper, AccessLog> implements AccessLogService {

    private final AccessDeviceService accessDeviceService;

    @Autowired
    public AccessLogServiceImpl(@Lazy AccessDeviceService accessDeviceService) {
        this.accessDeviceService = accessDeviceService;
    }

    @Override
    public AccessLog recordAccess(AccessLog log) {
        // 验证设备是否存在
        AccessDevice device = accessDeviceService.getById(log.getDeviceId());
        if (device == null) {
            throw new RuntimeException("设备不存在");
        }

        // 设置访问时间和默认值
        log.setAccessTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        log.setHidden(false);
        
        // 保存日志
        save(log);
        return log;
    }

    @Override
    public IPage<AccessLog> getLogPage(Integer current, Integer size, Integer deviceId,
                                     Integer visitorId, Integer reservationId, String accessType,
                                     String result, ZonedDateTime startTime, ZonedDateTime endTime) {
        Page<AccessLog> page = new Page<>(current, size);
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (deviceId != null) {
            wrapper.eq(AccessLog::getDeviceId, deviceId);
        }
        if (visitorId != null) {
            wrapper.eq(AccessLog::getVisitorId, visitorId);
        }
        if (reservationId != null) {
            wrapper.eq(AccessLog::getReservationId, reservationId);
        }
        if (accessType != null) {
            wrapper.eq(AccessLog::getAccessType, accessType);
        }
        if (result != null) {
            wrapper.eq(AccessLog::getResult, result);
        }
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        // 只查询未隐藏的记录
        wrapper.eq(AccessLog::getHidden, false);
        wrapper.orderByDesc(AccessLog::getAccessTime);
        
        return page(page, wrapper);
    }

    @Override
    public List<AccessLog> getDeviceLogs(Integer deviceId, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessLog::getDeviceId, deviceId)
               .eq(AccessLog::getHidden, false);
        
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        wrapper.orderByDesc(AccessLog::getAccessTime);
        return list(wrapper);
    }

    @Override
    public List<AccessLog> getVisitorLogs(Integer visitorId, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessLog::getVisitorId, visitorId)
               .eq(AccessLog::getHidden, false);
        
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        wrapper.orderByDesc(AccessLog::getAccessTime);
        return list(wrapper);
    }

    @Override
    public List<AccessLog> getReservationLogs(Integer reservationId) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessLog::getReservationId, reservationId)
               .eq(AccessLog::getHidden, false)
               .orderByDesc(AccessLog::getAccessTime);
        return list(wrapper);
    }

    @Override
    public Boolean hideLog(Integer logId) {
        AccessLog log = getById(logId);
        if (log == null) {
            return false;
        }

        log.setHidden(true);
        return updateById(log);
    }

    @Override
    public Boolean restoreLog(Integer logId) {
        AccessLog log = getById(logId);
        if (log == null) {
            return false;
        }

        log.setHidden(false);
        return updateById(log);
    }

    @Override
    public IPage<AccessLog> getAllLogPage(Integer current, Integer size, Integer deviceId,
                                       Integer visitorId, Integer reservationId, String accessType,
                                       String result, ZonedDateTime startTime, ZonedDateTime endTime) {
        Page<AccessLog> page = new Page<>(current, size);
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (deviceId != null) {
            wrapper.eq(AccessLog::getDeviceId, deviceId);
        }
        if (visitorId != null) {
            wrapper.eq(AccessLog::getVisitorId, visitorId);
        }
        if (reservationId != null) {
            wrapper.eq(AccessLog::getReservationId, reservationId);
        }
        if (accessType != null) {
            wrapper.eq(AccessLog::getAccessType, accessType);
        }
        if (result != null) {
            wrapper.eq(AccessLog::getResult, result);
        }
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        wrapper.orderByDesc(AccessLog::getAccessTime);
        return page(page, wrapper);
    }

    @Override
    public List<AccessLog> getAllDeviceLogs(Integer deviceId, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessLog::getDeviceId, deviceId);
        
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        wrapper.orderByDesc(AccessLog::getAccessTime);
        return list(wrapper);
    }

    @Override
    public List<AccessLog> getAllVisitorLogs(Integer visitorId, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessLog::getVisitorId, visitorId);
        
        if (startTime != null) {
            wrapper.ge(AccessLog::getAccessTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AccessLog::getAccessTime, endTime);
        }

        wrapper.orderByDesc(AccessLog::getAccessTime);
        return list(wrapper);
    }

    @Override
    public List<AccessLog> getAllReservationLogs(Integer reservationId) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessLog::getReservationId, reservationId)
               .orderByDesc(AccessLog::getAccessTime);
        return list(wrapper);
    }
}
