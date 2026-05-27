package org.example.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.model.Enterprise;
import org.example.model.Manager;
import org.example.application.repository.EnterpriseRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;
    private final ManagerService managerService;

    public List<Enterprise> getAll(String username) {
        Manager manager = getManager(username);

        return enterpriseRepository.findAllByManagerId(manager.getId());
    }

    public Enterprise getById(UUID id, String username) {
        Manager manager = getManager(username);

        return enterpriseRepository.findByIdAndManagerId(id, manager.getId())
                .orElseThrow(() -> new RuntimeException("Предприятие не найдено"));
    }

    @Cacheable(cacheNames = "enterprisesByUsername")
    public Enterprise getById(UUID id) {
        return enterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Предприятие не найдено"));
    }

    public List<UUID> getEnterpriseIdsByManagerUsername(String username) {
        return getAll(username).stream()
                .map(Enterprise::getId)
                .toList();
    }

    @Transactional
    @CacheEvict(cacheNames = "enterprisesByUsername", key = "#username")
    public Enterprise create(Enterprise enterprise, String username) {
        Manager manager = getManager(username);

        enterprise.setId(UUID.randomUUID());
        enterprise.setManagerIds(List.of(manager.getId()));

        return enterpriseRepository.save(enterprise);
    }

    @Transactional
    @CacheEvict(cacheNames = "enterprisesByUsername", key = "#username")
    public Enterprise update(UUID id, Enterprise enterprise, String username) {
        Enterprise savedEnterprise = getById(id, username);

        updateEnterprise(enterprise, savedEnterprise);

        return enterpriseRepository.save(savedEnterprise);
    }

    @Transactional
    @CacheEvict(cacheNames = "enterprisesByUsername", key = "#username")
    public void delete(UUID id, String username) {
        Enterprise enterprise = getById(id, username);

        enterpriseRepository.deleteById(enterprise.getId());
    }

    private Manager getManager(String username) {
        return managerService.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Менеджер не найден"));
    }

    private void updateEnterprise(Enterprise source, Enterprise target) {
        target.setName(source.getName());
        target.setCountry(source.getCountry());
        target.setProductionCapacity(source.getProductionCapacity());
        target.setTimeZone(source.getTimeZone());
    }
}
