package org.example.finansijeservice.mapper;

import org.example.finansijeservice.dto.InvoiceRequest;
import org.example.finansijeservice.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceRequestMapper {

    InvoiceRequest toInvoiceRequest(Invoice invoice);

    List<InvoiceRequest> toInvoiceRequestList(List<Invoice> invoices);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "taxRate", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Invoice toEntity(InvoiceRequest invoiceRequest);
}
