package com.suhail.collaborative_resource_lock_manager.Exception;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponseDTO {

    private String message;
    private LocalDateTime timestamp;
    private int status;
}



