package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.entity.Prediction;
import seig.ljm.xkckserver.service.PredictionService;

import java.time.LocalDate;
import java.util.List;

/**
 * 人流量预测控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/prediction")
@Tag(name = "Prediction", description = "人流量预测")
public class PredictionController {

    private PredictionService predictionService;
    @Autowired
    public PredictionController(PredictionService predictionService){
        this.predictionService = predictionService;
    }

    @GetMapping("/{predictionId}")
    @Operation(summary = "获取预测详情", description = "根据预测ID获取详细信息")
    public ApiResult<Prediction> getPrediction(
            @Parameter(description = "预测ID") @PathVariable Integer predictionId) {
        return ApiResult.success(predictionService.getById(predictionId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预测", description = "支持多条件分页查询预测数据")
    public ApiResult<IPage<Prediction>> getPredictionPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "最小准确度") @RequestParam(required = false) Double minAccuracy,
            @Parameter(description = "最小置信度") @RequestParam(required = false) Double minConfidence) {
        return ApiResult.success(predictionService.getPredictionPage(current, size, startDate, endDate, minAccuracy, minConfidence));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "获取指定日期预测", description = "获取指定日期的预测数据")
    public ApiResult<Prediction> getDatePrediction(
            @Parameter(description = "预测日期") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResult.success(predictionService.getDatePrediction(date));
    }

    @GetMapping("/date-range")
    @Operation(summary = "获取日期范围预测", description = "获取指定日期范围内的预测数据")
    public ApiResult<List<Prediction>> getDateRangePredictions(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(predictionService.getDateRangePredictions(startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "添加预测", description = "添加新的预测数据")
    public ApiResult<Prediction> addPrediction(@RequestBody Prediction prediction) {
        return ApiResult.success(predictionService.addPrediction(prediction));
    }

    @PutMapping("/{predictionId}/actual")
    @Operation(summary = "更新实际人数", description = "更新预测数据的实际人数，用于计算准确度")
    public ApiResult<Prediction> updateActualCount(
            @Parameter(description = "预测ID") @PathVariable Integer predictionId,
            @Parameter(description = "实际人数") @RequestParam Integer actualCount) {
        return ApiResult.success(predictionService.updateActualCount(predictionId, actualCount));
    }

    @GetMapping("/accuracy-stats")
    @Operation(summary = "获取准确度统计", description = "获取预测模型的准确度统计数据")
    public ApiResult<Object> getAccuracyStats(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(predictionService.getAccuracyStats(startDate, endDate));
    }
}
