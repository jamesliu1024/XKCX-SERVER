package seig.ljm.xkckserver.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import seig.ljm.xkckserver.entity.RfidCard;
import seig.ljm.xkckserver.entity.RfidCardRecord;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.dto.CardOperationDTO;
import seig.ljm.xkckserver.service.RfidCardRecordService;
import seig.ljm.xkckserver.service.RfidCardService;
import seig.ljm.xkckserver.service.CardOperationService;
import seig.ljm.xkckserver.mqtt.MQTTGateway;
import seig.ljm.xkckserver.service.DelayedMqttService;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CardOperationServiceImpl implements CardOperationService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RfidCardService rfidCardService;
    private final RfidCardRecordService rfidCardRecordService;
    private final MQTTGateway mqttGateway;
    private final DelayedMqttService delayedMqttService;

    public CardOperationServiceImpl(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            RfidCardService rfidCardService,
            RfidCardRecordService rfidCardRecordService,
            MQTTGateway mqttGateway,
            DelayedMqttService delayedMqttService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.rfidCardService = rfidCardService;
        this.rfidCardRecordService = rfidCardRecordService;
        this.mqttGateway = mqttGateway;
        this.delayedMqttService = delayedMqttService;
    }

    private void sendCardOperationMessage(String deviceId, String uid, String operationType) {
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            String message = String.format("quere|%s|%s|%s|%d", deviceId, uid, operationType, timestamp);
            String topic = "xkck/device/" + deviceId + "/command";
            delayedMqttService.sendDelayedMessage(topic, message, 2000);
            log.info("Sent card operation message: {} to device: {}", message, deviceId);
        } catch (Exception e) {
            log.error("Error sending card operation message: ", e);
        }
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
                        // 先验证卡片是否存在
                        RfidCard card = rfidCardService.getCardByUid(data.getUid());
                        
                        // 执行卡片操作
                        boolean success = false;
                        if ("add".equals(data.getOperationType())) {
                            if (card != null) {
                                log.error("Card already exists in database: {}", data.getUid());
                                redisTemplate.delete(redisKey);
                                return CompletableFuture.completedFuture(false);
                            }
                            success = processAddCard(data);
                            if (success) {
                                sendCardOperationMessage(data.getDeviceId(), data.getUid(), "added");
                            }
                        } else {
                            if (card == null) {
                                log.error("Card not found in database: {}", data.getUid());
                                redisTemplate.delete(redisKey);
                                return CompletableFuture.completedFuture(false);
                            }

                            if ("issue".equals(data.getOperationType())) {
                                // 验证卡片状态是否为可用
                                if (!"available".equals(card.getStatus())) {
                                    log.error("Card is not available for issue: {}, current status: {}", data.getUid(), card.getStatus());
                                    redisTemplate.delete(redisKey);
                                    return CompletableFuture.completedFuture(false);
                                }
                                success = processIssueCard(card, data, reservation);
                                if (success) {
                                    sendCardOperationMessage(data.getDeviceId(), data.getUid(), "issued");
                                }
                            } else if ("return".equals(data.getOperationType())) {
                                // 验证卡片状态是否为已发放
                                if (!"issued".equals(card.getStatus())) {
                                    log.error("Card is not in issued status for return: {}, current status: {}", data.getUid(), card.getStatus());
                                    redisTemplate.delete(redisKey);
                                    return CompletableFuture.completedFuture(false);
                                }
                                success = processReturnCard(card, data);
                                if (success) {
                                    sendCardOperationMessage(data.getDeviceId(), data.getUid(), "returned");
                                }
                            }
                        }
                        // 操作成功后删除Redis数据
                        if (success) {
                            redisTemplate.delete(redisKey);
                            return CompletableFuture.completedFuture(true);
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

    private boolean processAddCard(CardOperationDTO data) {
        try {
            // 创建新卡片
            RfidCard newCard = new RfidCard();
            newCard.setUid(data.getUid());
            newCard.setStatus("available");
            newCard.setRemarks(data.getRemarks());
            rfidCardService.save(newCard);

            log.info("New card added successfully: {}", data.getUid());
            return true;
        } catch (Exception e) {
            log.error("Error processing add card: ", e);
            return false;
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