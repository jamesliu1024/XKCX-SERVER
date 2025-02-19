package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.mapper.MessageLogMapper;
import seig.ljm.xkckserver.service.MessageLogService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * MQTT消息日志服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
public class MessageLogServiceImpl extends ServiceImpl<MessageLogMapper, MessageLog> implements MessageLogService {

    @Override
    public MessageLog recordMessage(MessageLog messageLog) {
        // 设置接收时间
        messageLog.setReceiveTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        
        // 如果状态为空，设置默认状态
        if (messageLog.getStatus() == null) {
            messageLog.setStatus("received");
        }
        
        // 保存消息
        save(messageLog);
        return messageLog;
    }

    @Override
    public IPage<MessageLog> getMessagePage(Integer current, Integer size, Integer deviceId,
                                          String status, ZonedDateTime startTime, ZonedDateTime endTime) {
        Page<MessageLog> page = new Page<>(current, size);
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (deviceId != null) {
            wrapper.eq(MessageLog::getDeviceId, deviceId);
        }
        if (status != null) {
            wrapper.eq(MessageLog::getStatus, status);
        }
        if (startTime != null) {
            wrapper.ge(MessageLog::getReceiveTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MessageLog::getReceiveTime, endTime);
        }

        // 按接收时间倒序排序
        wrapper.orderByDesc(MessageLog::getReceiveTime);
        return page(page, wrapper);
    }

    @Override
    public List<MessageLog> getDeviceMessages(Integer deviceId, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getDeviceId, deviceId);
        
        if (startTime != null) {
            wrapper.ge(MessageLog::getReceiveTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MessageLog::getReceiveTime, endTime);
        }

        wrapper.orderByDesc(MessageLog::getReceiveTime);
        return list(wrapper);
    }

    @Override
    public MessageLog getLatestMessage(Integer deviceId) {
        LambdaQueryWrapper<MessageLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageLog::getDeviceId, deviceId)
               .orderByDesc(MessageLog::getReceiveTime)
               .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Boolean updateStatus(Integer messageId, String status) {
        MessageLog messageLog = getById(messageId);
        if (messageLog == null) {
            return false;
        }

        messageLog.setStatus(status);
        return updateById(messageLog);
    }
}
