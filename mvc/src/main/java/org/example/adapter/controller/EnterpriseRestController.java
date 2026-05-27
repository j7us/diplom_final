package org.example.adapter.controller;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.controller.dto.EnterpriseRestDto;
import org.example.adapter.batch.dto.EnterpriseExport;
import org.example.adapter.controller.mapper.EnterpriseRestMapper;
import org.example.model.Enterprise;
import org.example.adapter.batch.EnterpriseJobService;
import org.example.application.service.EnterpriseService;
import org.springframework.http.HttpHeaders;
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

@Tag(name = "Предприятия", description = "Эндпоинты управления предприятиями")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class EnterpriseRestController {
    private final EnterpriseService enterpriseService;
    private final EnterpriseJobService enterpriseJobService;
    private final EnterpriseRestMapper enterpriseRestMapper;

    @Operation(summary = "Получение всех предприятий")
    @GetMapping("/enterprises")
    public List<EnterpriseRestDto> getEnterprises(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises с параметрами: {}", userDetails.getUsername());

        return enterpriseRestMapper.toDto(enterpriseService.getAll(userDetails.getUsername()));
    }

    @Operation(summary = "Получение предприятия по id")
    @GetMapping("/enterprises/{id}/")
    public EnterpriseRestDto getEnterprise(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/{id}/ с параметрами: {}, {}", id, userDetails.getUsername());

        Enterprise enterprise = enterpriseService.getById(id, userDetails.getUsername());

        return enterpriseRestMapper.toDto(enterprise);
    }

    @Operation(summary = "Создание предприятий")
    @PostMapping("/enterprises")
    public EnterpriseRestDto createEnterprise(@RequestBody EnterpriseRestDto dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises с параметрами: {}, {}, {}", dto.getName(), dto.getCountry(), userDetails.getUsername());

        Enterprise enterprise = enterpriseRestMapper.toModel(dto);
        Enterprise savedEnterprise = enterpriseService.create(enterprise, userDetails.getUsername());

        return enterpriseRestMapper.toDto(savedEnterprise);
    }

    @Operation(summary = "Создание предприятий из файла csv")
    @PostMapping(value = "/enterprises/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importEnterprisesCsv(@RequestParam("file") MultipartFile file,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/import/csv с параметрами: {}, {}, {}", file.getOriginalFilename(), file.getSize(), userDetails.getUsername());

        enterpriseJobService.importCsv(file, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Создание предприятий из файла json")
    @PostMapping(value = "/enterprises/import/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importEnterprisesJson(@RequestParam("file") MultipartFile file,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/import/json с параметрами: {}, {}, {}", file.getOriginalFilename(), file.getSize(), userDetails.getUsername());

        enterpriseJobService.importJson(file, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Выгрузка предприятий в формате csv")
    @GetMapping(value = "/enterprises/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportEnterprisesCsv(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/export/csv с параметрами: {}", userDetails.getUsername());

        EnterpriseExport file = enterpriseJobService.exportCsv(userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getContent());
    }

    @Operation(summary = "Выгрузка предприятий в формате json")
    @GetMapping(value = "/enterprises/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportEnterprisesJson(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/export/json с параметрами: {}", userDetails.getUsername());

        EnterpriseExport file = enterpriseJobService.exportJson(userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(file.getContent());
    }

    @Operation(summary = "Обновление предприятия")
    @PutMapping("/enterprises/{id}/")
    public EnterpriseRestDto updateEnterprise(@PathVariable UUID id,
                                              @RequestBody EnterpriseRestDto dto,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/{id}/ с параметрами: {}, {}, {}", id, dto.getName(), userDetails.getUsername());

        Enterprise enterprise = enterpriseRestMapper.toModel(dto);
        Enterprise savedEnterprise = enterpriseService.update(id, enterprise, userDetails.getUsername());

        return enterpriseRestMapper.toDto(savedEnterprise);
    }

    @Operation(summary = "Удаление предприятия")
    @DeleteMapping("/enterprises/{id}/")
    public void deleteEnterprise(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Пришел запрос /api/enterprises/{id}/ с параметрами: {}, {}", id, userDetails.getUsername());

        enterpriseService.delete(id, userDetails.getUsername());
    }
}
