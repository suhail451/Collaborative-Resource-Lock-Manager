package com.suhail.collaborative_resource_lock_manager.Exception;


import org.springframework.http.HttpHeaders;
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
    }