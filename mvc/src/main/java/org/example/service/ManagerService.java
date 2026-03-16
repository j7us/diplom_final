package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Manager;
import org.example.repository.ManagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {
    private final ManagerRepository managerRepository;

    public Optional<Manager> findByUserName(String username) {
        return managerRepository.findByUsername(username);
    }

    @Transactional
    public void save(Manager manager) {
        managerRepository.save(manager);
    }
}
