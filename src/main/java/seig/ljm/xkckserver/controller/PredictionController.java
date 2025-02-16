package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.entity.Prediction;
import seig.ljm.xkckserver.service.PredictionService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 人流量预测数据管理控制器
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@RestController
@RequestMapping("/prediction")
@Tag(name = "Prediction", description = "人流量预测数据")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping
    @Operation(summary = "新增预测数据")
    public ResponseEntity<Prediction> addPrediction(@RequestBody Prediction prediction) {
        predictionService.save(prediction);
        return ResponseEntity.ok(prediction);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预测数据")
    public ResponseEntity<Boolean> deletePrediction(
            @Parameter(description = "预测记录ID") 
            @PathVariable Integer id) {
        boolean success = predictionService.removeById(id);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新预测数据")
    public ResponseEntity<Boolean> updatePrediction(
            @Parameter(description = "预测记录ID") 
            @PathVariable Integer id,
            @RequestBody Prediction prediction) {
        prediction.setPredictionId(id);
        boolean success = predictionService.updateById(prediction);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取单条预测数据")
    public ResponseEntity<Prediction> getPrediction(
            @Parameter(description = "预测记录ID") 
            @PathVariable Integer id) {
        Prediction prediction = predictionService.getById(id);
        return ResponseEntity.ok(prediction);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "根据日期获取预测数据")
    public ResponseEntity<Prediction> getPredictionByDate(
            @Parameter(description = "预测日期 (格式: yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("predict_date", date);
        Prediction prediction = predictionService.getOne(queryWrapper);
        return ResponseEntity.ok(prediction);
    }

    @GetMapping("/list")
    @Operation(summary = "获取预测数据列表")
    public ResponseEntity<List<Prediction>> getPredictionList(
            @Parameter(description = "开始日期 (格式: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (格式: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("predict_date", startDate, endDate)
                   .orderByAsc("predict_date");
        List<Prediction> predictions = predictionService.list(queryWrapper);
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取预测数据")
    public ResponseEntity<Page<Prediction>> getPredictionPage(
            @Parameter(description = "页码") 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "开始日期 (格式: yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (格式: yyyy-MM-dd)") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        Page<Prediction> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Prediction> queryWrapper = new QueryWrapper<>();
        
        if (startDate != null && endDate != null) {
            queryWrapper.between("predict_date", startDate, endDate);
        }
        queryWrapper.orderByDesc("predict_date");
        
        Page<Prediction> predictions = predictionService.page(page, queryWrapper);
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/accuracy/average")
    @Operation(summary = "获取指定时间段的平均预测准确度")
    public ResponseEntity<Double> getAverageAccuracy(
            @Parameter(description = "开始日期 (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Double avgAccuracy = predictionService.getAverageAccuracy(startDate, endDate);
        return ResponseEntity.ok(avgAccuracy);
    }

    @GetMapping("/model/{version}")
    @Operation(summary = "获取指定模型版本的预测数据")
    public ResponseEntity<List<Prediction>> getPredictionsByModel(
            @Parameter(description = "模型版本") 
            @PathVariable String version) {
        List<Prediction> predictions = predictionService.getPredictionsByModelVersion(version);
        return ResponseEntity.ok(predictions);
    }

    @PutMapping("/{id}/accuracy")
    @Operation(summary = "更新预测准确度和置信度")
    public ResponseEntity<Void> updateAccuracy(
            @Parameter(description = "预测记录ID") 
            @PathVariable Integer id,
            @Parameter(description = "准确度") 
            @RequestParam Double accuracy,
            @Parameter(description = "置信度") 
            @RequestParam Double confidence) {
        predictionService.updatePredictionAccuracy(id, accuracy, confidence);
        return ResponseEntity.ok().build();
    }
}
