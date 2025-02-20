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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public IPage<AccessDevice> getDevicePage(Integer current, Integer size, String status, String type,
            String location, String doorStatus, String sortField, String sortOrder) {
        LambdaQueryWrapper<AccessDevice> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AccessDevice::getStatus, status);
        }
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(AccessDevice::getDeviceType, type);
        }
        
        if (location != null && !location.isEmpty()) {
            wrapper.like(AccessDevice::getLocation, "%" + location + "%");
        }
        
        if (doorStatus != null && !doorStatus.isEmpty()) {
            wrapper.eq(AccessDevice::getDoorStatus, doorStatus);
        }
        
        // 添加排序
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        switch (sortField) {
            case "status":
                wrapper.orderBy(true, isAsc, AccessDevice::getStatus);
                break;
            case "lastHeartbeatTime":
            default:
                wrapper.orderBy(true, isAsc, AccessDevice::getLastHeartbeatTime);
                break;
        }
        
        // 执行分页查询
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public Map<String, Object> getDeviceStatusStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取所有设备的状态统计
            Map<String, Long> statusCount = list().stream()
                    .collect(Collectors.groupingBy(
                            AccessDevice::getStatus,
                            Collectors.counting()
                    ));
            
            // 2. 按设备类型分组统计状态
            Map<String, Map<String, Long>> typeStatusCount = list().stream()
                    .collect(Collectors.groupingBy(
                            AccessDevice::getDeviceType,
                            Collectors.groupingBy(
                                    AccessDevice::getStatus,
                                    Collectors.counting()
                            )
                    ));
            
            // 3. 统计总设备数
            long totalDevices = list().size();
            
            // 4. 统计最近心跳时间超过5分钟的设备数量（可能存在通信问题）
            ZonedDateTime fiveMinutesAgo = ZonedDateTime.now().minusMinutes(5);
            long potentialIssueDevices = list().stream()
                    .filter(device -> device.getLastHeartbeatTime() != null && 
                            device.getLastHeartbeatTime().isBefore(fiveMinutesAgo))
                    .count();
            
            // 5. 组装返回数据
            result.put("totalDevices", totalDevices);
            result.put("statusCount", statusCount);
            result.put("typeStatusCount", typeStatusCount);
            result.put("potentialIssueDevices", potentialIssueDevices);
            result.put("success", true);
            result.put("message", "获取设备状态统计成功");
            
        } catch (Exception e) {
            log.error("获取设备状态统计失败", e);
            result.put("success", false);
            result.put("message", "获取设备状态统计失败：" + e.getMessage());
        }
        
        return result;
    }
}
