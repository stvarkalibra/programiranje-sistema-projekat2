package org.example.finansijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.finansijeservice.dto.PaymentMethodDTO;
import org.example.finansijeservice.exception.ResourceNotFoundException;
import org.example.finansijeservice.mapper.PaymentMethodDTOMapper;
import org.example.finansijeservice.model.PaymentMethod;
import org.example.finansijeservice.repository.PaymentMethodsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodsService {

    private final PaymentMethodsRepository paymentMethodsRepository;
    private final PaymentMethodDTOMapper mapper;

    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return mapper.toPaymentMethodDTOList(paymentMethodsRepository.findAll());
    }

    public PaymentMethodDTO getPaymentMethodById(Long id) {
        PaymentMethod paymentMethod = paymentMethodsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found with id: " + id));
        return mapper.toPaymentMethodDTO(paymentMethod);
    }

    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO dto) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethod_name(dto.getMethodName());
        paymentMethod.setIsActive(dto.getIsActive());

        PaymentMethod saved = paymentMethodsRepository.save(paymentMethod);
        return mapper.toPaymentMethodDTO(saved);
    }

    public PaymentMethodDTO updatePaymentMethod(Long id, PaymentMethodDTO dto) {
        PaymentMethod paymentMethod = paymentMethodsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found with id: " + id));

        if (dto.getMethodName() != null) {
            paymentMethod.setMethod_name(dto.getMethodName());
        }
        if (dto.getIsActive() != null) {
            paymentMethod.setIsActive(dto.getIsActive());
        }

        PaymentMethod updated = paymentMethodsRepository.save(paymentMethod);
        return mapper.toPaymentMethodDTO(updated);
    }

    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethodsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found with id: " + id));
        paymentMethodsRepository.deleteById(id);
    }

    public List<PaymentMethodDTO> getActivePaymentMethods() {
        return mapper.toPaymentMethodDTOList(
            paymentMethodsRepository.findAll().stream()
                .filter(pm -> Boolean.TRUE.equals(pm.getIsActive()))
                .toList()
        );
    }
}
