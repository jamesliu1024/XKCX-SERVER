package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.RfidCard;

import java.util.List;

/**
 * RFID卡片服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface RfidCardService extends IService<RfidCard> {

    /**
     * 添加新卡片
     *
     * @param rfidCard 卡片信息
     * @return 添加的卡片信息
     */
    RfidCard addCard(RfidCard rfidCard);

    /**
     * 更新卡片信息
     *
     * @param rfidCard 卡片信息
     * @return 更新后的卡片信息
     */
    RfidCard updateCard(RfidCard rfidCard);

    /**
     * 分页查询卡片
     *
     * @param current 当前页
     * @param size    每页大小
     * @param status  卡片状态（可选）
     * @return 分页结果
     */
    IPage<RfidCard> getCardPage(Integer current, Integer size, String status);

    /**
     * 根据UID查询卡片
     *
     * @param uid 卡片UID
     * @return 卡片信息
     */
    RfidCard getCardByUid(String uid);

    /**
     * 更新卡片状态
     *
     * @param cardId  卡片ID
     * @param status  新状态
     * @param remarks 备注
     * @return 是否更新成功
     */
    Boolean updateCardStatus(Integer cardId, String status, String remarks);

    /**
     * 获取所有可用卡片
     *
     * @return 可用卡片列表
     */
    List<RfidCard> getAvailableCards();

    /**
     * 获取所有已发放卡片
     *
     * @return 已发放卡片列表
     */
    List<RfidCard> getIssuedCards();

    /**
     * 获取所有挂失卡片
     *
     * @return 挂失卡片列表
     */
    List<RfidCard> getLostCards();

    /**
     * 批量更新卡片状态
     *
     * @param cardIds 卡片ID列表
     * @param status  新状态
     * @return 是否更新成功
     */
    Boolean batchUpdateStatus(List<Integer> cardIds, String status);
}
