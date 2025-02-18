package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.entity.QuotaSetting;
import seig.ljm.xkckserver.service.QuotaSettingService;

import java.time.LocalDate;
import java.util.List;

/**
 * 配额设置控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/quotaSetting")
@Tag(name = "QuotaSetting", description = "每日预约配额")
@RequiredArgsConstructor
public class QuotaSettingController {

    private final QuotaSettingService quotaSettingService;

    @GetMapping("/{quotaId}")
    @Operation(summary = "获取配额设置", description = "根据配额ID获取详细信息")
    public ApiResult<QuotaSetting> getQuotaSetting(
            @Parameter(description = "配额ID") @PathVariable Integer quotaId) {
        return ApiResult.success(quotaSettingService.getById(quotaId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询配额", description = "支持多条件分页查询配额设置")
    public ApiResult<IPage<QuotaSetting>> getQuotaPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "是否节假日") @RequestParam(required = false) Boolean isHoliday) {
        return ApiResult.success(quotaSettingService.getQuotaPage(current, size, startDate, endDate, isHoliday));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "获取指定日期配额", description = "获取指定日期的配额设置")
    public ApiResult<QuotaSetting> getDateQuota(
            @Parameter(description = "日期") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResult.success(quotaSettingService.getDateQuota(date));
    }

    @GetMapping("/date-range")
    @Operation(summary = "获取日期范围配额", description = "获取指定日期范围内的配额设置")
    public ApiResult<List<QuotaSetting>> getDateRangeQuotas(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(quotaSettingService.getDateRangeQuotas(startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "添加配额设置", description = "添加新的配额设置")
    public ApiResult<QuotaSetting> addQuotaSetting(@RequestBody QuotaSetting quotaSetting) {
        return ApiResult.success(quotaSettingService.addQuotaSetting(quotaSetting));
    }

    @PutMapping("/{quotaId}")
    @Operation(summary = "更新配额设置", description = "更新指定配额设置的信息")
    public ApiResult<QuotaSetting> updateQuotaSetting(
            @Parameter(description = "配额ID") @PathVariable Integer quotaId,
            @RequestBody QuotaSetting quotaSetting) {
        quotaSetting.setQuotaId(quotaId);
        return ApiResult.success(quotaSettingService.updateQuotaSetting(quotaSetting));
    }

    @PutMapping("/{quotaId}/increment")
    @Operation(summary = "增加当前预约数", description = "增加指定配额设置的当前预约数")
    public ApiResult<Boolean> incrementCurrentCount(
            @Parameter(description = "配额ID") @PathVariable Integer quotaId) {
        return ApiResult.success(quotaSettingService.incrementCurrentCount(quotaId));
    }

    @PutMapping("/{quotaId}/decrement")
    @Operation(summary = "减少当前预约数", description = "减少指定配额设置的当前预约数")
    public ApiResult<Boolean> decrementCurrentCount(
            @Parameter(description = "配额ID") @PathVariable Integer quotaId) {
        return ApiResult.success(quotaSettingService.decrementCurrentCount(quotaId));
    }

    @GetMapping("/check-available/{date}")
    @Operation(summary = "检查配额可用性", description = "检查指定日期是否还有可用配额")
    public ApiResult<Boolean> checkQuotaAvailable(
            @Parameter(description = "日期") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResult.success(quotaSettingService.checkQuotaAvailable(date));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量设置配额", description = "批量设置日期范围内的配额")
    public ApiResult<Boolean> batchSetQuota(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "最大配额") @RequestParam Integer maxQuota,
            @Parameter(description = "是否节假日") @RequestParam(required = false) Boolean isHoliday,
            @Parameter(description = "特殊事件说明") @RequestParam(required = false) String specialEvent) {
        return ApiResult.success(quotaSettingService.batchSetQuota(startDate, endDate, maxQuota, isHoliday, specialEvent));
    }
}
