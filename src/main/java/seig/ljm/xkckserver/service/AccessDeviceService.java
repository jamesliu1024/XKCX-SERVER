package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.AccessDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 门禁设备服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface AccessDeviceService extends IService<AccessDevice> {
    
    /**
     * 添加门禁设备
     *
     * @param device 设备信息
     * @return 添加后的设备信息
     */
    AccessDevice addDevice(AccessDevice device);
    
    /**
     * 更新门禁设备信息
     *
     * @param device 设备信息
     * @return 更新后的设备信息
     */
    AccessDevice updateDevice(AccessDevice device);
    
    /**
     * 获取设备状态
     *
     * @param deviceId 设备ID
     * @return 设备状态信息
     */
    AccessDevice getDeviceStatus(Integer deviceId);
    
    /**
     * 紧急控制
     *
     * @param deviceId 设备ID
     * @param action 控制动作
     * @param reason 操作原因
     * @return 操作结果
     */
    Boolean emergencyControl(Integer deviceId, String action, String reason);
    
    /**
     * 更新设备心跳时间
     *
     * @param deviceId 设备ID
     * @return 更新结果
     */
    Boolean updateHeartbeat(Integer deviceId);

    /**
     * 更新设备状态
     *
     * @param deviceId 设备ID
     * @param status 设备状态
     * @return 更新结果
     */
    Boolean updateDeviceStatus(Integer deviceId, String status);
}
