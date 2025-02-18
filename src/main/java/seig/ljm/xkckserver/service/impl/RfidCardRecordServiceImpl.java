package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.RfidCardRecord;
import seig.ljm.xkckserver.mapper.RfidCardRecordMapper;
import seig.ljm.xkckserver.service.RfidCardRecordService;
import seig.ljm.xkckserver.service.RfidCardService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * RFID卡片使用记录服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
@RequiredArgsConstructor
public class RfidCardRecordServiceImpl extends ServiceImpl<RfidCardRecordMapper, RfidCardRecord> implements RfidCardRecordService {

    private final RfidCardRecordMapper rfidCardRecordMapper;
    private final RfidCardService rfidCardService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCardRecord recordIssue(Integer cardId, Integer reservationId, Integer adminId, 
                                    ZonedDateTime expirationTime, String remarks) {
        // 创建发卡记录
        RfidCardRecord record = new RfidCardRecord();
        record.setCardId(cardId);
        record.setReservationId(reservationId);
        record.setAdminId(adminId);
        record.setOperationType("issue");
        record.setExpirationTime(expirationTime);
        record.setRemarks(remarks);
        record.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setHidden(false);

        // 保存记录
        save(record);

        // 更新卡片状态为已发放
        rfidCardService.updateCardStatus(cardId, "issued", remarks);

        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCardRecord recordReturn(Integer cardId, Integer reservationId, Integer adminId, String remarks) {
        // 创建还卡记录
        RfidCardRecord record = new RfidCardRecord();
        record.setCardId(cardId);
        record.setReservationId(reservationId);
        record.setAdminId(adminId);
        record.setOperationType("return");
        record.setReturnTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setRemarks(remarks);
        record.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        record.setHidden(false);

        // 保存记录
        save(record);

        // 更新卡片状态为可用
        rfidCardService.updateCardStatus(cardId, "available", remarks);

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
        return rfidCardRecordMapper.getLatestCardRecord(cardId);
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
