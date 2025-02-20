package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import seig.ljm.xkckserver.entity.Reservation;

import java.time.ZonedDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

/**
 * 预约管理服务接口
 *
 * @author ljm
 * @since 2025-02-18
 */
public interface ReservationService extends IService<Reservation> {
    
    /**
     * 创建新预约
     *
     * @param reservation 预约信息
     * @return 创建的预约记录
     */
    Reservation createReservation(Reservation reservation);
    
    /**
     * 更新预约信息
     *
     * @param reservation 预约信息
     * @return 更新后的预约记录
     */
    Reservation updateReservation(Reservation reservation);
    
    /**
     * 分页查询预约记录
     *
     * @param current 当前页
     * @param size 每页大小
     * @param visitorId 访客ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param status 预约状态（可选）
     * @return 分页结果
     */
    IPage<Reservation> getReservationPage(Integer current, Integer size, Integer visitorId,
                                        ZonedDateTime startTime, ZonedDateTime endTime, String status);
    
    /**
     * 获取访客的所有预约记录
     *
     * @param visitorId 访客ID
     * @return 预约记录列表
     */
    List<Reservation> getVisitorReservations(Integer visitorId);
    
    /**
     * 获取指定时间范围内的预约记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 预约记录列表
     */
    List<Reservation> getTimeRangeReservations(ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 获取待确认的预约记录
     *
     * @return 待确认的预约记录列表
     */
    List<Reservation> getPendingReservations();
    
    /**
     * 更新预约状态
     *
     * @param reservationId 预约ID
     * @param status 新状态
     * @return 是否更新成功
     */
    Boolean updateReservationStatus(Integer reservationId, String status);
    
    /**
     * 更新主人确认状态
     *
     * @param reservationId 预约ID
     * @param hostConfirm 确认状态
     * @return 是否更新成功
     */
    Boolean updateHostConfirm(Integer reservationId, String hostConfirm);
    
    /**
     * 软删除预约记录
     *
     * @param reservationId 预约ID
     * @return 是否删除成功
     */
    Boolean deleteReservation(Integer reservationId);
    
    /**
     * 获取指定部门的预约记录
     *
     * @param department 部门名称
     * @return 预约记录列表
     */
    List<Reservation> getDepartmentReservations(String department);
    
    /**
     * 获取即将开始的预约
     *
     * @return 即将开始的预约列表
     */
    List<Reservation> getUpcomingReservations();
    
    /**
     * 检查预约时间是否可用
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否可用
     */
    Boolean checkTimeAvailable(ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 管理员分页查询所有预约（包括隐藏的）
     *
     * @param current    页码
     * @param size       每页大小
     * @param visitorId  访客ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param status     状态
     * @return 预约分页列表
     */
    IPage<Reservation> getAdminReservationPage(Integer current, Integer size, Integer visitorId, ZonedDateTime startTime, ZonedDateTime endTime, String status);

    /**
     * 管理员查询访客的所有预约（包括隐藏的）
     *
     * @param visitorId 访客ID
     * @return 预约列表
     */
    List<Reservation> getAdminVisitorReservations(Integer visitorId);

    /**
     * 管理员查询时间范围内的所有预约（包括隐藏的）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 预约列表
     */
    List<Reservation> getAdminTimeRangeReservations(ZonedDateTime startTime, ZonedDateTime endTime);

    /**
     * 管理员查询部门的所有预约（包括隐藏的）
     *
     * @param department 部门名称
     * @return 预约列表
     */
    List<Reservation> getAdminDepartmentReservations(String department);

    /**
     * 恢复被隐藏的预约记录
     *
     * @param reservationId 预约ID
     * @return 是否成功
     */
    Boolean restoreReservation(Integer reservationId);

    /**
     * 获取预约列表
     * @param status 预约状态筛选
     * @param date 日期筛选
     * @return 预约列表
     */
    List<Reservation> listReservations(String status, LocalDate date);

    /**
     * 更新预约状态
     * @param reservationId 预约ID
     * @param status 新状态
     */
    void updateStatus(Integer reservationId, String status);

    /**
     * 获取待审核预约数量
     * @param date 指定日期（可选）
     * @return 统计结果
     */
    Map<String, Object> getPendingReservationCount(LocalDate date);

    /**
     * 获取预约统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    Map<String, Object> getReservationStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 管理员修改预约信息
     * @param reservation 预约信息
     * @param adminId 操作管理员ID
     * @return 更新后的预约信息
     */
    Reservation updateAdminReservation(Reservation reservation, Integer adminId);
}
