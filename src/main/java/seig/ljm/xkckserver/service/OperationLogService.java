package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 获取指定操作员的操作记录
     */
    List<OperationLog> getByOperator(Integer operatorId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 按操作类型查询日志
     */
    List<OperationLog> getByOperationType(String operationType, LocalDateTime startTime, LocalDateTime endTime);
}
