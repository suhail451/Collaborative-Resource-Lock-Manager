package com.suhail.collaborative_resource_lock_manager.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    private String resourceId;
    private String clientId;
    private String eventType;
    private LocalDateTime eventTimestamp;
    private LocalDateTime lockExpireAt;


}
