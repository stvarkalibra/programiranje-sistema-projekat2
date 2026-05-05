package com.ecotravel.maintenance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Sumarni prikaz opterecenja radnika")
public class WorkerSummaryResponseDTO {

    private Long workerId;
    private String fullName;
    private String specialty;

    @Schema(description = "Ukupan broj radnih naloga")
    private Integer totalOrders;

    @Schema(description = "Broj naloga na cekanju (PENDING)")
    private Integer pendingOrders;

    @Schema(description = "Broj naloga u toku (IN_PROGRESS)")
    private Integer inProgressOrders;

    @Schema(description = "Broj zavrsenih naloga (COMPLETED)")
    private Integer completedOrders;

    @Schema(description = "Ukupno procijenjenih sati rada na aktivnim nalozima (PENDING + IN_PROGRESS)")
    private Integer totalEstimatedHours;
}