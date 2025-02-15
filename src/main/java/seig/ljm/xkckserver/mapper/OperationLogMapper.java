package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 获取指定操作员的操作记录
     */
    List<OperationLog> getByOperator(@Param("operatorId") Integer operatorId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按操作类型查询日志
     */
    List<OperationLog> getByOperationType(@Param("operationType") String operationType,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
}

