package org.example.finansijeservice.mapper;

import org.example.finansijeservice.dto.PaymentMethodDTO;
import org.example.finansijeservice.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodDTOMapper {

    @Mapping(source = "methodName", target = "method_name")
    PaymentMethod toPaymentMethod(PaymentMethodDTO paymentMethodDTO);

    @Mapping(source = "methodName", target = "method_name")
    PaymentMethod toEntity(PaymentMethodDTO paymentMethodDTO);

    List<PaymentMethodDTO> toPaymentMethodDTOList(List<PaymentMethod> paymentMethods);

    @Mapping(source = "method_name", target = "methodName")
    PaymentMethodDTO toPaymentMethodDTO(PaymentMethod paymentMethod);
}
