package org.example.bootstrap.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.adapter.controller.filter.JwtFilter;
import org.example.application.report.ReportTypeNameResolver;
import org.example.application.service.ManagerService;
import org.example.adapter.security.ManagerAuthService;
import org.example.bootstrap.property.GeoapifyProp;
import org.example.bootstrap.property.GraphHopperProp;
import org.example.bootstrap.property.ReportProp;
import org.example.bootstrap.property.SecurityProp;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableConfigurationProperties(value = {SecurityProp.class, GraphHopperProp.class, GeoapifyProp.class, ReportProp.class})
@RequiredArgsConstructor
@EnableCaching
public class AppConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login/**","/registration/**", "/css/**", "/refresh_token/**", "/", "/view/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**")
                            .permitAll();
                    auth.anyRequest().hasAuthority("MANAGER");
                })
                .exceptionHandling(exh -> exh.authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                        }
                ))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(ManagerService service) {
        return new ManagerAuthService(service);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    @Bean
    public ReportTypeNameResolver reportTypeNameResolver(ReportProp reportProp) {
        return reportType -> reportProp.getTranslations().getOrDefault(reportType, reportType);
    }
}
