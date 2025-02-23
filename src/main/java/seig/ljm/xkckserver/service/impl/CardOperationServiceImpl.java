package seig.ljm.xkckserver.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.RfidCard;
import seig.ljm.xkckserver.entity.RfidCardRecord;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.mqtt.dto.CardOperationDTO;
import seig.ljm.xkckserver.service.RfidCardRecordService;
import seig.ljm.xkckserver.service.RfidCardService;
import seig.ljm.xkckserver.service.CardOperationService;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CardOperationServiceImpl implements CardOperationService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RfidCardService rfidCardService;
    private final RfidCardRecordService rfidCardRecordService;

    public CardOperationServiceImpl(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            RfidCardService rfidCardService,
            RfidCardRecordService rfidCardRecordService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.rfidCardService = rfidCardService;
        this.rfidCardRecordService = rfidCardRecordService;
    }

    @Async("cardOperationExecutor")
    public CompletableFuture<Boolean> processCardOperation(String redisKey, Reservation reservation) {
        try {
            int attempts = 12; // 1分钟内每5秒检查一次
            while (attempts > 0) {
                Object redisData = redisTemplate.opsForValue().get(redisKey);
                if (redisData != null) {
                    CardOperationDTO data = objectMapper.convertValue(redisData, CardOperationDTO.class);
                    if (data.getUid() != null) {
                        // 执行卡片操作
                        RfidCard card = rfidCardService.getCardByUid(data.getUid());
                        if (card != null) {
                            boolean success = false;
                            if ("issue".equals(data.getOperationType())) {
                                success = processIssueCard(card, data, reservation);
                            } else if ("return".equals(data.getOperationType())) {
                                success = processReturnCard(card, data);
                            }
                            // 操作成功后删除Redis数据
                            if (success) {
                                redisTemplate.delete(redisKey);
                                return CompletableFuture.completedFuture(true);
                            }
                        }
                    }
                }
                attempts--;
                Thread.sleep(5000);
            }
            
            // 一分钟后还未处理，删除Redis数据
            redisTemplate.delete(redisKey);
            log.warn("Card operation timeout for key: {}", redisKey);
            return CompletableFuture.completedFuture(false);
            
        } catch (Exception e) {
            log.error("Error processing card operation: ", e);
            redisTemplate.delete(redisKey);
            return CompletableFuture.completedFuture(false);
        }
    }

    private boolean processIssueCard(RfidCard card, CardOperationDTO data, Reservation reservation) {
        try {
            // 更新卡片状态
            card.setStatus("issued");
            rfidCardService.updateById(card);

            // 创建发卡记录
            RfidCardRecord record = new RfidCardRecord();
            record.setCardId(card.getCardId());
            record.setReservationId(data.getReservationId());
            record.setAdminId(data.getAdminId());
            record.setOperationType("issue");
            record.setIssueTime(ZonedDateTime.now());
            record.setExpirationTime(reservation.getEndTime());
            rfidCardRecordService.save(record);

            log.info("Card issued successfully: {}", data.getUid());
            return true;
        } catch (Exception e) {
            log.error("Error processing issue card: ", e);
            return false;
        }
    }

    private boolean processReturnCard(RfidCard card, CardOperationDTO data) {
        try {
            // 更新卡片状态
            card.setStatus("available");
            rfidCardService.updateById(card);

            // 创建还卡记录
            RfidCardRecord record = new RfidCardRecord();
            record.setCardId(card.getCardId());
            record.setReservationId(data.getReservationId());
            record.setAdminId(data.getAdminId());
            record.setOperationType("return");
            record.setReturnTime(ZonedDateTime.now());
            rfidCardRecordService.save(record);

            log.info("Card returned successfully: {}", data.getUid());
            return true;
        } catch (Exception e) {
            log.error("Error processing return card: ", e);
            return false;
        }
    }
} 