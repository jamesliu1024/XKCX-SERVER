package seig.ljm.xkckserver.service.impl;

import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.mapper.MessageLogMapper;
import seig.ljm.xkckserver.service.MessageLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class MessageLogServiceImpl extends ServiceImpl<MessageLogMapper, MessageLog> implements MessageLogService {
    private MessageLogMapper messageLogMapper;
    @Autowired
    public MessageLogServiceImpl(MessageLogMapper messageLogMapper) {
        this.messageLogMapper = messageLogMapper;
    }
    
    @Override
    public List<Map<String, Object>> getDailyStats(LocalDate startDate, LocalDate endDate) {
        return baseMapper.getDailyStats(startDate, endDate);
    }

    @Override
    public List<MessageLog> getRecentByDeviceId(Integer deviceId, Integer limit) {
        return baseMapper.getRecentByDeviceId(deviceId, limit);
    }

    @Override
    public List<MessageLog> getErrorLogs(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.getErrorLogs(startTime, endTime);
    }
}
