package com.suhail.collaborative_resource_lock_manager.Controller;


import com.suhail.collaborative_resource_lock_manager.Service.LockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.suhail.collaborative_resource_lock_manager.Service.LockService.LockResult.ACQUIRED;
import static com.suhail.collaborative_resource_lock_manager.Service.LockService.LockResult.ALREADY_HELD;
import static org.springframework.http.HttpStatus.*;

@RestController
public class LockController {

    final LockService lockService;

    public LockController(LockService lockService) {
        this.lockService = lockService;
    }

    @PostMapping("Lock/{resourceId}")
    public ResponseEntity<String> acquireLock(@PathVariable String resourceId, @RequestBody String clientId){

       LockService.LockResult result=lockService.acquireLock(resourceId,clientId);

        return switch (result) {
            case ACQUIRED -> ResponseEntity.status(CREATED).body("Lock Acquired");
            case ALREADY_HELD -> ResponseEntity.status(CONFLICT).body("Lock is already acquired");
            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unexpected error");
        };


    }





}
