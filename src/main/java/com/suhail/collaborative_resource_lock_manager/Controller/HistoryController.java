package com.suhail.collaborative_resource_lock_manager.Controller;


import com.suhail.collaborative_resource_lock_manager.Entity.HistoryEntity;
import com.suhail.collaborative_resource_lock_manager.Entity.ResponceDTo;
import com.suhail.collaborative_resource_lock_manager.Service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "History API ", description = "API responsible for log and audit")

public class HistoryController {
    final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Operation(summary = "Get history",description = "Get all the details about the lock and who is the client what was the time to acquire lock and when it is supposed to be released")
    @GetMapping("/locks/{resourceId}/history")
    public ResponseEntity<List<ResponceDTo>> history(
            @PathVariable String resourceId
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(historyService.findByResourceId(resourceId));
    }


}
