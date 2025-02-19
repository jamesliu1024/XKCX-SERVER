package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.mapper.AccessDeviceMapper;
import seig.ljm.xkckserver.service.AccessDeviceService;
import seig.ljm.xkckserver.service.MQTTMessageService;

import java.time.ZonedDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
public class AccessDeviceServiceImpl extends ServiceImpl<AccessDeviceMapper, AccessDevice> implements AccessDeviceService {

    private final MQTTMessageService mqttMessageService;

    @Autowired
    public AccessDeviceServiceImpl(MQTTMessageService mqttMessageService) {
        this.mqttMessageService = mqttMessageService;
    }

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
}
