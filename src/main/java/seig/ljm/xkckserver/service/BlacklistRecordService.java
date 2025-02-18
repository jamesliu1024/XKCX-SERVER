package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.BlacklistRecord;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * 黑名单记录服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface BlacklistRecordService extends IService<BlacklistRecord> {
    
    /**
     * 添加黑名单记录
     *
     * @param record 黑名单记录
     * @return 添加的记录
     */
    BlacklistRecord addToBlacklist(BlacklistRecord record);
    
    /**
     * 从黑名单中移除
     *
     * @param recordId 记录ID
     * @return 操作结果
     */
    Boolean removeFromBlacklist(Integer recordId);
    
    /**
     * 分页查询黑名单记录
     *
     * @param current 当前页
     * @param size 每页大小
     * @param visitorId 访客ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<BlacklistRecord> getRecordPage(Integer current, Integer size, Integer visitorId,
                                         ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取访客的黑名单记录
     *
     * @param visitorId 访客ID
     * @return 黑名单记录
     */
    BlacklistRecord getVisitorRecord(Integer visitorId);
    
    /**
     * 检查访客是否在黑名单中
     *
     * @param visitorId 访客ID
     * @return 是否在黑名单中
     */
    Boolean isInBlacklist(Integer visitorId);
}
