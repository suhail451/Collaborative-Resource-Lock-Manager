package com.suhail.collaborative_resource_lock_manager.Service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LockService  {
    final HistoryService historyService;
    final RedisTemplate<String,String> redisTemplate;

    public LockService(HistoryService historyService, RedisTemplate<String, String> redisTemplate) {
        this.historyService = historyService;
        this.redisTemplate = redisTemplate;
    }





}

