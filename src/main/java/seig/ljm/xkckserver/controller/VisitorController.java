package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.Visitor;
import seig.ljm.xkckserver.service.VisitorService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 访客管理控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/visitor")
@Tag(name = "Visitor", description = "访客信息")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/register")
    @Operation(summary = "注册访客", description = "注册新的访客用户")
    public ApiResult<Visitor> register(@RequestBody Visitor visitor) {
        return ApiResult.success(visitorService.register(visitor));
    }

    @PutMapping("/{visitorId}")
    @Operation(summary = "更新访客信息", description = "更新指定访客的信息")
    public ApiResult<Visitor> updateVisitor(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId,
            @RequestBody Visitor visitor) {
        visitor.setVisitorId(visitorId);
        return ApiResult.success(visitorService.updateVisitor(visitor));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询访客", description = "支持按角色和状态筛选的分页查询")
    public ApiResult<IPage<Visitor>> getVisitorPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "角色筛选") @RequestParam(required = false) String role,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        return ApiResult.success(visitorService.getVisitorPage(current, size, role, status));
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "根据手机号查询", description = "根据手机号查询访客信息")
    public ApiResult<Visitor> getByPhone(
            @Parameter(description = "手机号") @PathVariable String phone) {
        return ApiResult.success(visitorService.getByPhone(phone));
    }

    @GetMapping("/id-number/{idNumber}")
    @Operation(summary = "根据证件号查询", description = "根据证件号码查询访客信息")
    public ApiResult<Visitor> getByIdNumber(
            @Parameter(description = "证件号码") @PathVariable String idNumber) {
        return ApiResult.success(visitorService.getByIdNumber(idNumber));
    }

    @GetMapping("/wechat/{openId}")
    @Operation(summary = "根据微信OpenID查询", description = "根据微信OpenID查询访客信息")
    public ApiResult<Visitor> getByWechatOpenId(
            @Parameter(description = "微信OpenID") @PathVariable String openId) {
        return ApiResult.success(visitorService.getByWechatOpenId(openId));
    }

    @PutMapping("/{visitorId}/status")
    @Operation(summary = "更新账号状态", description = "更新指定访客的账号状态")
    public ApiResult<Boolean> updateAccountStatus(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId,
            @Parameter(description = "新状态") @RequestParam String status) {
        return ApiResult.success(visitorService.updateAccountStatus(visitorId, status));
    }

    @PutMapping("/{visitorId}/login-time")
    @Operation(summary = "更新登录时间", description = "更新指定访客的最后登录时间")
    public ApiResult<Boolean> updateLastLoginTime(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId) {
        return ApiResult.success(visitorService.updateLastLoginTime(visitorId, ZonedDateTime.now(TimeZoneConstant.ZONE_ID)));
    }

    @DeleteMapping("/{visitorId}")
    @Operation(summary = "删除访客", description = "软删除指定的访客")
    public ApiResult<Boolean> deleteVisitor(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId) {
        return ApiResult.success(visitorService.deleteVisitor(visitorId));
    }

    @GetMapping("/admins")
    @Operation(summary = "获取管理员列表", description = "获取所有管理员用户列表")
    public ApiResult<List<Visitor>> getAllAdmins() {
        return ApiResult.success(visitorService.getAllAdmins());
    }

    @GetMapping("/blacklist")
    @Operation(summary = "获取黑名单", description = "获取所有黑名单用户列表")
    public ApiResult<List<Visitor>> getAllBlacklisted() {
        return ApiResult.success(visitorService.getAllBlacklisted());
    }
}
