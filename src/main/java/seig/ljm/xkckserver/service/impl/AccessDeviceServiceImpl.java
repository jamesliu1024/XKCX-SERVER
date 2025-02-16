package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.mapper.AccessDeviceMapper;
import seig.ljm.xkckserver.service.AccessDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Service
public class AccessDeviceServiceImpl extends ServiceImpl<AccessDeviceMapper, AccessDevice> implements AccessDeviceService {

    private AccessDeviceMapper accessDeviceMapper;
    @Autowired
    public AccessDeviceServiceImpl(AccessDeviceMapper accessDeviceMapper) {
        this.accessDeviceMapper = accessDeviceMapper;
    }

    @Override
    public Map<String, Object> getDeviceUsageStats(Integer deviceId) {
        Map<String, Object> stats = baseMapper.getDeviceUsageStats(deviceId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("deviceId", deviceId);
            stats.put("totalAccess", 0);
            stats.put("successRate", 0.0);
        }
        return stats;
    }

    @Override
    public List<AccessDevice> getOnlineDevices() {
        return lambdaQuery()
                .eq(AccessDevice::getStatus, "online")
                .list();
    }

    @Override
    public boolean updateDeviceStatus(Integer deviceId, String status) {
        return lambdaUpdate()
                .eq(AccessDevice::getDeviceId, deviceId)
                .set(AccessDevice::getStatus, status)
                .update();
    }

    @Override
    public List<AccessDevice> findByLocation(String location) {
        return lambdaQuery()
                .like(AccessDevice::getLocation, location)
                .list();
    }
}
