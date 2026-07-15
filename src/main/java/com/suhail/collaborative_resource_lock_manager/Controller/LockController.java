package com.suhail.collaborative_resource_lock_manager.Controller;


import com.suhail.collaborative_resource_lock_manager.Service.LockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.suhail.collaborative_resource_lock_manager.Service.LockService.LockResult.*;
import static org.springframework.http.HttpStatus.*;

@RestController
public class LockController {

    final LockService lockService;

    public LockController(LockService lockService) {
        this.lockService = lockService;
    }

    @PostMapping("locks/{resourceId}")
    public ResponseEntity<String> acquire(@PathVariable String resourceId, @RequestBody String clientId){
        LockService.LockResult result=lockService.acquireLock(resourceId,clientId);

        return switch (result) {
            case ACQUIRED -> ResponseEntity.status(CREATED).body("Lock Acquired");
            case ALREADY_HELD -> ResponseEntity.status(CONFLICT).body("Lock is already acquired");
            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unexpected error");
        };
    }


    @PatchMapping("locks/{resourceId}/renew")
    public ResponseEntity<String> renew(@PathVariable String resourceId,@RequestBody String clientId){
        LockService.LockResult result=lockService.renewLock(resourceId,clientId);

        return switch (result){
            case NOT_HELD -> ResponseEntity.status(NOT_FOUND).body("Lock not found, Already Expired");
            case NOT_OWNER -> ResponseEntity.status(FORBIDDEN).body("Not your Lock");
            case RENEW -> ResponseEntity.status(OK).body("Lock renewed");
            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unexpected Error");

        };
    }


    @DeleteMapping("locks/{resourceId}")
    public ResponseEntity<String> delete(@PathVariable String resourceId,@RequestBody String clientId){
        LockService.LockResult result=lockService.releaseLock(resourceId,clientId);

        return switch(result){
            case RELEASED -> ResponseEntity.status(OK).body("Lock release Succesfully");
            case NOT_OWNER -> ResponseEntity.status(FORBIDDEN).body("Not your Lock");
            case NOT_HELD -> ResponseEntity.status(NOT_FOUND).body("No lock found");
            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unexpected error");

        };



    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<LockService.LockStatus> check(@PathVariable String resourceId){
        LockService.LockStatus status = lockService.checkStatus(resourceId);

        if (status == null) {
            return ResponseEntity.status(NOT_FOUND).body(null);
        }
        return ResponseEntity.status(OK).body(status);
    }

}
