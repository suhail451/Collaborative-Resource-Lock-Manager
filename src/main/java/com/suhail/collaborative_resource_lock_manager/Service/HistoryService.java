package com.suhail.collaborative_resource_lock_manager.Service;

import com.suhail.collaborative_resource_lock_manager.Entity.HistoryEntity;
import com.suhail.collaborative_resource_lock_manager.Repository.HistoryRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepo historyRepo;

    public HistoryService(HistoryRepo historyRepo) {
        this.historyRepo = historyRepo;
    }

    public HistoryEntity logEvent(String resourceId, String clientId, String eventType, LocalDateTime expiryAt) {
        HistoryEntity entity = new HistoryEntity();
        entity.setResourceId(resourceId);
        entity.setClientId(clientId);
        entity.setEventType(eventType);
        entity.setEventTimestamp(LocalDateTime.now());
        entity.setLockExpireAt(expiryAt);

        return historyRepo.save(entity);
    }

    public List<HistoryEntity> findByResourceId(String resourceId) {
        return historyRepo.findByResourceIdOrderByEventTimestampDesc(resourceId);
    }
}