package seig.ljm.xkckserver.service.impl;

import seig.ljm.xkckserver.entity.OperationLog;
import seig.ljm.xkckserver.mapper.OperationLogMapper;
import seig.ljm.xkckserver.service.OperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Override
    public List<OperationLog> getByOperator(Integer operatorId, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.getByOperator(operatorId, startTime, endTime);
    }
    
    @Override
    public List<OperationLog> getByOperationType(String operationType, LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.getByOperationType(operationType, startTime, endTime);
    }
}
