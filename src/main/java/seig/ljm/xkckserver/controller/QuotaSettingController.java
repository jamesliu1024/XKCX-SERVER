package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.entity.QuotaSetting;
import seig.ljm.xkckserver.service.QuotaSettingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/quotaSetting")
@Tag(name = "配额管理", description = "日期配额设置相关接口")
public class QuotaSettingController {

    @Autowired
    private QuotaSettingService quotaSettingService;

    @PostMapping
    @Operation(summary = "新增日期配额设置")
    public ResponseEntity<QuotaSetting> addQuota(@RequestBody QuotaSetting quotaSetting) {
        quotaSettingService.save(quotaSetting);
        return ResponseEntity.ok(quotaSetting);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除日期配额设置")
    public ResponseEntity<Boolean> deleteQuota(
            @Parameter(description = "配额设置ID") 
            @PathVariable Integer id) {
        boolean success = quotaSettingService.removeById(id);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新日期配额设置")
    public ResponseEntity<Boolean> updateQuota(
            @Parameter(description = "配额设置ID") 
            @PathVariable Integer id,
            @RequestBody QuotaSetting quotaSetting) {
        quotaSetting.setQuotaId(id);
        boolean success = quotaSettingService.updateById(quotaSetting);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取单个配额设置")
    public ResponseEntity<QuotaSetting> getQuota(
            @Parameter(description = "配额设置ID") 
            @PathVariable Integer id) {
        QuotaSetting quotaSetting = quotaSettingService.getById(id);
        return ResponseEntity.ok(quotaSetting);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "获取指定日期的配额设置")
    public ResponseEntity<QuotaSetting> getQuotaByDate(
            @Parameter(description = "日期 (格式: yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        QuotaSetting quota = quotaSettingService.getQuotaByDate(date);
        return ResponseEntity.ok(quota);
    }

    @GetMapping("/available/{date}")
    @Operation(summary = "检查指定日期是否还有剩余配额")
    public ResponseEntity<Boolean> checkQuotaAvailable(
            @Parameter(description = "日期 (格式: yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        boolean available = quotaSettingService.isQuotaAvailable(date);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/list")
    @Operation(summary = "获取日期范围内的配额设置列表")
    public ResponseEntity<List<QuotaSetting>> getQuotaList(
            @Parameter(description = "开始日期 (格式: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (格式: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<QuotaSetting> quotas = quotaSettingService.getQuotasByDateRange(startDate, endDate);
        return ResponseEntity.ok(quotas);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取配额设置")
    public ResponseEntity<Page<QuotaSetting>> getQuotaPage(
            @Parameter(description = "页码") 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "开始日期 (格式: yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (格式: yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Page<QuotaSetting> quotas = quotaSettingService.getQuotaPage(pageNum, pageSize, startDate, endDate);
        return ResponseEntity.ok(quotas);
    }
}
