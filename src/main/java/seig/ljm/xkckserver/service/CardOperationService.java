package seig.ljm.xkckserver.service;

import seig.ljm.xkckserver.entity.Reservation;
import java.util.concurrent.CompletableFuture;

public interface CardOperationService {
    CompletableFuture<Boolean> processCardOperation(String redisKey, Reservation reservation);
} 