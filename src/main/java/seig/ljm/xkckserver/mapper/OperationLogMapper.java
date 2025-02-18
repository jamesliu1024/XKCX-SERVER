package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.OperationLog;


import java.time.ZonedDateTime;
import java.util.List;

/**
 * 操作日志Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 获取指定时间范围内的操作日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    @Select("SELECT * FROM operation_log WHERE operation_time BETWEEN #{startTime} AND #{endTime} ORDER BY operation_time DESC")
    List<OperationLog> getLogsByTimeRange(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取指定操作员在指定时间范围内的操作日志
     *
     * @param operatorId 操作员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    @Select("SELECT * FROM operation_log WHERE operator_id = #{operatorId} AND operation_time BETWEEN #{startTime} AND #{endTime} ORDER BY operation_time DESC")
    List<OperationLog> getOperatorLogsByTimeRange(@Param("operatorId") Integer operatorId, 
                                                 @Param("startTime") ZonedDateTime startTime,
                                                 @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取指定操作类型在指定时间范围内的操作日志
     *
     * @param operationType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    @Select("SELECT * FROM operation_log WHERE operation_type = #{operationType} AND operation_time BETWEEN #{startTime} AND #{endTime} ORDER BY operation_time DESC")
    List<OperationLog> getOperationTypeLogsByTimeRange(@Param("operationType") String operationType,
                                                      @Param("startTime") ZonedDateTime startTime,
                                                      @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取指定目标对象的操作日志
     *
     * @param targetId 目标对象ID
     * @return 日志列表
     */
    @Select("SELECT * FROM operation_log WHERE target_id = #{targetId} ORDER BY operation_time DESC")
    List<OperationLog> getTargetLogs(@Param("targetId") Integer targetId);

    /**
     * 批量删除指定时间之前的日志
     *
     * @param time 指定时间
     * @return 删除的记录数
     */
    @Delete("DELETE FROM operation_log WHERE operation_time < #{time}")
    int deleteLogsBefore(@Param("time") ZonedDateTime time);
}

