package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.AccessDevice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取门禁设备列表
     * @param status 设备状态筛选
     * @param type 设备类型筛选
     * @return 设备列表
     */
    List<AccessDevice> listDevices(String status, String type);

    /**
     * 更新设备状态
     * @param deviceId 设备ID
     * @param status 新状态
     */
    void updateStatus(Integer deviceId, String status);

    /**
     * 分页查询设备列表
     * @param current 当前页
     * @param size 每页大小
     * @param status 设备状态筛选
     * @param type 设备类型筛选
     * @param location 设备位置筛选
     * @param doorStatus 门禁状态筛选
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 分页结果
     */
    IPage<AccessDevice> getDevicePage(Integer current, Integer size, String status, String type,
            String location, String doorStatus, String sortField, String sortOrder);

    /**
     * 获取设备运行状态统计
     * @return 统计结果
     */
    Map<String, Object> getDeviceStatusStatistics();
}
