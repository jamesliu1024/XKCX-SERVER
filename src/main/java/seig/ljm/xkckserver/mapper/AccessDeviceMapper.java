package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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
public interface AccessDeviceMapper extends BaseMapper<AccessDevice> {
    /**
     * 获取设备使用统计
     * @param deviceId 设备ID
     * @return 统计数据
     */
    Map<String, Object> getDeviceUsageStats(@Param("deviceId") Integer deviceId);
}

