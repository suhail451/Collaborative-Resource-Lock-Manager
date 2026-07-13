package com.suhail.collaborative_resource_lock_manager.Repository;

import com.suhail.collaborative_resource_lock_manager.Entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepo extends JpaRepository<HistoryEntity, Integer> {
}