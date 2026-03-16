package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.EnterpriseRestDto;
import org.example.service.EnterpriseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class EnterpriseRestController {
    private final EnterpriseService enterpriseService;

    @GetMapping( "/enterprises")
    public List<EnterpriseRestDto> getEnterprises(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Прищел запрос");
        return enterpriseService.getAll(userDetails.getUsername());
    }

    @GetMapping("/enterprises/{id}/")
    public EnterpriseRestDto getEnterprise(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        return enterpriseService.getById(id, userDetails.getUsername());
    }

    @PostMapping("/enterprises")
    public EnterpriseRestDto createEnterprise(@RequestBody EnterpriseRestDto dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return enterpriseService.create(dto, userDetails.getUsername());
    }

    @PutMapping("/enterprises/{id}/")
    public EnterpriseRestDto updateEnterprise(@PathVariable UUID id,
                                              @RequestBody EnterpriseRestDto dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return enterpriseService.update(id, dto, userDetails.getUsername());
    }

    @DeleteMapping("/enterprises/{id}/")
    public void deleteEnterprise(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        enterpriseService.delete(id, userDetails.getUsername());
    }
}
