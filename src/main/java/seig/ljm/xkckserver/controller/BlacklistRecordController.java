package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.BlacklistRecord;
import seig.ljm.xkckserver.service.BlacklistRecordService;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * 黑名单记录控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/blacklist")
@Tag(name = "BlacklistRecord", description = "黑名单记录")
public class BlacklistRecordController {

    private BlacklistRecordService blacklistRecordService;
    @Autowired
    public BlacklistRecordController(BlacklistRecordService blacklistRecordService){
        this.blacklistRecordService = blacklistRecordService;
    }

    @PostMapping
    @Operation(summary = "添加黑名单", description = "将访客添加到黑名单")
    public ApiResult<BlacklistRecord> addToBlacklist(@RequestBody BlacklistRecord record) {
        return ApiResult.success(blacklistRecordService.addToBlacklist(record));
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "移除黑名单", description = "将访客从黑名单中移除")
    public ApiResult<Boolean> removeFromBlacklist(
            @Parameter(description = "记录ID") @PathVariable Integer recordId) {
        return ApiResult.success(blacklistRecordService.removeFromBlacklist(recordId));
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "获取黑名单记录", description = "获取指定黑名单记录的详细信息")
    public ApiResult<BlacklistRecord> getRecord(
            @Parameter(description = "记录ID") @PathVariable Integer recordId) {
        return ApiResult.success(blacklistRecordService.getById(recordId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询黑名单", description = "支持多条件分页查询黑名单记录")
    public ApiResult<IPage<BlacklistRecord>> getRecordPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "访客ID") @RequestParam(required = false) Integer visitorId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(blacklistRecordService.getRecordPage(current, size, visitorId, startTime, endTime));
    }

    @GetMapping("/visitor/{visitorId}")
    @Operation(summary = "获取访客黑名单记录", description = "获取指定访客的所有黑名单记录")
    public ApiResult<BlacklistRecord> getVisitorRecord(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId) {
        return ApiResult.success(blacklistRecordService.getVisitorRecord(visitorId));
    }

    @GetMapping("/check/{visitorId}")
    @Operation(summary = "检查黑名单状态", description = "检查访客是否在黑名单中")
    public ApiResult<Boolean> checkBlacklistStatus(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId) {
        return ApiResult.success(blacklistRecordService.isInBlacklist(visitorId));
    }
}
