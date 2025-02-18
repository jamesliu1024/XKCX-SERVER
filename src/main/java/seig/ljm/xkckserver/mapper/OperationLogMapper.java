package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 获取指定操作员的操作记录
     */
    @Select("<script>" +
            "SELECT log_id, operator_id, operation_type, target_id, operation_time, details " +
            "FROM OperationLog " +
            "WHERE operator_id = #{operatorId} " +
            "<if test='startTime != null and endTime != null'>" +
            "AND operation_time BETWEEN #{startTime} AND #{endTime} " +
            "</if>" +
            "ORDER BY operation_time DESC" +
            "</script>")
    List<OperationLog> getByOperator(@Param("operatorId") Integer operatorId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按操作类型查询日志
     */
    @Select("<script>" +
            "SELECT log_id, operator_id, operation_type, target_id, operation_time, details " +
            "FROM OperationLog " +
            "WHERE operation_type = #{operationType} " +
            "<if test='startTime != null and endTime != null'>" +
            "AND operation_time BETWEEN #{startTime} AND #{endTime} " +
            "</if>" +
            "ORDER BY operation_time DESC" +
            "</script>")
    List<OperationLog> getByOperationType(@Param("operationType") String operationType,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
}

