package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.RfidCardRecord;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * RFID卡片使用记录服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface RfidCardRecordService extends IService<RfidCardRecord> {

    /**
     * 记录发卡操作
     *
     * @param cardId 卡片ID
     * @param reservationId 预约ID
     * @param adminId 管理员ID
     * @param expirationTime 过期时间
     * @param remarks 备注
     * @return 记录信息
     */
    RfidCardRecord recordIssue(Integer cardId, Integer reservationId, Integer adminId, 
                              ZonedDateTime expirationTime, String remarks);

    /**
     * 记录还卡操作
     *
     * @param cardId 卡片ID
     * @param reservationId 预约ID
     * @param adminId 管理员ID
     * @param remarks 备注
     * @return 记录信息
     */
    RfidCardRecord recordReturn(Integer cardId, Integer reservationId, Integer adminId, String remarks);

    /**
     * 记录挂失操作
     *
     * @param cardId 卡片ID
     * @param reservationId 预约ID
     * @param adminId 管理员ID
     * @param remarks 备注
     * @return 记录信息
     */
    RfidCardRecord recordLost(Integer cardId, Integer reservationId, Integer adminId, String remarks);

    /**
     * 记录注销操作
     *
     * @param cardId 卡片ID
     * @param reservationId 预约ID
     * @param adminId 管理员ID
     * @param remarks 备注
     * @return 记录信息
     */
    RfidCardRecord recordDeactivate(Integer cardId, Integer reservationId, Integer adminId, String remarks);

    /**
     * 分页查询记录
     *
     * @param current 当前页
     * @param size 每页大小
     * @param cardId 卡片ID
     * @param operationType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<RfidCardRecord> getRecordPage(Integer current, Integer size, Integer cardId,
                                       String operationType, ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取卡片的使用记录
     *
     * @param cardId 卡片ID
     * @return 记录列表
     */
    List<RfidCardRecord> getCardRecords(Integer cardId);

    /**
     * 获取预约的卡片记录
     *
     * @param reservationId 预约ID
     * @return 记录列表
     */
    List<RfidCardRecord> getReservationRecords(Integer reservationId);

    /**
     * 获取管理员的操作记录
     *
     * @param adminId 管理员ID
     * @return 记录列表
     */
    List<RfidCardRecord> getAdminRecords(Integer adminId);

    /**
     * 获取时间范围内的记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录列表
     */
    List<RfidCardRecord> getTimeRangeRecords(ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 获取卡片最新的使用记录
     *
     * @param cardId 卡片ID
     * @return 记录信息
     */
    RfidCardRecord getLatestCardRecord(Integer cardId);

    /**
     * 软删除记录
     *
     * @param recordId 记录ID
     * @return 是否成功
     */
    Boolean deleteRecord(Integer recordId);

    /**
     * 获取即将过期的卡片记录
     *
     * @return 记录列表
     */
    List<RfidCardRecord> getExpiringRecords();

    /**
     * 批量软删除记录
     *
     * @param recordIds 记录ID列表
     * @return 是否成功
     */
    Boolean batchDeleteRecords(List<Integer> recordIds);
}
