package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.OperationLog;
import seig.ljm.xkckserver.mapper.OperationLogMapper;
import seig.ljm.xkckserver.service.OperationLogService;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public OperationLog recordOperation(OperationLog log) {
        log.setOperationTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        save(log);
        return log;
    }

    @Override
    public IPage<OperationLog> getLogPage(Integer current, Integer size, Integer operatorId,
                                        String operationType, Integer targetId,
                                        ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (operatorId != null) {
            wrapper.eq(OperationLog::getOperatorId, operatorId);
        }
        if (operationType != null) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }
        if (targetId != null) {
            wrapper.eq(OperationLog::getTargetId, targetId);
        }
        if (startTime != null) {
            wrapper.ge(OperationLog::getOperationTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getOperationTime, endTime);
        }
        
        // 按时间倒序排序
        wrapper.orderByDesc(OperationLog::getOperationTime);
        
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<OperationLog> getOperatorLogs(Integer operatorId, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getOperatorId, operatorId);
        
        if (startTime != null) {
            wrapper.ge(OperationLog::getOperationTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getOperationTime, endTime);
        }
        
        wrapper.orderByDesc(OperationLog::getOperationTime);
        return list(wrapper);
    }

    @Override
    public List<OperationLog> getOperationTypeLogs(String operationType, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getOperationType, operationType);
        
        if (startTime != null) {
            wrapper.ge(OperationLog::getOperationTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(OperationLog::getOperationTime, endTime);
        }
        
        wrapper.orderByDesc(OperationLog::getOperationTime);
        return list(wrapper);
    }

    @Override
    public List<OperationLog> getTargetLogs(Integer targetId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getTargetId, targetId)
               .orderByDesc(OperationLog::getOperationTime);
        return list(wrapper);
    }
}
