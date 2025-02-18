package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.RfidCard;

/**
 * RFID卡片Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface RfidCardMapper extends BaseMapper<RfidCard> {

    /**
     * 根据UID查询卡片
     */
    @Select("SELECT * FROM rfid_card WHERE uid = #{uid}")
    RfidCard selectByUid(@Param("uid") String uid);

    /**
     * 更新卡片状态
     */
    @Update("UPDATE rfid_card SET status = #{status}, update_time = NOW(), remarks = #{remarks} WHERE card_id = #{cardId}")
    int updateCardStatus(@Param("cardId") Integer cardId, @Param("status") String status, @Param("remarks") String remarks);

    /**
     * 获取所有可用卡片
     */
    @Select("SELECT * FROM rfid_card WHERE status = 'available'")
    java.util.List<RfidCard> selectAvailableCards();

    /**
     * 获取所有已发放卡片
     */
    @Select("SELECT * FROM rfid_card WHERE status = 'issued'")
    java.util.List<RfidCard> selectIssuedCards();

    /**
     * 获取所有挂失卡片
     */
    @Select("SELECT * FROM rfid_card WHERE status = 'lost'")
    java.util.List<RfidCard> selectLostCards();

    /**
     * 批量更新卡片状态
     */
    @Update("<script>" +
            "UPDATE rfid_card SET status = #{status}, update_time = NOW() " +
            "WHERE card_id IN " +
            "<foreach collection='cardIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("cardIds") java.util.List<Integer> cardIds, @Param("status") String status);
}

