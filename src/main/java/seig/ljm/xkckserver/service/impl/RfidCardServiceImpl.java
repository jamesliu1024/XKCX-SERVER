package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.RfidCard;
import seig.ljm.xkckserver.mapper.RfidCardMapper;
import seig.ljm.xkckserver.service.RfidCardService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * RFID卡片服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Service
@RequiredArgsConstructor
public class RfidCardServiceImpl extends ServiceImpl<RfidCardMapper, RfidCard> implements RfidCardService {

    private final RfidCardMapper rfidCardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCard addCard(RfidCard rfidCard) {
        // 检查UID是否已存在
        RfidCard existingCard = rfidCardMapper.selectByUid(rfidCard.getUid());
        if (existingCard != null) {
            throw new RuntimeException("卡片UID已存在");
        }

        // 设置初始状态
        rfidCard.setStatus("available");
        rfidCard.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        
        // 保存卡片
        save(rfidCard);
        return rfidCard;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RfidCard updateCard(RfidCard rfidCard) {
        // 检查卡片是否存在
        RfidCard existingCard = getById(rfidCard.getCardId());
        if (existingCard == null) {
            throw new RuntimeException("卡片不存在");
        }

        // 如果修改了UID，需要检查新UID是否已存在
        if (!existingCard.getUid().equals(rfidCard.getUid())) {
            RfidCard cardWithNewUid = rfidCardMapper.selectByUid(rfidCard.getUid());
            if (cardWithNewUid != null) {
                throw new RuntimeException("新UID已被使用");
            }
        }

        // 更新卡片
        rfidCard.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        updateById(rfidCard);
        return getById(rfidCard.getCardId());
    }

    @Override
    public IPage<RfidCard> getCardPage(Integer current, Integer size, String status) {
        LambdaQueryWrapper<RfidCard> wrapper = new LambdaQueryWrapper<>();
        
        // 添加状态查询条件
        if (status != null) {
            wrapper.eq(RfidCard::getStatus, status);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(RfidCard::getCreateTime);

        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public RfidCard getCardByUid(String uid) {
        return rfidCardMapper.selectByUid(uid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCardStatus(Integer cardId, String status, String remarks) {
        return rfidCardMapper.updateCardStatus(cardId, status, remarks) > 0;
    }

    @Override
    public List<RfidCard> getAvailableCards() {
        return rfidCardMapper.selectAvailableCards();
    }

    @Override
    public List<RfidCard> getIssuedCards() {
        return rfidCardMapper.selectIssuedCards();
    }

    @Override
    public List<RfidCard> getLostCards() {
        return rfidCardMapper.selectLostCards();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateStatus(List<Integer> cardIds, String status) {
        return rfidCardMapper.batchUpdateStatus(cardIds, status) > 0;
    }
}
