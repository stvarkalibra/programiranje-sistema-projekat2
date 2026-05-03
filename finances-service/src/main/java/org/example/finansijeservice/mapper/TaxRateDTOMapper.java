package org.example.finansijeservice.mapper;

import org.example.finansijeservice.dto.TaxRateDTO;
import org.example.finansijeservice.model.TaxRate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaxRateDTOMapper {

    TaxRateDTO toTaxRateDTO(TaxRate taxRate);
    List<TaxRateDTO> toTaxRateDTOList(List<TaxRate> taxRates);
    TaxRate toEntity(TaxRateDTO taxRateDTO);
    TaxRate toTaxRate(TaxRateDTO taxRateDTO);
}
