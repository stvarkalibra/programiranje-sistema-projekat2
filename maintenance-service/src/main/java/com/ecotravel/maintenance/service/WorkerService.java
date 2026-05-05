package com.ecotravel.maintenance.service;

import com.ecotravel.maintenance.dto.request.WorkerRequestDTO;
import com.ecotravel.maintenance.dto.response.WorkerResponseDTO;
import com.ecotravel.maintenance.dto.response.WorkerSummaryResponseDTO;
import com.ecotravel.maintenance.model.WorkOrder;
import com.ecotravel.maintenance.model.Worker;
import com.ecotravel.maintenance.repository.WorkOrderRepository;
import com.ecotravel.maintenance.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkOrderRepository workOrderRepository;

    public List<WorkerResponseDTO> getAll() {
        return workerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WorkerResponseDTO getById(Long id) {
        return convertToDTO(findWorkerById(id));
    }

    public List<WorkerResponseDTO> getBySpecialty(String specialty) {
        return workerRepository.findBySpecialty(specialty.toUpperCase())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WorkerResponseDTO create(WorkerRequestDTO dto) {
        Worker worker = new Worker();
        worker.setFirstName(dto.getFirstName());
        worker.setLastName(dto.getLastName());
        worker.setSpecialty(dto.getSpecialty().toUpperCase());
        return convertToDTO(workerRepository.save(worker));
    }

    public WorkerResponseDTO update(Long id, WorkerRequestDTO dto) {
        Worker worker = findWorkerById(id);
        worker.setFirstName(dto.getFirstName());
        worker.setLastName(dto.getLastName());
        worker.setSpecialty(dto.getSpecialty().toUpperCase());
        return convertToDTO(workerRepository.save(worker));
    }

    public void delete(Long id) {
        if (!workerRepository.existsById(id)) {
            throw new RuntimeException("Radnik sa ID " + id + " nije pronadjen");
        }
        workerRepository.deleteById(id);
    }

    /**
     * Logicka kalkulacija: sumarni prikaz opterecenja radnika
     * Broji naloge po statusu i sumira procijenjene sate za aktivne naloge
     */
    public WorkerSummaryResponseDTO getWorkerSummary(Long workerId) {
        Worker worker = findWorkerById(workerId);
        List<WorkOrder> orders = workOrderRepository.findByWorker_Id(workerId);

        long pending = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long inProgress = orders.stream().filter(o -> "IN_PROGRESS".equals(o.getStatus())).count();
        long completed = orders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();

        // Suma sati samo za aktivne (PENDING + IN_PROGRESS) naloge
        int totalHours = orders.stream()
                .filter(o -> "PENDING".equals(o.getStatus()) || "IN_PROGRESS".equals(o.getStatus()))
                .mapToInt(o -> o.getTask().getEstimatedDurationHours())
                .sum();

        WorkerSummaryResponseDTO summary = new WorkerSummaryResponseDTO();
        summary.setWorkerId(workerId);
        summary.setFullName(worker.getFirstName() + " " + worker.getLastName());
        summary.setSpecialty(worker.getSpecialty());
        summary.setTotalOrders(orders.size());
        summary.setPendingOrders((int) pending);
        summary.setInProgressOrders((int) inProgress);
        summary.setCompletedOrders((int) completed);
        summary.setTotalEstimatedHours(totalHours);

        return summary;
    }

    private Worker findWorkerById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Radnik sa ID " + id + " nije pronadjen"));
    }

    public WorkerResponseDTO convertToDTO(Worker worker) {
        WorkerResponseDTO dto = new WorkerResponseDTO();
        dto.setId(worker.getId());
        dto.setFirstName(worker.getFirstName());
        dto.setLastName(worker.getLastName());
        dto.setSpecialty(worker.getSpecialty());
        dto.setFullName(worker.getFirstName() + " " + worker.getLastName());
        return dto;
    }
}