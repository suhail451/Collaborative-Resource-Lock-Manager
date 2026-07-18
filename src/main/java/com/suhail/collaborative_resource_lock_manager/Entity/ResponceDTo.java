package com.suhail.collaborative_resource_lock_manager.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ResponceDTo {


    private String resourceId;
    private String clientId;
    private String eventType;
    private LocalDateTime eventTimestamp;
    private LocalDateTime lockExpireAt;



}
