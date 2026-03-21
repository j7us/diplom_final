package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.dto.EnterpriseRestDto;
import org.example.service.EnterpriseJobService;
import org.example.service.EnterpriseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnterpriseRestController {
    private final EnterpriseService enterpriseService;
    private final EnterpriseJobService enterpriseJobService;

    @GetMapping("/enterprises")
    public List<EnterpriseRestDto> getEnterprises(@AuthenticationPrincipal UserDetails userDetails) {
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

    @PostMapping(value = "/enterprises/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity importEnterprisesCsv(@RequestParam("file") MultipartFile file,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        enterpriseJobService.importCsv(file, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/enterprises/import/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity importEnterprisesJson(@RequestParam("file") MultipartFile file,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        enterpriseJobService.importJson(file, userDetails.getUsername());
        return ResponseEntity.ok().build();
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
