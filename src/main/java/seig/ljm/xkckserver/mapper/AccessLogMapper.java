package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import seig.ljm.xkckserver.entity.AccessLog;

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
@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {
    
    @Select("SELECT " +
            "COUNT(*) as totalAccess, " +
            "SUM(CASE WHEN access_type = 'entry' THEN 1 ELSE 0 END) as entryCount, " +
            "SUM(CASE WHEN access_type = 'exit' THEN 1 ELSE 0 END) as exitCount, " +
            "SUM(CASE WHEN result = 'allowed' THEN 1 ELSE 0 END) as allowedCount, " +
            "SUM(CASE WHEN result = 'denied' THEN 1 ELSE 0 END) as deniedCount " +
            "FROM AccessLog " +
            "WHERE device_id = #{deviceId} " +
            "AND access_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> getDeviceStats(@Param("deviceId") Integer deviceId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    @Select("SELECT " +
            "DATE(access_time) as date, " +
            "COUNT(*) as totalCount, " +
            "SUM(CASE WHEN access_type = 'entry' THEN 1 ELSE 0 END) as entryCount, " +
            "SUM(CASE WHEN access_type = 'exit' THEN 1 ELSE 0 END) as exitCount " +
            "FROM AccessLog " +
            "WHERE access_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(access_time) " +
            "ORDER BY date")
    List<Map<String, Object>> getDailyStats(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Select("SELECT " +
            "HOUR(access_time) as hour, " +
            "COUNT(*) as count " +
            "FROM AccessLog " +
            "WHERE DATE(access_time) = #{date} " +
            "GROUP BY HOUR(access_time) " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getPeakHours(@Param("date") LocalDate date);
}

