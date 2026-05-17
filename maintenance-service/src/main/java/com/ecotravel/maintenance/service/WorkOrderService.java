package com.ecotravel.maintenance.service;

import com.ecotravel.maintenance.dto.request.WorkOrderRequestDTO;
import com.ecotravel.maintenance.dto.request.WorkOrderStatusUpdateDTO;
import com.ecotravel.maintenance.dto.response.WorkOrderResponseDTO;
import com.ecotravel.maintenance.messaging.WorkOrderEventPublisher;
import com.ecotravel.maintenance.model.MaintenanceTask;
import com.ecotravel.maintenance.model.WorkOrder;
import com.ecotravel.maintenance.model.Worker;
import com.ecotravel.maintenance.repository.WorkOrderRepository;
import com.ecotravel.maintenance.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkerRepository workerRepository;
    private final MaintenanceTaskService maintenanceTaskService;
    private final WorkerService workerService;
    private final AccommodationIntegrationService integrationService;
    private final WorkOrderEventPublisher workOrderEventPublisher; // << NOVO

    public List<WorkOrderResponseDTO> getAll() {
        return workOrderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WorkOrderResponseDTO getById(Long id) {
        return convertToDTO(findOrderById(id));
    }

    public List<WorkOrderResponseDTO> getByUnitId(Long unitId) {
        return workOrderRepository.findByUnitId(unitId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<WorkOrderResponseDTO> getByStatus(String status) {
        return workOrderRepository.findByStatus(status.toUpperCase())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WorkOrderResponseDTO create(WorkOrderRequestDTO dto) {
        Worker worker = workerRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new RuntimeException(
                        "Radnik sa ID " + dto.getWorkerId() + " nije pronadjen"));

        MaintenanceTask task = maintenanceTaskService.findTaskById(dto.getTaskId());

        LocalDateTime scheduled = dto.getScheduledFor();
        integrationService.validateWorkOrderCreation(
                dto.getUnitId(),
                dto.getWorkerId(),
                scheduled.toLocalDate()
        );

        WorkOrder order = new WorkOrder();
        order.setUnitId(dto.getUnitId());
        order.setWorker(worker);
        order.setTask(task);
        order.setStatus("PENDING");
        order.setScheduledFor(scheduled);

        WorkOrder saved = workOrderRepository.save(order);
        log.info("Kreiran radni nalog ID {} za jedinicu {}", saved.getId(), dto.getUnitId());
        return convertToDTO(saved);
    }

    /**
     * Ažurira status radnog naloga.
     * Kada status pređe u COMPLETED, publishuje WorkOrderCompletedEvent.
     * Accommodation servis sluša i automatski briše MAINTENANCE blokadu iz kalendara.
     */
    public WorkOrderResponseDTO updateStatus(Long id, WorkOrderStatusUpdateDTO dto) {
        WorkOrder order = findOrderById(id);
        String newStatus = dto.getStatus().toUpperCase();

        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if ("COMPLETED".equals(newStatus)) {
            LocalDateTime completedAt = LocalDateTime.now();
            order.setCompletedAt(completedAt);

            WorkOrder saved = workOrderRepository.save(order);

            // Publishuj asinhroni događaj NAKON uspješnog upisa
            workOrderEventPublisher.publishWorkOrderCompleted(
                    saved.getId(),
                    saved.getUnitId(),
                    saved.getTask().getTaskName(),
                    saved.getWorker().getFirstName() + " " + saved.getWorker().getLastName(),
                    completedAt,
                    saved.getScheduledFor()
            );

            return convertToDTO(saved);
        }

        return convertToDTO(workOrderRepository.save(order));
    }

    public void delete(Long id) {
        if (!workOrderRepository.existsById(id)) {
            throw new RuntimeException("Radni nalog sa ID " + id + " nije pronadjen");
        }
        workOrderRepository.deleteById(id);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            throw new IllegalArgumentException("Radni nalog je vec u statusu: " + currentStatus);
        }
        if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new IllegalArgumentException(
                    "Završeni ili otkazani radni nalog ne može mijenjati status");
        }
        if ("IN_PROGRESS".equals(currentStatus) && "PENDING".equals(newStatus)) {
            throw new IllegalArgumentException(
                    "Status ne moze ici unazad (IN_PROGRESS -> PENDING nije dozvoljeno)");
        }
        List<String> validStatuses = List.of("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED");
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Nevalidan status: " + newStatus +
                            ". Dozvoljeni: PENDING, IN_PROGRESS, COMPLETED, CANCELLED");
        }
    }

    private WorkOrder findOrderById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Radni nalog sa ID " + id + " nije pronadjen"));
    }

    public WorkOrderResponseDTO convertToDTO(WorkOrder order) {
        WorkOrderResponseDTO dto = new WorkOrderResponseDTO();
        dto.setId(order.getId());
        dto.setUnitId(order.getUnitId());
        dto.setWorker(workerService.convertToDTO(order.getWorker()));
        dto.setTask(maintenanceTaskService.convertToDTO(order.getTask()));
        dto.setStatus(order.getStatus());
        dto.setScheduledFor(order.getScheduledFor());
        dto.setCompletedAt(order.getCompletedAt());
        dto.setEstimatedDurationHours(order.getTask().getEstimatedDurationHours());
        return dto;
    }
}