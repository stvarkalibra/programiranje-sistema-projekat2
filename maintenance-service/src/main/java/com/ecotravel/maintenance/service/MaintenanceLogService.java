package com.ecotravel.maintenance.service;

import com.ecotravel.maintenance.dto.request.MaintenanceLogRequestDTO;
import com.ecotravel.maintenance.dto.response.MaintenanceLogResponseDTO;
import com.ecotravel.maintenance.model.MaintenanceLog;
import com.ecotravel.maintenance.repository.MaintenanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceLogService {

    private final MaintenanceLogRepository maintenanceLogRepository;

    public List<MaintenanceLogResponseDTO> getAll() {
        return maintenanceLogRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceLogResponseDTO> getByUnitId(Long unitId) {
        return maintenanceLogRepository.findByUnitId(unitId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceLogResponseDTO getById(Long id) {
        return convertToDTO(findLogById(id));
    }

    public MaintenanceLogResponseDTO create(MaintenanceLogRequestDTO dto) {
        MaintenanceLog log = new MaintenanceLog();
        log.setUnitId(dto.getUnitId());
        log.setTaskDescription(dto.getTaskDescription());
        log.setWorkerName(dto.getWorkerName());
        log.setCompletedAt(dto.getCompletedAt());
        log.setNotes(dto.getNotes());
        return convertToDTO(maintenanceLogRepository.save(log));
    }

    public void delete(Long id) {
        if (!maintenanceLogRepository.existsById(id)) {
            throw new RuntimeException("Log sa ID " + id + " nije pronadjen");
        }
        maintenanceLogRepository.deleteById(id);
    }

    private MaintenanceLog findLogById(Long id) {
        return maintenanceLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log sa ID " + id + " nije pronadjen"));
    }

    private MaintenanceLogResponseDTO convertToDTO(MaintenanceLog log) {
        MaintenanceLogResponseDTO dto = new MaintenanceLogResponseDTO();
        dto.setId(log.getId());
        dto.setUnitId(log.getUnitId());
        dto.setTaskDescription(log.getTaskDescription());
        dto.setWorkerName(log.getWorkerName());
        dto.setCompletedAt(log.getCompletedAt());
        dto.setNotes(log.getNotes());
        return dto;
    }
}