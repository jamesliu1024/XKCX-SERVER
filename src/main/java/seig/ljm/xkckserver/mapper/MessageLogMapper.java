package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.MessageLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
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
    @Select("SELECT " +
            "DATE(receive_time) as date, " +
            "COUNT(*) as totalMessages, " +
            "SUM(CASE WHEN status = 'processed' THEN 1 ELSE 0 END) as processedCount, " +
            "SUM(CASE WHEN status != 'processed' THEN 1 ELSE 0 END) as errorCount " +
            "FROM MessageLog " +
            "WHERE receive_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(receive_time) " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getDailyStats(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    /**
     * 获取指定设备的最近通信记录
     */
    @Select("SELECT message_id, device_id, payload, receive_time, status " +
            "FROM MessageLog " +
            "WHERE device_id = #{deviceId} " +
            "ORDER BY receive_time DESC " +
            "LIMIT #{limit}")
    List<MessageLog> getRecentByDeviceId(@Param("deviceId") Integer deviceId,
                                        @Param("limit") Integer limit);

    /**
     * 获取错误日志
     */
    @Select("<script>" +
            "SELECT message_id, device_id, payload, receive_time, status " +
            "FROM MessageLog " +
            "WHERE status != 'processed' " +
            "<if test='startTime != null and endTime != null'>" +
            "AND receive_time BETWEEN #{startTime} AND #{endTime} " +
            "</if>" +
            "ORDER BY receive_time DESC" +
            "</script>")
    List<MessageLog> getErrorLogs(@Param("startTime") LocalDateTime startTime,
                                 @Param("endTime") LocalDateTime endTime);
}

