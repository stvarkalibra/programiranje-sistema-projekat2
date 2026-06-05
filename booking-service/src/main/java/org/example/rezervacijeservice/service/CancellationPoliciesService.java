package org.example.rezervacijeservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rezervacijeservice.dto.CancellationPolicyResponse;
import org.example.rezervacijeservice.exception.ResourceNotFoundException;
import org.example.rezervacijeservice.model.CancellationPolicy;
import org.example.rezervacijeservice.repository.CancellationPoliciesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancellationPoliciesService {

    private final CancellationPoliciesRepository cancellationPoliciesRepository;

    public List<CancellationPolicyResponse> getAllPolicies() {
        return cancellationPoliciesRepository.findAll().stream()
                .map(this::toPolicyResponse)
                .collect(Collectors.toList());
    }

    public CancellationPolicyResponse getPolicyById(Long id) {
        CancellationPolicy policy = cancellationPoliciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CancellationPolicy not found with id: " + id));
        return toPolicyResponse(policy);
    }

    public CancellationPolicyResponse createPolicy(CancellationPolicyResponse dto) {
        log.info("Kreirana nova polisa - naziv: {}", dto.getPolicyName());
        CancellationPolicy policy = new CancellationPolicy();
        policy.setPolicyName(dto.getPolicyName());
        policy.setRefundPercentage(dto.getRefundPercentage());

        CancellationPolicy saved = cancellationPoliciesRepository.save(policy);
        return toPolicyResponse(saved);
    }

    public CancellationPolicyResponse updatePolicy(Long id, CancellationPolicyResponse dto) {
        CancellationPolicy policy = cancellationPoliciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CancellationPolicy not found with id: " + id));

        if (dto.getPolicyName() != null) {
            policy.setPolicyName(dto.getPolicyName());
        }
        if (dto.getRefundPercentage() != null) {
            policy.setRefundPercentage(dto.getRefundPercentage());
        }

        CancellationPolicy updated = cancellationPoliciesRepository.save(policy);
        return toPolicyResponse(updated);
    }

    public void deletePolicy(Long id) {
        CancellationPolicy policy = cancellationPoliciesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CancellationPolicy not found with id: " + id));
        cancellationPoliciesRepository.deleteById(id);
    }

    private CancellationPolicyResponse toPolicyResponse(CancellationPolicy policy) {
        return new CancellationPolicyResponse(
                policy.getId(),
                policy.getPolicyName(),
                policy.getRefundPercentage()
        );
    }
}
