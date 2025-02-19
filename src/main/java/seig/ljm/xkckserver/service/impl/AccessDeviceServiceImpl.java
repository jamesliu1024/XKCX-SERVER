package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.mapper.AccessDeviceMapper;
import seig.ljm.xkckserver.service.AccessDeviceService;
import seig.ljm.xkckserver.service.MQTTMessageService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljm
 * @since 2025-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessDeviceServiceImpl extends ServiceImpl<AccessDeviceMapper, AccessDevice> implements AccessDeviceService {

    private final MQTTMessageService mqttMessageService;
    private final AccessDeviceMapper accessDeviceMapper;

    @Override
    public AccessDevice addDevice(AccessDevice device) {
        // 设置初始状态
//        device.setStatus("offline");
//        device.setDoorStatus("closed");
//        device.setLastHeartbeatTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        save(device);
        return device;
    }

    @Override
    public AccessDevice updateDevice(AccessDevice device) {
        if (updateById(device)) {
            return getById(device.getDeviceId());
        }
        return null;
    }

    @Override
    public AccessDevice getDeviceStatus(Integer deviceId) {
        return getById(deviceId);
    }

    @Override
    public Boolean emergencyControl(Integer deviceId, String action, String reason) {
        AccessDevice device = getById(deviceId);
        if (device == null) {
            return false;
        }

        // 发送MQTT消息到设备
        boolean success = mqttMessageService.sendEmergencyControl(deviceId, action, reason);
        if (success) {
            // 更新设备状态
            device.setDoorStatus(action.equals("emergency_open") ? "open" : "closed");
            updateById(device);
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateHeartbeat(Integer deviceId) {
        AccessDevice device = getById(deviceId);
        if (device == null) {
            return false;
        }

        device.setLastHeartbeatTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        device.setStatus("online");
        return updateById(device);
    }

    @Override
    public Boolean updateDeviceStatus(Integer deviceId, String status) {
        AccessDevice device = getById(deviceId);
        if (device == null) {
            return false;
        }

        device.setStatus(status);
        return updateById(device);
    }

    @Override
    public List<AccessDevice> listDevices(String status, String type) {
        LambdaQueryWrapper<AccessDevice> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AccessDevice::getStatus, status);
        }
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(AccessDevice::getDeviceType, type);
        }
        
        wrapper.orderByDesc(AccessDevice::getLastHeartbeatTime);
        
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer deviceId, String status) {
        AccessDevice device = getById(deviceId);
        if (device != null) {
            device.setStatus(status);
            updateById(device);
            
            log.info("Updated device {} status to {}", deviceId, status);
        } else {
            log.warn("Device {} not found when updating status", deviceId);
        }
    }

    @Override
    public IPage<AccessDevice> getDevicePage(Integer current, Integer size, String status, String type) {
        LambdaQueryWrapper<AccessDevice> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AccessDevice::getStatus, status);
        }
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(AccessDevice::getDeviceType, type);
        }
        
        wrapper.orderByDesc(AccessDevice::getLastHeartbeatTime);
        
        return page(new Page<>(current, size), wrapper);
    }
}
