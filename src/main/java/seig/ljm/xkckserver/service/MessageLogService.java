package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.MessageLog;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * MQTT消息日志服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface MessageLogService extends IService<MessageLog> {
    
    /**
     * 记录消息
     *
     * @param messageLog 消息日志
     * @return 记录的消息
     */
    MessageLog recordMessage(MessageLog messageLog);
    
    /**
     * 分页查询消息
     *
     * @param current 当前页
     * @param size 每页大小
     * @param deviceId 设备ID
     * @param status 消息状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<MessageLog> getMessagePage(Integer current, Integer size, Integer deviceId,
                                   String status, ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取设备消息
     *
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息列表
     */
    List<MessageLog> getDeviceMessages(Integer deviceId, ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取最新消息
     *
     * @param deviceId 设备ID
     * @return 最新消息
     */
    MessageLog getLatestMessage(Integer deviceId);
    
    /**
     * 更新消息状态
     *
     * @param messageId 消息ID
     * @param status 新状态
     * @return 操作结果
     */
    Boolean updateStatus(Integer messageId, String status);
}
