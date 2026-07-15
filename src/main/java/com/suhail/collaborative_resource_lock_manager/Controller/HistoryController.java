package com.suhail.collaborative_resource_lock_manager.Controller;


import com.suhail.collaborative_resource_lock_manager.Entity.HistoryEntity;
import com.suhail.collaborative_resource_lock_manager.Service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HistoryController {
    final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }


    @GetMapping("locks/{resourceId}/history")
    public List<HistoryEntity> history(@PathVariable String resourceId){
        return historyService.findByResourceId(resourceId);

    }


}
