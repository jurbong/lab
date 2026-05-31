package com.lab.global.config;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration
public class CorsConfig {
    @Bean public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration c=new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173"));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/api/**",c);
        return s;
    }
}
