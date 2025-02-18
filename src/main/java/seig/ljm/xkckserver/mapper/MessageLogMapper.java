package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.MessageLog;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * MQTT消息日志Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface MessageLogMapper extends BaseMapper<MessageLog> {

    /**
     * 获取指定时间范围内的设备消息
     */
    @Select("SELECT * FROM message_log " +
            "WHERE device_id = #{deviceId} " +
            "AND receive_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY receive_time DESC")
    List<MessageLog> getDeviceMessages(@Param("deviceId") Integer deviceId,
                                     @Param("startTime") ZonedDateTime startTime,
                                     @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取设备最新消息
     */
    @Select("SELECT * FROM message_log " +
            "WHERE device_id = #{deviceId} " +
            "ORDER BY receive_time DESC LIMIT 1")
    MessageLog getLatestMessage(@Param("deviceId") Integer deviceId);

    /**
     * 更新消息状态
     */
    @Update("UPDATE message_log SET status = #{status} " +
            "WHERE message_id = #{messageId}")
    int updateStatus(@Param("messageId") Integer messageId,
                    @Param("status") String status);

    /**
     * 批量更新消息状态
     */
    @Update("<script>" +
            "UPDATE message_log SET status = #{status} " +
            "WHERE message_id IN " +
            "<foreach collection='messageIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("messageIds") List<Integer> messageIds,
                         @Param("status") String status);

    /**
     * 删除指定时间之前的消息
     */
    @Delete("DELETE FROM message_log " +
            "WHERE receive_time < #{time}")
    int deleteMessagesBefore(@Param("time") ZonedDateTime time);
}

