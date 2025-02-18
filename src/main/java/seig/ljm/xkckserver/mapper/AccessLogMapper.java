package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.AccessLog;

import java.time.ZonedDateTime;
import java.util.List;

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
}

