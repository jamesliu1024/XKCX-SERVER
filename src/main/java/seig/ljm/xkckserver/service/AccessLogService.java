package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.AccessLog;

import java.time.ZonedDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface AccessLogService extends IService<AccessLog> {
    
    /**
     * 记录访问日志
     *
     * @param log 日志信息
     * @return 记录的日志
     */
    AccessLog recordAccess(AccessLog log);
    
    /**
     * 分页查询日志
     *
     * @param current 当前页
     * @param size 每页大小
     * @param deviceId 设备ID
     * @param visitorId 访客ID
     * @param reservationId 预约ID
     * @param accessType 访问类型
     * @param result 访问结果
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<AccessLog> getLogPage(Integer current, Integer size, Integer deviceId, 
                               Integer visitorId, Integer reservationId, String accessType, 
                               String result, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取设备日志
     *
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AccessLog> getDeviceLogs(Integer deviceId, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取访客日志
     *
     * @param visitorId 访客ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AccessLog> getVisitorLogs(Integer visitorId, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取预约日志
     *
     * @param reservationId 预约ID
     * @return 日志列表
     */
    List<AccessLog> getReservationLogs(Integer reservationId);

    /**
     * 隐藏日志
     *
     * @param logId 日志ID
     * @return 操作结果
     */
    Boolean hideLog(Integer logId);

    /**
     * 恢复日志
     *
     * @param logId 日志ID
     * @return 操作结果
     */
    Boolean restoreLog(Integer logId);

    /**
     * 分页查询所有日志（包括隐藏日志）
     *
     * @param current 当前页
     * @param size 每页大小
     * @param deviceId 设备ID
     * @param visitorId 访客ID
     * @param reservationId 预约ID
     * @param accessType 访问类型
     * @param result 访问结果
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<AccessLog> getAllLogPage(Integer current, Integer size, Integer deviceId,
                                 Integer visitorId, Integer reservationId, String accessType,
                                 String result, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取设备所有日志（包括隐藏日志）
     *
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AccessLog> getAllDeviceLogs(Integer deviceId, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取访客所有日志（包括隐藏日志）
     *
     * @param visitorId 访客ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<AccessLog> getAllVisitorLogs(Integer visitorId, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取预约所有日志（包括隐藏日志）
     *
     * @param reservationId 预约ID
     * @return 日志列表
     */
    List<AccessLog> getAllReservationLogs(Integer reservationId);

    /**
     * 获取进出统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取设备使用统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getDeviceUsageStatistics(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getRealTimeFlow();
}
