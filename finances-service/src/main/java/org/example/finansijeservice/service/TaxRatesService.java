package org.example.finansijeservice.service;

import lombok.RequiredArgsConstructor;
import org.example.finansijeservice.dto.TaxRateDTO;
import org.example.finansijeservice.exception.ResourceNotFoundException;
import org.example.finansijeservice.mapper.TaxRateDTOMapper;
import org.example.finansijeservice.model.TaxRate;
import org.example.finansijeservice.repository.TaxRatesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxRatesService {

    private final TaxRatesRepository taxRatesRepository;
    private final TaxRateDTOMapper mapper;

    public List<TaxRateDTO> getAllTaxRates() {
        return mapper.toTaxRateDTOList(taxRatesRepository.findAll());
    }

    public TaxRateDTO getTaxRateById(Long id) {
        TaxRate taxRate = taxRatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxRate not found with id: " + id));
        return mapper.toTaxRateDTO(taxRate);
    }

    public TaxRateDTO createTaxRate(TaxRateDTO dto) {
        TaxRate taxRate = mapper.toEntity(dto);
        TaxRate saved = taxRatesRepository.save(taxRate);
        return mapper.toTaxRateDTO(saved);
    }

    public TaxRateDTO updateTaxRate(Long id, TaxRateDTO dto) {
        TaxRate taxRate = taxRatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxRate not found with id: " + id));

        if (dto.getTaxName() != null) {
            taxRate.setTaxName(dto.getTaxName());
        }
        if (dto.getPercentage() != null) {
            taxRate.setPercentage(dto.getPercentage());
        }

        TaxRate updated = taxRatesRepository.save(taxRate);
        return mapper.toTaxRateDTO(updated);
    }

    public void deleteTaxRate(Long id) {
        TaxRate taxRate = taxRatesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxRate not found with id: " + id));
        taxRatesRepository.deleteById(id);
    }

    public List<TaxRateDTO> getTaxRatesByPercentageRange(Double minPercentage, Double maxPercentage) {
        return mapper.toTaxRateDTOList(
            taxRatesRepository.findAll().stream()
                .filter(tr -> tr.getPercentage() >= minPercentage && tr.getPercentage() <= maxPercentage)
                .toList()
        );
    }
}
