package com.suhail.collaborative_resource_lock_manager.Service;

import com.suhail.collaborative_resource_lock_manager.Entity.HistoryEntity;
import com.suhail.collaborative_resource_lock_manager.Entity.ResponceDTo;
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

    public ResponceDTo logEvent(String resourceId,
                                String clientId,
                                String eventType,
                                LocalDateTime expiryAt) {

        HistoryEntity entity = new HistoryEntity();

        entity.setResourceId(resourceId);
        entity.setClientId(clientId);
        entity.setEventType(eventType);
        entity.setEventTimestamp(LocalDateTime.now());
        entity.setLockExpireAt(expiryAt);

        HistoryEntity saved = historyRepo.save(entity);

        ResponceDTo dto = new ResponceDTo();
        dto.setResourceId(saved.getResourceId());
        dto.setClientId(saved.getClientId());
        dto.setEventType(saved.getEventType());
        dto.setEventTimestamp(saved.getEventTimestamp());
        dto.setLockExpireAt(saved.getLockExpireAt());

        return dto;
    }

    public List<ResponceDTo> findByResourceId(String resourceId) {

        List<HistoryEntity> historyList =
                historyRepo.findByResourceIdOrderByEventTimestampDesc(resourceId);

        return historyList.stream()
                .map(history -> new ResponceDTo(
                        history.getResourceId(),
                        history.getClientId(),
                        history.getEventType(),
                        history.getEventTimestamp(),
                        history.getLockExpireAt()
                ))
                .toList();
    }
}