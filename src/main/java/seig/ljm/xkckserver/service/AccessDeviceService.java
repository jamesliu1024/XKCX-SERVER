package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.AccessDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 门禁设备服务接口
 */
public interface AccessDeviceService extends IService<AccessDevice> {
    /**
     * 获取指定设备的使用统计
     * @param deviceId 设备ID
     * @return 统计数据
     */
    Map<String, Object> getDeviceUsageStats(Integer deviceId);

    /**
     * 获取所有在线设备
     * @return 在线设备列表
     */
    List<AccessDevice> getOnlineDevices();

    /**
     * 更新设备状态
     * @param deviceId 设备ID
     * @param status 新状态
     * @return 更新是否成功
     */
    boolean updateDeviceStatus(Integer deviceId, String status);

    /**
     * 按位置查找设备
     * @param location 位置
     * @return 设备列表
     */
    List<AccessDevice> findByLocation(String location);
}
