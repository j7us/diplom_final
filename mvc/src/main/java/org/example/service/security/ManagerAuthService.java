package org.example.service.security;

import lombok.RequiredArgsConstructor;
import org.example.service.ManagerService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class ManagerAuthService implements UserDetailsService {

    private final ManagerService managerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return managerService.findByUserName(username).orElseThrow();
    }
}
