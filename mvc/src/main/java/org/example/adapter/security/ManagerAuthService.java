package org.example.adapter.security;

import lombok.RequiredArgsConstructor;
import org.example.model.Manager;
import org.example.application.service.ManagerService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class ManagerAuthService implements UserDetailsService {

    private final ManagerService managerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerService.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Менеджер не найден"));

        return new ManagerUserDetails(manager);
    }
}
