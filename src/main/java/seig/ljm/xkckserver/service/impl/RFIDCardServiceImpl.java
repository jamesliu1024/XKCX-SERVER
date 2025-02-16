package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.entity.RFIDCard;
import seig.ljm.xkckserver.mapper.RFIDCardMapper;
import seig.ljm.xkckserver.service.RFIDCardService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RFIDCardServiceImpl extends ServiceImpl<RFIDCardMapper, RFIDCard> implements RFIDCardService {

    @Override
    public RFIDCard getCardByUid(String uid) {
        return baseMapper.selectByUid(uid);
    }

    @Override
    public RFIDCard getCardByReservation(Integer reservationId) {
        return baseMapper.selectByReservationId(reservationId);
    }

    @Override
    public Page<RFIDCard> getCardPage(Integer pageNum, Integer pageSize, String status, LocalDateTime startTime, LocalDateTime endTime) {
        Page<RFIDCard> page = new Page<>(pageNum, pageSize);
        QueryWrapper<RFIDCard> queryWrapper = new QueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        if (startTime != null && endTime != null) {
            queryWrapper.between("issue_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("issue_time");
        return page(page, queryWrapper);
    }

    @Override
    public Map<String, Object> getCardStats(LocalDateTime startTime, LocalDateTime endTime) {
        return baseMapper.selectCardStats(startTime, endTime);
    }

    @Override
    public List<RFIDCard> getExpiredCards() {
        return baseMapper.selectExpiredCards(LocalDateTime.now());
    }

    @Override
    public boolean updateCardStatus(Integer cardId, String status, Integer adminId) {
        RFIDCard card = new RFIDCard();
        card.setCardId(cardId);
        card.setStatus(status);
        card.setLastAdminId(adminId);
        card.setUpdateTime(LocalDateTime.now());
        return updateById(card);
    }

    @Override
    public boolean extendCardExpiration(Integer cardId, LocalDateTime newExpirationTime, Integer adminId) {
        RFIDCard card = new RFIDCard();
        card.setCardId(cardId);
        card.setExpirationTime(newExpirationTime);
        card.setLastAdminId(adminId);
        card.setUpdateTime(LocalDateTime.now());
        return updateById(card);
    }

    @Override
    public List<Map<String, Object>> getStatusStatistics() {
        return baseMapper.countByStatus();
    }

    @Override
    public List<RFIDCard> getNearExpirationCards() {
        return baseMapper.selectNearExpirationCards();
    }

    @Override
    @Transactional
    public boolean batchUpdateStatus(List<Integer> cardIds, String status, Integer adminId) {
        LocalDateTime now = LocalDateTime.now();
        for (Integer cardId : cardIds) {
            RFIDCard card = new RFIDCard();
            card.setCardId(cardId);
            card.setStatus(status);
            card.setLastAdminId(adminId);
            card.setUpdateTime(now);
            if (!updateById(card)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public RFIDCard issueCard(Integer visitorId, Integer reservationId, Integer adminId, LocalDateTime expirationTime) {
        // 获取一张可用的卡
        RFIDCard availableCard = baseMapper.selectOneAvailableCard();
        if (availableCard == null) {
            return null;
        }

        // 更新卡片信息
        availableCard.setStatus(RFIDCard.STATUS_ISSUED);
        availableCard.setReservationId(reservationId);
        availableCard.setLastAdminId(adminId);
        availableCard.setIssueTime(LocalDateTime.now());
        availableCard.setExpirationTime(expirationTime);
        availableCard.setUpdateTime(LocalDateTime.now());

        // 保存更新
        if (updateById(availableCard)) {
            return availableCard;
        }
        return null;
    }
}
