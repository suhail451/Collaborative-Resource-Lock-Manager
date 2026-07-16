package com.suhail.collaborative_resource_lock_manager.Exception;


import org.hibernate.metamodel.internal.EmbeddableRepresentationStrategyPojo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController

public class GlobalExceptionHandler {

            @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound( ResourceNotFoundException ex){
                    ErrorResponseDTO myerror = new ErrorResponseDTO();
                    myerror.setMessage(ex.getMessage());
                    myerror.setStatus(404);
                    myerror.setTimestamp(LocalDateTime.now());

                    return new ResponseEntity<>(myerror, HttpStatus.NOT_FOUND);

}





    @ExceptionHandler(ResourceAlreadyLocked.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExist(ResourceAlreadyLocked ex){
                ErrorResponseDTO error=new ErrorResponseDTO();

                error.setMessage(ex.getMessage());
                error.setTimestamp(LocalDateTime.now());
                error.setStatus(409);

                return new ResponseEntity<>(error,HttpStatus.CONFLICT);


    }

    @ExceptionHandler(NoActiveLockException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoLock(NoActiveLockException ex)
    {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(409);

        return new ResponseEntity<>(error,HttpStatus.CONFLICT);


    }

    @ExceptionHandler(LockOwnershipException.class)
    public ResponseEntity<ErrorResponseDTO> handleLockOwner(LockOwnershipException ex)
    {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setMessage(ex.getMessage());
        error.setStatus(403);
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(InvalidLockRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidLockRequest(InvalidLockRequestException ex){

                ErrorResponseDTO myerror= new ErrorResponseDTO();

                myerror.setMessage(ex.getMessage());
                myerror.setStatus(403);
                myerror.setTimestamp(LocalDateTime.now());

                return new ResponseEntity<>(myerror,HttpStatus.FORBIDDEN);


    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO>
    handleGeneric(Exception ex){

        ErrorResponseDTO myerror= new ErrorResponseDTO();

        myerror.setMessage(ex.getMessage());
        myerror.setStatus(501);
        myerror.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(myerror,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}


