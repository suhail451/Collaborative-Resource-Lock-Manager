package com.suhail.collaborative_resource_lock_manager.Service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class LockService  {

    public enum LockResult {
        ACQUIRED,
        ALREADY_HELD,
        NOT_HELD,
        NOT_OWNER,
        RENEW,
        RELEASED

    }

    final HistoryService historyService;
    final RedisTemplate<String,String> redisTemplate;

    public LockService(HistoryService historyService, RedisTemplate<String, String> redisTemplate) {
        this.historyService = historyService;
        this.redisTemplate = redisTemplate;
    }

    public LockResult acquireLock(String resourceId,String clientId){
        Boolean acquired=redisTemplate.opsForValue()
                .setIfAbsent(resourceId,clientId,30, TimeUnit.SECONDS);

    if (acquired){
        historyService.logEvent(resourceId,clientId,"Acquired",LocalDateTime.now().plusSeconds(30));
        return LockResult.ACQUIRED;
    }

        return LockResult.ALREADY_HELD;

    }

    public LockResult renewLock(String resourceId,String clientId){

        String KeyOwner=redisTemplate.opsForValue()
                .get(resourceId);
        if(KeyOwner == null){
            return LockResult.NOT_HELD;
        }

        if(!KeyOwner.equals(clientId)){
            return LockResult.NOT_OWNER;
        }

        redisTemplate
                .expire(resourceId,30,TimeUnit.SECONDS);;
        historyService
                .logEvent(resourceId,clientId,"Renewed",LocalDateTime.now().plusSeconds(30));
        return LockResult.RENEW;


    }
    public LockResult releaseLock(String resourceId, String clientId) {
        String currentHolder = redisTemplate.opsForValue().get(resourceId);

        if (currentHolder == null) {
            return LockResult.NOT_HELD;
        }
        if (!currentHolder.equals(clientId)) {
            return LockResult.NOT_OWNER;
        }

        redisTemplate.delete(resourceId);
        historyService.logEvent(resourceId, clientId, "RELEASED", null);
        return LockResult.RELEASED;
    }







}

