package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.RFIDCard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface RFIDCardService extends IService<RFIDCard> {
    
    /**
     * 根据UID查询RFID卡
     */
    RFIDCard getCardByUid(String uid);
    
    /**
     * 获取预约关联的RFID卡
     */
    RFIDCard getCardByReservation(Integer reservationId);
    
    /**
     * 分页查询RFID卡
     */
    Page<RFIDCard> getCardPage(Integer pageNum, Integer pageSize, String status, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取RFID卡使用统计
     */
    Map<String, Object> getCardStats(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取过期未归还的RFID卡
     */
    List<RFIDCard> getExpiredCards();
    
    /**
     * 更新RFID卡状态
     */
    boolean updateCardStatus(Integer cardId, String status, Integer adminId);
    
    /**
     * 延长RFID卡有效期
     */
    boolean extendCardExpiration(Integer cardId, LocalDateTime newExpirationTime, Integer adminId);
}
