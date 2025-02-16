package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.AccessLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface AccessLogService extends IService<AccessLog> {
    /**
     * 查询进出记录
     */
    List<AccessLog> queryLogs(Integer visitorId, Integer deviceId, String accessType, 
                            String result, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取设备进出统计
     */
    Map<String, Object> getDeviceStats(Integer deviceId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取每日进出统计
     */
    List<Map<String, Object>> getDailyStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取访问高峰时段
     */
    List<Map<String, Object>> getPeakHours(LocalDate date);

    /**
     * 获取异常访问记录
     */
    List<AccessLog> getAbnormalLogs(LocalDateTime startTime, LocalDateTime endTime);
}
