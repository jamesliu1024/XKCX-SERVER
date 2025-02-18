package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.Visitor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {

    /**
     * 根据手机号查询访客
     */
    @Select("SELECT * FROM visitor WHERE phone = #{phone} AND hidden = false")
    Visitor selectByPhone(@Param("phone") String phone);

    /**
     * 根据证件号码查询访客
     */
    @Select("SELECT * FROM visitor WHERE id_number = #{idNumber} AND hidden = false")
    Visitor selectByIdNumber(@Param("idNumber") String idNumber);

    /**
     * 根据微信OpenID查询访客
     */
    @Select("SELECT * FROM visitor WHERE wechat_openid = #{openId} AND hidden = false")
    Visitor selectByWechatOpenId(@Param("openId") String openId);

    /**
     * 更新访客状态
     */
    @Update("UPDATE visitor SET account_status = #{status}, update_time = #{updateTime} WHERE visitor_id = #{visitorId}")
    int updateAccountStatus(@Param("visitorId") Integer visitorId, @Param("status") String status, @Param("updateTime") ZonedDateTime updateTime);

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE visitor SET last_login_time = #{loginTime} WHERE visitor_id = #{visitorId}")
    int updateLastLoginTime(@Param("visitorId") Integer visitorId, @Param("loginTime") ZonedDateTime loginTime);

    /**
     * 软删除访客
     */
    @Update("UPDATE visitor SET hidden = true, update_time = #{updateTime} WHERE visitor_id = #{visitorId}")
    int softDelete(@Param("visitorId") Integer visitorId, @Param("updateTime") ZonedDateTime updateTime);

    /**
     * 获取所有管理员
     */
    @Select("SELECT * FROM visitor WHERE role = 'admin' AND hidden = false")
    List<Visitor> selectAllAdmins();

    /**
     * 获取所有黑名单用户
     */
    @Select("SELECT * FROM visitor WHERE account_status = 'blacklist' AND hidden = false")
    List<Visitor> selectAllBlacklisted();
}

