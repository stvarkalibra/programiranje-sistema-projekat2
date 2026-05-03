package org.example.finansijeservice.mapper;

import org.example.finansijeservice.dto.TransactionDTO;
import org.example.finansijeservice.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionDTOMapper {

    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    Transaction toEntity(TransactionDTO transactionDTO);

    @Mapping(source = "invoice.id", target = "invoiceId")
    @Mapping(source = "paymentMethod.id", target = "paymentMethodId")
    TransactionDTO toTransactionDTO(Transaction transaction);

    List<TransactionDTO> toTransactionDTOList(List<Transaction> transactions);
}
