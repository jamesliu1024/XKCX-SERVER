package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.RFIDCard;
import seig.ljm.xkckserver.mapper.RFIDCardMapper;
import seig.ljm.xkckserver.service.RFIDCardService;

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
public class RFIDCardServiceImpl extends ServiceImpl<RFIDCardMapper, RFIDCard> implements RFIDCardService {

    @Autowired
    private RFIDCardMapper rfidCardMapper;

    @Override
    public RFIDCard getCardByUid(String uid) {
        return rfidCardMapper.selectByUid(uid);
    }

    @Override
    public RFIDCard getCardByReservation(Integer reservationId) {
        return rfidCardMapper.selectByReservationId(reservationId);
    }

    @Override
    public Page<RFIDCard> getCardPage(Integer pageNum, Integer pageSize, String status, 
                                    LocalDateTime startTime, LocalDateTime endTime) {
        Page<RFIDCard> page = new Page<>(pageNum, pageSize);
        QueryWrapper<RFIDCard> queryWrapper = new QueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        if (startTime != null && endTime != null) {
            queryWrapper.between("issue_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("issue_time");
        return rfidCardMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Map<String, Object> getCardStats(LocalDateTime startTime, LocalDateTime endTime) {
        return rfidCardMapper.selectCardStats(startTime, endTime);
    }

    @Override
    public List<RFIDCard> getExpiredCards() {
        return rfidCardMapper.selectExpiredCards(LocalDateTime.now());
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
        card.setExpireTime(newExpirationTime);
        card.setLastAdminId(adminId);
        card.setUpdateTime(LocalDateTime.now());
        return updateById(card);
    }
}
