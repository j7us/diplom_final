package org.example.application.repository;

import java.util.Optional;
import org.example.model.Manager;

public interface ManagerRepository {
    Optional<Manager> findByUsername(String username);

    Manager save(Manager manager);
}
