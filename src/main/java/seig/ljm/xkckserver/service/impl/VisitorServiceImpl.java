package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.common.security.Hash;
import seig.ljm.xkckserver.entity.Visitor;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.entity.AccessLog;
import seig.ljm.xkckserver.mapper.VisitorMapper;
import seig.ljm.xkckserver.service.VisitorService;
import seig.ljm.xkckserver.service.ReservationService;
import seig.ljm.xkckserver.service.AccessLogService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 访客服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    private final VisitorMapper visitorMapper;
    private final ReservationService reservationService;
    private final AccessLogService accessLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Visitor register(Visitor visitor) {
        // 检查手机号是否已注册
        if (getByPhone(visitor.getPhone()) != null) {
            throw new RuntimeException("手机号已注册");
        }

        // 检查证件号码是否已注册
        if (getByIdNumber(visitor.getIdNumber()) != null) {
            throw new RuntimeException("证件号码已注册");
        }

        // 设置默认值
        ZonedDateTime now = ZonedDateTime.now(TimeZoneConstant.ZONE_ID);
        visitor.setRole("visitor");
        visitor.setAccountStatus("normal");
        visitor.setHidden(false);
        visitor.setCreateTime(now);
        visitor.setUpdateTime(now);

        // 保存访客信息
        save(visitor);
        return visitor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Visitor updateVisitor(Visitor visitor) {
        // 检查访客是否存在
        Visitor existingVisitor = getById(visitor.getVisitorId());
        if (existingVisitor == null) {
            throw new RuntimeException("访客不存在");
        }

        // 如果修改了手机号，检查新手机号是否被其他用户使用
        if (!existingVisitor.getPhone().equals(visitor.getPhone())) {
            Visitor phoneUser = getByPhone(visitor.getPhone());
            if (phoneUser != null && !phoneUser.getVisitorId().equals(visitor.getVisitorId())) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
        }

        // 如果修改了证件号码，检查新证件号码是否被其他用户使用
        if (!existingVisitor.getIdNumber().equals(visitor.getIdNumber())) {
            Visitor idNumberUser = getByIdNumber(visitor.getIdNumber());
            if (idNumberUser != null && !idNumberUser.getVisitorId().equals(visitor.getVisitorId())) {
                throw new RuntimeException("证件号码已被其他用户使用");
            }
        }

        // 如果修改了密码，检查新密码是否符合要求
        if (visitor.getPasswordHash() != null && !visitor.getPasswordHash().isEmpty()) {
            visitor.setPasswordHash(Hash.hashPassword(visitor.getPasswordHash()));
        }

        // 更新访客信息
        visitor.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        updateById(visitor);
        return visitor;
    }

    @Override
    public IPage<Visitor> getVisitorPage(Integer current, Integer size, String role, String status) {
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (role != null) {
            wrapper.eq(Visitor::getRole, role);
        }
        if (status != null) {
            wrapper.eq(Visitor::getAccountStatus, status);
        }
        
        // 只查询未隐藏的访客
        wrapper.eq(Visitor::getHidden, false);
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(Visitor::getCreateTime);
        
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public Visitor getByVisitorId(Integer visitorId) {
        return getById(visitorId);
    }

    @Override
    public Visitor getByPhone(String phone) {
        return visitorMapper.selectByPhone(phone);
    }

    @Override
    public Visitor getByIdNumber(String idNumber) {
        return visitorMapper.selectByIdNumber(idNumber);
    }

    @Override
    public Visitor getByWechatOpenId(String openId) {
        return visitorMapper.selectByWechatOpenId(openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAccountStatus(Integer visitorId, String status) {
        Visitor visitor = getById(visitorId);
        if (visitor != null) {
            visitor.setAccountStatus(status);
            visitor.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            updateById(visitor);
            
            log.info("Updated visitor {} account status to {}", visitorId, status);
        } else {
            log.warn("Visitor {} not found when updating account status", visitorId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Integer visitorId, ZonedDateTime lastLoginTime) {
        Visitor visitor = getById(visitorId);
        if (visitor != null) {
            visitor.setLastLoginTime(lastLoginTime);
            updateById(visitor);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteVisitor(Integer visitorId) {
        ZonedDateTime now = ZonedDateTime.now(TimeZoneConstant.ZONE_ID);
        return visitorMapper.softDelete(visitorId, now) > 0;
    }

    @Override
    public List<Visitor> getAllAdmins() {
        return visitorMapper.selectAllAdmins();
    }

    @Override
    public List<Visitor> getAllBlacklisted() {
        return baseMapper.selectList(new QueryWrapper<Visitor>().eq("account_status", "blacklisted"));
    }

    @Override
    public Visitor getVisitorByPhone(String phone) {
        return lambdaQuery()
                .eq(Visitor::getPhone, phone)
                .eq(Visitor::getHidden, false)
                .one();
    }

    @Override
    public List<Visitor> listVisitors(String role, String accountStatus) {
        LambdaQueryWrapper<Visitor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Visitor::getHidden, false);
        
        if (role != null && !role.isEmpty()) {
            wrapper.eq(Visitor::getRole, role);
        }
        
        if (accountStatus != null && !accountStatus.isEmpty()) {
            wrapper.eq(Visitor::getAccountStatus, accountStatus);
        }
        
        return list(wrapper);
    }

    @Override
    public Map<String, Object> getVisitorDetail(Integer visitorId, Boolean includeReservations, Boolean includeAccessLogs) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取访客基本信息
            Visitor visitor = getById(visitorId);
            if (visitor == null) {
                throw new RuntimeException("访客不存在");
            }
            
            // 移除敏感信息
            visitor.setPasswordHash(null);
            result.put("visitorInfo", visitor);
            
            // 2. 如果需要，获取预约历史
            if (includeReservations) {
                LambdaQueryWrapper<Reservation> reservationWrapper = new LambdaQueryWrapper<>();
                reservationWrapper.eq(Reservation::getVisitorId, visitorId)
                                .eq(Reservation::getHidden, false)
                                .orderByDesc(Reservation::getCreateTime);
                List<Reservation> reservations = reservationService.list(reservationWrapper);
                
                // 统计预约状态
                Map<String, Long> reservationStats = reservations.stream()
                        .collect(Collectors.groupingBy(
                                Reservation::getStatus,
                                Collectors.counting()
                        ));
                
                result.put("reservations", reservations);
                result.put("reservationStats", reservationStats);
            }
            
            // 3. 如果需要，获取门禁记录
            if (includeAccessLogs) {
                LambdaQueryWrapper<AccessLog> accessLogWrapper = new LambdaQueryWrapper<>();
                accessLogWrapper.eq(AccessLog::getVisitorId, visitorId)
                              .eq(AccessLog::getHidden, false)
                              .orderByDesc(AccessLog::getAccessTime)
                              .last("LIMIT 100"); // 限制最近100条记录
                List<AccessLog> accessLogs = accessLogService.list(accessLogWrapper);
                
                // 统计访问情况
                Map<String, Long> accessTypeStats = accessLogs.stream()
                        .collect(Collectors.groupingBy(
                                AccessLog::getAccessType,
                                Collectors.counting()
                        ));
                
                Map<String, Long> accessResultStats = accessLogs.stream()
                        .collect(Collectors.groupingBy(
                                AccessLog::getResult,
                                Collectors.counting()
                        ));
                
                result.put("accessLogs", accessLogs);
                result.put("accessTypeStats", accessTypeStats);
                result.put("accessResultStats", accessResultStats);
            }
            
            result.put("success", true);
            result.put("message", "获取访客详情成功");
            
        } catch (Exception e) {
            log.error("获取访客详情失败", e);
            result.put("success", false);
            result.put("message", "获取访客详情失败：" + e.getMessage());
        }
        
        return result;
    }
}
