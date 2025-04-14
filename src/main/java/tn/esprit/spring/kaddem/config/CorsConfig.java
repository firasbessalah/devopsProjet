package tn.esprit.spring.kaddem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        System.out.println("Applying CORS configuration for frontend origins");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from localhost:4200
        config.addAllowedOrigin("http://localhost:4200");

        // Allow all HTTP methods and headers
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);  // Changed to true if you need cookies/auth

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}