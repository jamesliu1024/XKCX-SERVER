package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import seig.ljm.xkckserver.entity.AccessDevice;

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
public interface AccessDeviceMapper extends BaseMapper<AccessDevice> {
    /**
     * 获取设备使用统计
     * @param deviceId 设备ID
     * @return 统计数据
     */
    @Select("SELECT " +
            "d.device_id, " +
            "COUNT(l.log_id) as totalAccess, " +
            "SUM(CASE WHEN l.result = 'allowed' THEN 1 ELSE 0 END) * 100.0 / COUNT(l.log_id) as successRate " +
            "FROM AccessDevice d " +
            "LEFT JOIN AccessLog l ON d.device_id = l.device_id " +
            "WHERE d.device_id = #{deviceId} " +
            "GROUP BY d.device_id")
    Map<String, Object> getDeviceUsageStats(@Param("deviceId") Integer deviceId);
}

