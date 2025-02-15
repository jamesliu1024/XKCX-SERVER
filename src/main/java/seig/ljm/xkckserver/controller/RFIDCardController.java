package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.entity.RFIDCard;
import seig.ljm.xkckserver.service.RFIDCardService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@RestController
@RequestMapping("/rfid-card")
@Tag(name = "RFID卡管理", description = "RFID卡相关接口")
public class RFIDCardController {

    @Autowired
    private RFIDCardService rfidCardService;

    @PostMapping("/issue")
    @Operation(summary = "发放RFID卡")
    public ResponseEntity<RFIDCard> issueCard(
            @Parameter(description = "RFID卡信息") 
            @RequestBody RFIDCard rfidCard) {
        rfidCard.setIssueTime(LocalDateTime.now());
        rfidCard.setStatus("issued");
        rfidCardService.save(rfidCard);
        return ResponseEntity.ok(rfidCard);
    }

    @PutMapping("/{cardId}/return")
    @Operation(summary = "归还RFID卡")
    public ResponseEntity<Boolean> returnCard(
            @Parameter(description = "RFID卡ID") 
            @PathVariable Integer cardId,
            @Parameter(description = "管理员ID") 
            @RequestParam Integer adminId) {
        RFIDCard rfidCard = new RFIDCard();
        rfidCard.setCardId(cardId);
        rfidCard.setReturnTime(LocalDateTime.now());
        rfidCard.setStatus("returned");
        rfidCard.setLastAdminId(adminId);
        boolean success = rfidCardService.updateById(rfidCard);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/{cardId}")
    @Operation(summary = "获取RFID卡详情")
    public ResponseEntity<RFIDCard> getCard(
            @Parameter(description = "RFID卡ID") 
            @PathVariable Integer cardId) {
        RFIDCard rfidCard = rfidCardService.getById(cardId);
        return ResponseEntity.ok(rfidCard);
    }

    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据UID查询RFID卡")
    public ResponseEntity<RFIDCard> getCardByUid(
            @Parameter(description = "RFID卡UID") 
            @PathVariable String uid) {
        RFIDCard rfidCard = rfidCardService.getCardByUid(uid);
        return ResponseEntity.ok(rfidCard);
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "获取预约关联的RFID卡")
    public ResponseEntity<RFIDCard> getCardByReservation(
            @Parameter(description = "预约ID") 
            @PathVariable Integer reservationId) {
        RFIDCard rfidCard = rfidCardService.getCardByReservation(reservationId);
        return ResponseEntity.ok(rfidCard);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询RFID卡")
    public ResponseEntity<Page<RFIDCard>> getCardPage(
            @Parameter(description = "页码") 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "卡片状态") 
            @RequestParam(required = false) String status,
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Page<RFIDCard> page = rfidCardService.getCardPage(pageNum, pageSize, status, startTime, endTime);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取RFID卡使用统计")
    public ResponseEntity<Map<String, Object>> getCardStats(
            @Parameter(description = "开始时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> stats = rfidCardService.getCardStats(startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/expired")
    @Operation(summary = "获取过期未归还的RFID卡")
    public ResponseEntity<List<RFIDCard>> getExpiredCards() {
        List<RFIDCard> expiredCards = rfidCardService.getExpiredCards();
        return ResponseEntity.ok(expiredCards);
    }

    @PutMapping("/{cardId}/status")
    @Operation(summary = "更新RFID卡状态")
    public ResponseEntity<Boolean> updateCardStatus(
            @Parameter(description = "RFID卡ID") 
            @PathVariable Integer cardId,
            @Parameter(description = "新状态") 
            @RequestParam String status,
            @Parameter(description = "管理员ID") 
            @RequestParam Integer adminId) {
        boolean success = rfidCardService.updateCardStatus(cardId, status, adminId);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{cardId}/extend")
    @Operation(summary = "延长RFID卡有效期")
    public ResponseEntity<Boolean> extendCardExpiration(
            @Parameter(description = "RFID卡ID") 
            @PathVariable Integer cardId,
            @Parameter(description = "新的过期时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime newExpirationTime,
            @Parameter(description = "管理员ID") 
            @RequestParam Integer adminId) {
        boolean success = rfidCardService.extendCardExpiration(cardId, newExpirationTime, adminId);
        return ResponseEntity.ok(success);
    }
}
