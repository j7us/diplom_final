package org.example.adapter.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden
@Controller
@Slf4j
public class FrontendController {

    @GetMapping("/login")
    public String loginPage() {
        log.info("Пришел запрос /login");

        return "restfront/login";
    }

    @GetMapping("/view/enterprises")
    public String enterprisesPage() {
        log.info("Пришел запрос /view/enterprises");

        return "restfront/enterprises";
    }

    @GetMapping("/view/enterprise-vehicles")
    public String enterpriseVehiclesPage() {
        log.info("Пришел запрос /view/enterprise-vehicles");

        return "restfront/enterprise-vehicles";
    }

    @GetMapping("/view/vehicle-details")
    public String vehicleDetailsPage() {
        log.info("Пришел запрос /view/vehicle-details");

        return "restfront/vehicle-details";
    }

    @GetMapping("/view/reports")
    public String reportsPage() {
        log.info("Пришел запрос /view/reports");

        return "restfront/reports";
    }
}
