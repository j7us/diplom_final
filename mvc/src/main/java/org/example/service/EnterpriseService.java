package org.example.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.EnterpriseRestDto;
import org.example.entity.Enterprise;
import org.example.map.EnterpriseRestMapper;
import org.example.repository.EnterpriseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseRestMapper enterpriseRestMapper;

    public List<EnterpriseRestDto> getAll() {
        return enterpriseRepository.findAll().stream()
                .map(enterpriseRestMapper::toDto)
                .toList();
    }

    public EnterpriseRestDto getById(UUID id) {
        Enterprise enterprise = enterpriseRepository.findById(id).orElseThrow();

        return enterpriseRestMapper.toDto(enterprise);
    }
}
