package org.example.adapter.repository;

import lombok.RequiredArgsConstructor;
import org.example.adapter.repository.entity.ManagerEntity;
import org.example.adapter.repository.mapper.ManagerMapper;
import org.example.model.Manager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ManagerRepositoryAdapter implements org.example.application.repository.ManagerRepository {
    private final org.example.adapter.repository.jpa.ManagerRepository managerRepository;
    private final ManagerMapper managerMapper;

    @Override
    public Optional<Manager> findByUsername(String username) {
        return managerRepository.findByUsername(username)
                .map(managerMapper::toModel);
    }

    @Override
    public Manager save(Manager manager) {
        ManagerEntity managerEntity = managerMapper.toEntity(manager);
        ManagerEntity savedManagerEntity = managerRepository.save(managerEntity);

        return managerMapper.toModel(savedManagerEntity);
    }
}
