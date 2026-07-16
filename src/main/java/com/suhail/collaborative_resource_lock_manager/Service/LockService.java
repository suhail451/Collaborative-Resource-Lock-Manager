package com.suhail.collaborative_resource_lock_manager.Service;

import com.suhail.collaborative_resource_lock_manager.Exception.InvalidLockRequestException;
import com.suhail.collaborative_resource_lock_manager.Exception.LockOwnershipException;
import com.suhail.collaborative_resource_lock_manager.Exception.NoActiveLockException;
import com.suhail.collaborative_resource_lock_manager.Exception.ResourceAlreadyLocked;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {

    public enum LockResult {
        ACQUIRED,
        RENEWED,
        RELEASED
    }

    private final HistoryService historyService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final long LOCK_DURATION = 50;

    public LockService(
            HistoryService historyService,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.historyService = historyService;
        this.redisTemplate = redisTemplate;
    }


    // Acquire Lock
    public LockResult acquireLock(String resourceId, String clientId)
            throws ResourceAlreadyLocked, InvalidLockRequestException {

        validateLockRequest(resourceId, clientId);

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(
                        resourceId,
                        clientId,
                        LOCK_DURATION,
                        TimeUnit.SECONDS
                );

        if (Boolean.TRUE.equals(acquired)) {

            historyService.logEvent(
                    resourceId,
                    clientId,
                    "ACQUIRED",
                    LocalDateTime.now().plusSeconds(LOCK_DURATION)
            );

            return LockResult.ACQUIRED;
        }

        throw new ResourceAlreadyLocked(
                "Resource " + resourceId + " is already locked."
        );
    }


    // Renew Lock
    public LockResult renewLock(String resourceId, String clientId)
            throws NoActiveLockException, LockOwnershipException, InvalidLockRequestException {

        validateLockRequest(resourceId, clientId);

        String keyOwner = redisTemplate.opsForValue()
                .get(resourceId);

        if (keyOwner == null) {
            throw new NoActiveLockException(
                    "No active lock exists on " + resourceId
            );
        }

        if (!keyOwner.equals(clientId)) {
            throw new LockOwnershipException(
                    "You are not the owner of this lock."
            );
        }


        redisTemplate.expire(
                resourceId,
                LOCK_DURATION,
                TimeUnit.SECONDS
        );


        historyService.logEvent(
                resourceId,
                clientId,
                "RENEWED",
                LocalDateTime.now().plusSeconds(LOCK_DURATION)
        );


        return LockResult.RENEWED;
    }



    // Release Lock
    public LockResult releaseLock(String resourceId, String clientId)
            throws NoActiveLockException, LockOwnershipException, InvalidLockRequestException {

        validateLockRequest(resourceId, clientId);

        String currentHolder = redisTemplate.opsForValue()
                .get(resourceId);


        if (currentHolder == null) {
            throw new NoActiveLockException(
                    "No active lock exists on " + resourceId
            );
        }


        if (!currentHolder.equals(clientId)) {
            throw new LockOwnershipException(
                    "You are not the owner of this lock."
            );
        }


        redisTemplate.delete(resourceId);


        historyService.logEvent(
                resourceId,
                clientId,
                "RELEASED",
                null
        );


        return LockResult.RELEASED;
    }



    // Check Lock Status
    public LockStatus checkStatus(String resourceId)
            throws InvalidLockRequestException {

        if(resourceId == null || resourceId.isBlank()){
            throw new InvalidLockRequestException(
                    "Resource ID cannot be empty."
            );
        }


        String currentHolder = redisTemplate.opsForValue()
                .get(resourceId);


        if(currentHolder == null){
            return new LockStatus(false, null, 0);
        }


        Long remainingTime = redisTemplate.getExpire(
                resourceId,
                TimeUnit.SECONDS
        );


        return new LockStatus(
                true,
                currentHolder,
                remainingTime != null ? remainingTime : 0
        );
    }



    // Validation Helper Method
    private void validateLockRequest(String resourceId, String clientId)
            throws InvalidLockRequestException {

        if(resourceId == null || resourceId.isBlank()){
            throw new InvalidLockRequestException(
                    "Resource ID cannot be empty."
            );
        }


        if(clientId == null || clientId.isBlank()){
            throw new InvalidLockRequestException(
                    "Client ID cannot be empty."
            );
        }
    }



    public record LockStatus(
            boolean locked,
            String holder,
            long remainingSeconds
    ) {}

}