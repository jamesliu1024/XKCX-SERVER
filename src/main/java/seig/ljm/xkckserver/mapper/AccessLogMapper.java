package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.dto.DeviceFlowDTO;
import seig.ljm.xkckserver.entity.AccessLog;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * 门禁日志Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {

    /**
     * 获取指定时间范围内的设备访问记录
     */
    @Select("SELECT * FROM access_log " +
            "WHERE device_id = #{deviceId} " +
            "AND access_time BETWEEN #{startTime} AND #{endTime} " +
            "AND hidden = false " +
            "ORDER BY access_time DESC")
    List<AccessLog> getDeviceAccessLogs(@Param("deviceId") Integer deviceId,
                                      @Param("startTime") ZonedDateTime startTime,
                                      @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取指定时间范围内的访客访问记录
     */
    @Select("SELECT * FROM access_log " +
            "WHERE visitor_id = #{visitorId} " +
            "AND access_time BETWEEN #{startTime} AND #{endTime} " +
            "AND hidden = false " +
            "ORDER BY access_time DESC")
    List<AccessLog> getVisitorAccessLogs(@Param("visitorId") Integer visitorId,
                                       @Param("startTime") ZonedDateTime startTime,
                                       @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取指定预约的访问记录
     */
    @Select("SELECT * FROM access_log " +
            "WHERE reservation_id = #{reservationId} " +
            "AND hidden = false " +
            "ORDER BY access_time DESC")
    List<AccessLog> getReservationAccessLogs(@Param("reservationId") Integer reservationId);

    /**
     * 获取最近的访问记录
     */
    @Select("SELECT * FROM access_log " +
            "WHERE device_id = #{deviceId} " +
            "AND hidden = false " +
            "ORDER BY access_time DESC LIMIT 1")
    AccessLog getLastAccess(@Param("deviceId") Integer deviceId);

    /**
     * 隐藏日志
     */
    @Update("UPDATE access_log SET hidden = true WHERE log_id = #{logId}")
    int hideLog(@Param("logId") Integer logId);

    /**
     * 批量隐藏日志
     */
    @Update("<script>" +
            "UPDATE access_log SET hidden = true " +
            "WHERE log_id IN " +
            "<foreach collection='logIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchHideLog(@Param("logIds") List<Integer> logIds);

    @Select("SELECT COUNT(DISTINCT visitor_id) as count " +
            "FROM access_log " +
            "WHERE visitor_id IN ( " +
            "   SELECT visitor_id " +
            "   FROM access_log a1 " +
            "   WHERE access_type = 'entry' " +
            "   AND NOT EXISTS ( " +
            "       SELECT 1 " +
            "       FROM access_log a2 " +
            "       WHERE a2.visitor_id = a1.visitor_id " +
            "       AND a2.access_time > a1.access_time " +
            "       AND a2.access_type = 'exit' " +
            "   ) " +
            ")")
    Integer getCurrentFlow();

    @Select("SELECT d.location as location, COUNT(DISTINCT al.visitor_id) as count " +
            "FROM access_device d " +
            "LEFT JOIN access_log al ON d.device_id = al.device_id " +
            "AND al.visitor_id IN ( " +
            "   SELECT visitor_id " +
            "   FROM access_log a1 " +
            "   WHERE access_type = 'entry' " +
            "   AND NOT EXISTS ( " +
            "       SELECT 1 " +
            "       FROM access_log a2 " +
            "       WHERE a2.visitor_id = a1.visitor_id " +
            "       AND a2.access_time > a1.access_time " +
            "       AND a2.access_type = 'exit' " +
            "   ) " +
            ") " +
            "GROUP BY d.device_id, d.location")
    @Results({
        @Result(property = "location", column = "location"),
        @Result(property = "count", column = "count")
    })
    List<DeviceFlowDTO> getDeviceCurrentFlow();

    @Select("SELECT " +
            "   SUM(CASE WHEN access_type = 'entry' AND access_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN 1 ELSE 0 END) as entry_count, " +
            "   SUM(CASE WHEN access_type = 'exit' AND access_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN 1 ELSE 0 END) as exit_count, " +
            "   COUNT(DISTINCT visitor_id) as unique_visitors " +
            "FROM access_log " +
            "WHERE access_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR)")
    Map<String, Object> getHourlyStats();
}

