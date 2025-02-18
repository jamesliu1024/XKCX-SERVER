package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.entity.RfidCard;
import seig.ljm.xkckserver.service.RfidCardService;

import java.util.List;

/**
 * RFID卡片管理控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/rfid-card")
@Tag(name = "RfidCard", description = "RFID卡片")
@RequiredArgsConstructor
public class RfidCardController {

    private final RfidCardService rfidCardService;

    @PostMapping
    @Operation(summary = "添加卡片", description = "添加新的RFID卡片")
    public ApiResult<RfidCard> addCard(@RequestBody RfidCard rfidCard) {
        return ApiResult.success(rfidCardService.addCard(rfidCard));
    }

    @PutMapping("/{cardId}")
    @Operation(summary = "更新卡片", description = "更新指定RFID卡片的信息")
    public ApiResult<RfidCard> updateCard(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId,
            @RequestBody RfidCard rfidCard) {
        rfidCard.setCardId(cardId);
        return ApiResult.success(rfidCardService.updateCard(rfidCard));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询卡片", description = "支持按状态分页查询RFID卡片")
    public ApiResult<IPage<RfidCard>> getCardPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "卡片状态") @RequestParam(required = false) String status) {
        return ApiResult.success(rfidCardService.getCardPage(current, size, status));
    }

    @GetMapping("/uid/{uid}")
    @Operation(summary = "根据UID查询卡片", description = "根据卡片UID查询RFID卡片信息")
    public ApiResult<RfidCard> getCardByUid(
            @Parameter(description = "卡片UID") @PathVariable String uid) {
        return ApiResult.success(rfidCardService.getCardByUid(uid));
    }

    @PutMapping("/{cardId}/status")
    @Operation(summary = "更新卡片状态", description = "更新指定RFID卡片的状态")
    public ApiResult<Boolean> updateCardStatus(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId,
            @Parameter(description = "新状态") @RequestParam String status,
            @Parameter(description = "备注") @RequestParam(required = false) String remarks) {
        return ApiResult.success(rfidCardService.updateCardStatus(cardId, status, remarks));
    }

    @GetMapping("/available")
    @Operation(summary = "获取可用卡片", description = "获取所有可用状态的RFID卡片")
    public ApiResult<List<RfidCard>> getAvailableCards() {
        return ApiResult.success(rfidCardService.getAvailableCards());
    }

    @GetMapping("/issued")
    @Operation(summary = "获取已发放卡片", description = "获取所有已发放状态的RFID卡片")
    public ApiResult<List<RfidCard>> getIssuedCards() {
        return ApiResult.success(rfidCardService.getIssuedCards());
    }

    @GetMapping("/lost")
    @Operation(summary = "获取挂失卡片", description = "获取所有挂失状态的RFID卡片")
    public ApiResult<List<RfidCard>> getLostCards() {
        return ApiResult.success(rfidCardService.getLostCards());
    }

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新状态", description = "批量更新多个RFID卡片的状态")
    public ApiResult<Boolean> batchUpdateStatus(
            @Parameter(description = "卡片ID列表") @RequestParam List<Integer> cardIds,
            @Parameter(description = "新状态") @RequestParam String status) {
        return ApiResult.success(rfidCardService.batchUpdateStatus(cardIds, status));
    }
}
