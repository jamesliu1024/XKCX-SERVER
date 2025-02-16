package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.MessageLog;
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
public interface MessageLogService extends IService<MessageLog> {
    /**
     * 获取每日通信统计
     */
    List<Map<String, Object>> getDailyStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取指定设备的最近通信记录
     */
    List<MessageLog> getRecentByDeviceId(Integer deviceId, Integer limit);
    
    /**
     * 获取错误日志
     */
    List<MessageLog> getErrorLogs(LocalDateTime startTime, LocalDateTime endTime);
}
