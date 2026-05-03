package org.example.finansijeservice.mapper;

import org.example.finansijeservice.dto.InvoiceResponse;
import org.example.finansijeservice.dto.InvoiceUpdateRequest;
import org.example.finansijeservice.model.Invoice;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceResponseMapper {
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    List<InvoiceResponse> toInvoiceResponseList(List<Invoice> invoices);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingId", ignore = true)
    Invoice toEntity(InvoiceResponse invoiceResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInvoiceFromRequest(InvoiceUpdateRequest request, @MappingTarget Invoice invoice);
}
