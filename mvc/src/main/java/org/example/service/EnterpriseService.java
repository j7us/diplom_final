package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.EnterpriseRestDto;
import org.example.entity.Enterprise;
import org.example.entity.Manager;
import org.example.map.EnterpriseRestMapper;
import org.example.repository.EnterpriseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseRestMapper enterpriseRestMapper;
    private final ManagerService managerService;

    public List<EnterpriseRestDto> getAll(String username) {
        Manager manager = managerService.findByUserName(username).orElseThrow();

        return enterpriseRepository.findAllByManagers_Id(manager.getId()).stream()
                .map(enterpriseRestMapper::toDto)
                .toList();
    }

    public EnterpriseRestDto getById(UUID id, String username) {
        return getByIdAndManagerUsername(id, username);
    }

    public EnterpriseRestDto getByIdAndManagerUsername(UUID id, String username) {
        Manager manager = managerService.findByUserName(username).orElseThrow();

        Enterprise enterprise = enterpriseRepository.findByIdAndManagers_Id(id, manager.getId()).orElseThrow();

        return enterpriseRestMapper.toDto(enterprise);
    }

    public Enterprise getEntityByIdAndManagerUsername(UUID id, String username) {
        Manager manager = managerService.findByUserName(username).orElseThrow();

        return enterpriseRepository.findByIdAndManagers_Id(id, manager.getId()).orElseThrow();
    }

    public Enterprise getEntityById(UUID id) {
        return enterpriseRepository.findById(id).orElseThrow();
    }

    public List<UUID> getEnterpriseIdsByManagerUsername(String username) {
        Manager manager = managerService.findByUserName(username).orElseThrow();

        return enterpriseRepository.findAllByManagers_Id(manager.getId()).stream()
                .map(Enterprise::getId)
                .toList();
    }

    @Transactional
    public EnterpriseRestDto create(EnterpriseRestDto dto, String username) {
        Manager manager = managerService.findByUserName(username).orElseThrow();

        Enterprise enterprise = enterpriseRestMapper.toEntity(dto);
        enterprise.setId(UUID.randomUUID());

        List<Manager> enterpriseManagers = new ArrayList<>();
        enterpriseManagers.add(manager);

        enterprise.setManagers(enterpriseManagers);

        Enterprise savedEnterprise = enterpriseRepository.save(enterprise);

        List<Enterprise> enterprises = manager.getEnterprises();
        if (enterprises == null) {
            enterprises = new ArrayList<>();
            manager.setEnterprises(enterprises);
        }
        enterprises.add(savedEnterprise);

        managerService.save(manager);

        return enterpriseRestMapper.toDto(savedEnterprise);
    }

    @Transactional
    public EnterpriseRestDto update(UUID id, EnterpriseRestDto dto, String username) {
        Manager manager = managerService.findByUserName(username).orElseThrow();

        Enterprise enterprise = enterpriseRepository.findByIdAndManagers_Id(id, manager.getId()).orElseThrow();

        enterpriseRestMapper.updateEntity(dto, enterprise);

        Enterprise savedEnterprise = enterpriseRepository.save(enterprise);

        return enterpriseRestMapper.toDto(savedEnterprise);
    }

    @Transactional
    public void delete(UUID id, String username) {
        Enterprise enterprise = getEntityByIdAndManagerUsername(id, username);

        enterpriseRepository.delete(enterprise);
    }
}
