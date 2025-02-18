package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.AccessDevice;

/**
 * 门禁设备Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface AccessDeviceMapper extends BaseMapper<AccessDevice> {

    /**
     * 根据IP地址查询设备
     */
    @Select("SELECT * FROM access_device WHERE ip_address = #{ipAddress}")
    AccessDevice selectByIpAddress(@Param("ipAddress") String ipAddress);

    /**
     * 更新设备状态
     */
    @Update("UPDATE access_device SET status = #{status}, door_status = #{doorStatus} WHERE device_id = #{deviceId}")
    int updateDeviceStatus(
            @Param("deviceId") Integer deviceId,
            @Param("status") String status,
            @Param("doorStatus") String doorStatus);

    /**
     * 更新设备心跳时间
     */
    @Update("UPDATE access_device SET last_heartbeat_time = NOW(), status = 'online' WHERE device_id = #{deviceId}")
    int updateHeartbeatTime(@Param("deviceId") Integer deviceId);

    /**
     * 查询所有在线设备
     */
    @Select("SELECT * FROM access_device WHERE status = 'online'")
    java.util.List<AccessDevice> selectOnlineDevices();
}

