package com.suhail.collaborative_resource_lock_manager.Controller;

import com.suhail.collaborative_resource_lock_manager.Exception.InvalidLockRequestException;
import com.suhail.collaborative_resource_lock_manager.Exception.LockOwnershipException;
import com.suhail.collaborative_resource_lock_manager.Exception.NoActiveLockException;
import com.suhail.collaborative_resource_lock_manager.Exception.ResourceAlreadyLocked;
import com.suhail.collaborative_resource_lock_manager.Service.LockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/locks")
public class LockController {

    private final LockService lockService;

    public LockController(LockService lockService) {
        this.lockService = lockService;
    }


    // Acquire Lock
    @PostMapping("/{resourceId}")
    public ResponseEntity<String> acquire(
            @PathVariable String resourceId,
            @RequestBody String clientId
    ) throws InvalidLockRequestException, ResourceAlreadyLocked {

        lockService.acquireLock(resourceId, clientId);

        return ResponseEntity
                .status(CREATED)
                .body("Lock acquired successfully");
    }



    // Renew Lock
    @PatchMapping("/{resourceId}/renew")
    public ResponseEntity<String> renew(
            @PathVariable String resourceId,
            @RequestBody String clientId
    ) throws InvalidLockRequestException, NoActiveLockException, LockOwnershipException {

        lockService.renewLock(resourceId, clientId);

        return ResponseEntity
                .status(OK)
                .body("Lock renewed successfully");
    }



    // Release Lock
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> release(
            @PathVariable String resourceId,
            @RequestBody String clientId
    ) throws InvalidLockRequestException, NoActiveLockException, LockOwnershipException {

        lockService.releaseLock(resourceId, clientId);

        return ResponseEntity
                .status(OK)
                .body("Lock released successfully");
    }



    // Check Lock Status
    @GetMapping("/{resourceId}")
    public ResponseEntity<LockService.LockStatus> check(
            @PathVariable String resourceId
    ) throws InvalidLockRequestException {

        return ResponseEntity
                .status(OK)
                .body(lockService.checkStatus(resourceId));
    }

}