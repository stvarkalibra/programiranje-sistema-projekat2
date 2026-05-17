package org.example.sharedmodule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderCompletedEventDTO {

    private Long workOrderId;
    private Long unitId;
    private String taskName;
    private String workerFullName;
    private LocalDateTime completedAt;
    // Accommodation koristi ovo da oslobodi blokadu u kalendaru
    private LocalDateTime scheduledFor;
}