package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.Visitor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访客服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface VisitorService extends IService<Visitor> {

    /**
     * 注册新访客
     *
     * @param visitor 访客信息
     * @return 注册成功的访客信息
     */
    Visitor register(Visitor visitor);

    /**
     * 更新访客信息
     *
     * @param visitor 访客信息
     * @return 更新后的访客信息
     */
    Visitor updateVisitor(Visitor visitor);

    /**
     * 分页查询访客列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @param role    角色筛选（可选）
     * @param status  状态筛选（可选）
     * @return 分页结果
     */
    IPage<Visitor> getVisitorPage(Integer current, Integer size, String role, String status);


    /**
     * 根据访客ID查询访客
     *
     * @param visitorId 访客ID
     * @return 访客信息
     */
    Visitor getByVisitorId(Integer visitorId);

    /**
     * 根据手机号查询访客
     *
     * @param phone 手机号
     * @return 访客信息
     */
    Visitor getByPhone(String phone);

    /**
     * 根据证件号码查询访客
     *
     * @param idNumber 证件号码
     * @return 访客信息
     */
    Visitor getByIdNumber(String idNumber);

    /**
     * 根据微信OpenID查询访客
     *
     * @param openId 微信OpenID
     * @return 访客信息
     */
    Visitor getByWechatOpenId(String openId);



    /**
     * 软删除访客
     *
     * @param visitorId 访客ID
     * @return 是否删除成功
     */
    Boolean deleteVisitor(Integer visitorId);

    /**
     * 获取所有管理员列表
     *
     * @return 管理员列表
     */
    List<Visitor> getAllAdmins();

    /**
     * 获取所有黑名单用户
     *
     * @return 黑名单用户列表
     */
    List<Visitor> getAllBlacklisted();

    /**
     * 根据手机号查找访客
     * @param phone 手机号
     * @return 访客信息
     */
    Visitor getVisitorByPhone(String phone);

    /**
     * 更新最后登录时间
     * @param visitorId 访客ID
     * @param lastLoginTime 最后登录时间
     */
    void updateLastLoginTime(Integer visitorId, ZonedDateTime lastLoginTime);

    /**
     * 获取访客列表
     * @param role 角色筛选
     * @param accountStatus 账号状态筛选
     * @return 访客列表
     */
    List<Visitor> listVisitors(String role, String accountStatus);

    /**
     * 更新访客账号状态
     * @param visitorId 访客ID
     * @param status 新状态
     */
    void updateAccountStatus(Integer visitorId, String status);

    /**
     * 获取访客详细信息
     * @param visitorId 访客ID
     * @param includeReservations 是否包含预约历史
     * @param includeAccessLogs 是否包含门禁记录
     * @return 访客详细信息
     */
    Map<String, Object> getVisitorDetail(Integer visitorId, Boolean includeReservations, Boolean includeAccessLogs);
}
