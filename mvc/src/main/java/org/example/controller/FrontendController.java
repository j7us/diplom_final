package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/login")
    public String loginPage() {
        return "restfront/login";
    }

    @GetMapping("/view/enterprises")
    public String enterprisesPage() {
        return "restfront/enterprises";
    }

    @GetMapping("/view/enterprise-vehicles")
    public String enterpriseVehiclesPage() {
        return "restfront/enterprise-vehicles";
    }
}
