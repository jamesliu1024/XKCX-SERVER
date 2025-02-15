package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.MessageLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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
public interface MessageLogMapper extends BaseMapper<MessageLog> {
    /**
     * 获取每日通信统计数据
     */
    List<Map<String, Object>> getDailyStats(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * 获取指定设备的最近通信记录
     */
    List<MessageLog> getRecentByDeviceId(@Param("deviceId") Integer deviceId,
                                        @Param("limit") Integer limit);

    /**
     * 获取错误日志
     */
    List<MessageLog> getErrorLogs(@Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);
}

