package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.RfidCard;
import seig.ljm.xkckserver.entity.RfidCardRecord;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.mapper.RfidCardRecordMapper;
import seig.ljm.xkckserver.service.RfidCardRecordService;
import seig.ljm.xkckserver.service.RfidCardService;
import seig.ljm.xkckserver.service.ReservationService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * RFID卡片使用记录服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfidCardRecordServiceImpl extends ServiceImpl<RfidCardRecordMapper, RfidCardRecord> implements RfidCardRecordService {

    private final RfidCardRecordMapper rfidCardRecordMapper;
    private final RfidCardService rfidCardService;
    private final ReservationService reservationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCardRecord issueCard(Integer cardId, Integer reservationId, Integer adminId, String remarks) {
        // 获取预约信息
        Reservation reservation = reservationService.getById(reservationId);
        if (reservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 获取卡片信息
        RfidCard card = rfidCardService.getById(cardId);
        if (card == null) {
            throw new RuntimeException("卡片不存在");
        }

        // 检查卡片状态
        if (!"available".equals(card.getStatus())) {
            throw new RuntimeException("卡片状态不可用");
        }

        // 创建发卡记录
        RfidCardRecord record = new RfidCardRecord();
        record.setCardId(cardId);
        record.setReservationId(reservationId);
        record.setAdminId(adminId);
        record.setOperationType("issue");
        record.setIssueTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setExpirationTime(reservation.getEndTime());
        record.setRemarks(remarks);
        record.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setHidden(false);

        // 更新卡片状态
        card.setStatus("issued");
        rfidCardService.updateById(card);

        // 保存记录
        save(record);
        
        log.info("Issued card {} to reservation {}", cardId, reservationId);
        
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCardRecord returnCard(Integer cardId, Integer adminId, String remarks) {
        // 获取卡片信息
        RfidCard card = rfidCardService.getById(cardId);
        if (card == null) {
            throw new RuntimeException("卡片不存在");
        }

        // 检查卡片状态
        if (!"issued".equals(card.getStatus())) {
            throw new RuntimeException("卡片未处于发放状态");
        }

        // 获取最新的发卡记录
        RfidCardRecord latestRecord = getLatestCardRecord(cardId);
        if (latestRecord == null) {
            throw new RuntimeException("找不到发卡记录");
        }

        // 创建还卡记录
        RfidCardRecord record = new RfidCardRecord();
        record.setCardId(cardId);
        record.setReservationId(latestRecord.getReservationId());
        record.setAdminId(adminId);
        record.setOperationType("return");
        record.setReturnTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setRemarks(remarks);
        record.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setHidden(false);

        // 更新卡片状态
        card.setStatus("available");
        rfidCardService.updateById(card);

        // 保存记录
        save(record);
        
        log.info("Returned card {}", cardId);
        
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCardRecord recordLost(Integer cardId, Integer reservationId, Integer adminId, String remarks) {
        // 创建挂失记录
        RfidCardRecord record = new RfidCardRecord();
        record.setCardId(cardId);
        record.setReservationId(reservationId);
        record.setAdminId(adminId);
        record.setOperationType("lost");
        record.setRemarks(remarks);
        record.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setHidden(false);

        // 保存记录
        save(record);

        // 更新卡片状态为挂失
        rfidCardService.updateCardStatus(cardId, "lost", remarks);

        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCardRecord recordDeactivate(Integer cardId, Integer reservationId, Integer adminId, String remarks) {
        // 创建注销记录
        RfidCardRecord record = new RfidCardRecord();
        record.setCardId(cardId);
        record.setReservationId(reservationId);
        record.setAdminId(adminId);
        record.setOperationType("deactivate");
        record.setRemarks(remarks);
        record.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setHidden(false);

        // 保存记录
        save(record);

        // 更新卡片状态为注销
        rfidCardService.updateCardStatus(cardId, "deactivated", remarks);

        return record;
    }

    @Override
    public IPage<RfidCardRecord> getRecordPage(Integer current, Integer size, Integer cardId,
                                              String operationType, ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<RfidCardRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (cardId != null) {
            wrapper.eq(RfidCardRecord::getCardId, cardId);
        }
        if (operationType != null) {
            wrapper.eq(RfidCardRecord::getOperationType, operationType);
        }
        if (startTime != null) {
            wrapper.ge(RfidCardRecord::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(RfidCardRecord::getCreateTime, endTime);
        }
        
        // 只查询未隐藏的记录
        wrapper.eq(RfidCardRecord::getHidden, false);
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(RfidCardRecord::getCreateTime);
        
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<RfidCardRecord> getCardRecords(Integer cardId) {
        return rfidCardRecordMapper.getCardRecords(cardId);
    }

    @Override
    public List<RfidCardRecord> getReservationRecords(Integer reservationId) {
        return rfidCardRecordMapper.getReservationRecords(reservationId);
    }

    @Override
    public List<RfidCardRecord> getAdminRecords(Integer adminId) {
        return rfidCardRecordMapper.getAdminRecords(adminId);
    }

    @Override
    public List<RfidCardRecord> getTimeRangeRecords(ZonedDateTime startTime, ZonedDateTime endTime) {
        return rfidCardRecordMapper.getTimeRangeRecords(startTime, endTime);
    }

    @Override
    public RfidCardRecord getLatestCardRecord(Integer cardId) {
        LambdaQueryWrapper<RfidCardRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RfidCardRecord::getCardId, cardId)
               .eq(RfidCardRecord::getHidden, false)
               .orderByDesc(RfidCardRecord::getCreateTime)
               .last("LIMIT 1");
        
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRecord(Integer recordId) {
        return rfidCardRecordMapper.softDeleteRecord(recordId) > 0;
    }

    @Override
    public List<RfidCardRecord> getExpiringRecords() {
        ZonedDateTime now = ZonedDateTime.now(TimeZoneConstant.ZONE_ID);
        ZonedDateTime dayLater = now.plusDays(1);
        return rfidCardRecordMapper.getExpiringRecords(now, dayLater);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteRecords(List<Integer> recordIds) {
        return rfidCardRecordMapper.batchSoftDelete(recordIds) > 0;
    }
}
