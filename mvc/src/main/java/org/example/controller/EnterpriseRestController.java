package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.EnterpriseRestDto;
import org.example.service.EnterpriseService;
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
    public List<EnterpriseRestDto> getEnterprises() {
        return enterpriseService.getAll();
    }

    @GetMapping("/enterprises/{id}/")
    public EnterpriseRestDto getEnterprise(@PathVariable UUID id) {
        return enterpriseService.getById(id);
    }
}
