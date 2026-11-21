package com.uniremington.armamento.config;

import com.uniremington.armamento.security.UsuarioAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {
    private final UsuarioAuthenticationService authenticationService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationFailureHandler failureHandler = (request, response, exception) -> {
            authenticationService.registrarFallo(request.getParameter("username"));
            response.sendRedirect(request.getContextPath() + "/login?error");
        };
        AuthenticationSuccessHandler successHandler = (request, response, authentication) -> {
            authenticationService.registrarAcceso(authentication.getName());
            response.sendRedirect(request.getContextPath() + "/");
        };

        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/styles.css",
                                "/error", "/403", "/404", "/500").permitAll()
                        .requestMatchers("/mvc/usuarios/**", "/mvc/roles/**", "/usuarios/**",
                                "/api/usuarios/**", "/api/roles/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/mvc/auditorias/**", "/api/auditorias/**")
                        .hasAnyRole("ADMINISTRADOR", "AUDITOR")
                        .requestMatchers("/mvc/auditorias/**", "/api/auditorias/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/auditorias/**").hasAnyRole("ADMINISTRADOR", "AUDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/**", "/mvc/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/**", "/mvc/**")
                        .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "ALMACENISTA")
                        .requestMatchers(HttpMethod.PUT, "/api/**", "/mvc/**")
                        .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "ALMACENISTA")
                        .requestMatchers(HttpMethod.DELETE, "/api/**", "/mvc/**")
                        .hasAnyRole("ADMINISTRADOR", "ALMACENISTA")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .failureHandler(failureHandler)
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(errors -> errors.accessDeniedPage("/403"))
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
