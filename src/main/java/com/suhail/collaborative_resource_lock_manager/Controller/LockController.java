package com.suhail.collaborative_resource_lock_manager.Controller;

import com.suhail.collaborative_resource_lock_manager.Exception.InvalidLockRequestException;
import com.suhail.collaborative_resource_lock_manager.Exception.LockOwnershipException;
import com.suhail.collaborative_resource_lock_manager.Exception.NoActiveLockException;
import com.suhail.collaborative_resource_lock_manager.Exception.ResourceAlreadyLocked;
import com.suhail.collaborative_resource_lock_manager.Service.LockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/locks")

@Tag(name = "Lock APIs ", description = "All API responsible for core Lock Management")
public class LockController {

    private final LockService lockService;

    public LockController(LockService lockService) {
        this.lockService = lockService;
    }


    // Acquire Lock
    @Operation(summary = "Acquire Lock",description = "Acquire the lock on given resource id if no lock exist before")
    @PostMapping("/{resourceId}")
    public ResponseEntity<String> acquire(
            @PathVariable String resourceId,
            Authentication authentication
    ) throws InvalidLockRequestException, ResourceAlreadyLocked {

        String ownerId = authentication.getName();
        lockService.acquireLock(resourceId, ownerId);

        return ResponseEntity
                .status(CREATED)
                .body("Lock acquired successfully");
    }



    // Renew Lock
    @Operation(summary = "Renew Lock",description = "Extend the time of lock expiry if it is in use and not expired yet")
    @PatchMapping("/{resourceId}/renew")
    public ResponseEntity<String> renew(
            @PathVariable String resourceId,
            Authentication authentication
    ) throws InvalidLockRequestException, NoActiveLockException, LockOwnershipException {

        String ownerId = authentication.getName();
        lockService.renewLock(resourceId, ownerId);

        return ResponseEntity
                .status(OK)
                .body("Lock renewed successfully");
    }



    // Release Lock
    @Operation(summary = "Delete Lock",description = "Release the lock on resource by resource ID")

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> release(
            @PathVariable String resourceId,
            Authentication authentication
    ) throws InvalidLockRequestException, NoActiveLockException, LockOwnershipException {

        String ownerId = authentication.getName();
        lockService.releaseLock(resourceId, ownerId);

        return ResponseEntity
                .status(OK)
                .body("Lock released successfully");
    }



    // Check Lock Status
    @Operation(summary = "Check Status",description = "Check if lock exist or not if exist return lock holder,and expiry time")
    @GetMapping("/{resourceId}")
    public ResponseEntity<LockService.LockStatus> check(
            @PathVariable String resourceId
    ) throws InvalidLockRequestException {

        return ResponseEntity
                .status(OK)
                .body(lockService.checkStatus(resourceId));
    }

}