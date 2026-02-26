package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.EnterpriseRestDto;
import org.example.service.EnterpriseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnterpriseRestController {
    private final EnterpriseService enterpriseService;

    @GetMapping( "/enterprises")
    public List<EnterpriseRestDto> getEnterprises(@AuthenticationPrincipal UserDetails userDetails) {
        return enterpriseService.getAll(userDetails.getUsername());
    }

    @GetMapping("/enterprises/{id}/")
    public EnterpriseRestDto getEnterprise(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        return enterpriseService.getById(id, userDetails.getUsername());
    }
}
