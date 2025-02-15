package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import seig.ljm.xkckserver.entity.AccessLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface AccessLogMapper extends BaseMapper<AccessLog> {
    
    /**
     * 获取设备进出统计
     */
    Map<String, Object> getDeviceStats(@Param("deviceId") Integer deviceId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 获取每日进出统计
     */
    List<Map<String, Object>> getDailyStats(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * 获取访问高峰时段
     */
    List<Map<String, Object>> getPeakHours(@Param("date") LocalDateTime date);
}

