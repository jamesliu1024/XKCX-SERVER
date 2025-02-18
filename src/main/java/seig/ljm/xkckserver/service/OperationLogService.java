package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.OperationLog;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 操作日志服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 记录操作
     *
     * @param log 操作日志
     * @return 记录的日志
     */
    OperationLog recordOperation(OperationLog log);
    
    /**
     * 分页查询日志
     *
     * @param current 当前页
     * @param size 每页大小
     * @param operatorId 操作员ID
     * @param operationType 操作类型
     * @param targetId 操作对象ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<OperationLog> getLogPage(Integer current, Integer size, Integer operatorId,
                                  String operationType, Integer targetId,
                                  ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取操作员日志
     *
     * @param operatorId 操作员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<OperationLog> getOperatorLogs(Integer operatorId, ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取操作类型日志
     *
     * @param operationType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<OperationLog> getOperationTypeLogs(String operationType, ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取目标对象日志
     *
     * @param targetId 目标对象ID
     * @return 日志列表
     */
    List<OperationLog> getTargetLogs(Integer targetId);
}
