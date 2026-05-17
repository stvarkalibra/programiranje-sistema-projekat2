package com.ecotravel.maintenance.repository;

import com.ecotravel.maintenance.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByUnitId(Long unitId);

    List<WorkOrder> findByStatus(String status);

    List<WorkOrder> findByWorker_Id(Long workerId);

    List<WorkOrder> findByUnitIdAndStatus(Long unitId, String status);
}