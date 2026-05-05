package com.ecotravel.maintenance.service;

import com.ecotravel.maintenance.dto.request.MaintenanceTaskRequestDTO;
import com.ecotravel.maintenance.dto.response.MaintenanceTaskResponseDTO;
import com.ecotravel.maintenance.model.MaintenanceTask;
import com.ecotravel.maintenance.repository.MaintenanceTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public List<MaintenanceTaskResponseDTO> getAll() {
        return maintenanceTaskRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceTaskResponseDTO getById(Long id) {
        return convertToDTO(findTaskById(id));
    }

    public List<MaintenanceTaskResponseDTO> getByMaxDuration(Integer maxHours) {
        return maintenanceTaskRepository.findByEstimatedDurationHoursLessThanEqual(maxHours)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceTaskResponseDTO create(MaintenanceTaskRequestDTO dto) {
        MaintenanceTask task = new MaintenanceTask();
        task.setTaskName(dto.getTaskName());
        task.setEstimatedDurationHours(dto.getEstimatedDurationHours());
        return convertToDTO(maintenanceTaskRepository.save(task));
    }

    public MaintenanceTaskResponseDTO update(Long id, MaintenanceTaskRequestDTO dto) {
        MaintenanceTask task = findTaskById(id);
        task.setTaskName(dto.getTaskName());
        task.setEstimatedDurationHours(dto.getEstimatedDurationHours());
        return convertToDTO(maintenanceTaskRepository.save(task));
    }

    public void delete(Long id) {
        if (!maintenanceTaskRepository.existsById(id)) {
            throw new RuntimeException("Zadatak sa ID " + id + " nije pronadjen");
        }
        maintenanceTaskRepository.deleteById(id);
    }

    public MaintenanceTask findTaskById(Long id) {
        return maintenanceTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zadatak sa ID " + id + " nije pronadjen"));
    }

    public MaintenanceTaskResponseDTO convertToDTO(MaintenanceTask task) {
        MaintenanceTaskResponseDTO dto = new MaintenanceTaskResponseDTO();
        dto.setId(task.getId());
        dto.setTaskName(task.getTaskName());
        dto.setEstimatedDurationHours(task.getEstimatedDurationHours());
        return dto;
    }
}