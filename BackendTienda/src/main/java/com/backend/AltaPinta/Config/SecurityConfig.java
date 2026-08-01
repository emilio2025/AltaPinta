package com.backend.AltaPinta.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Permitimos los orígenes locales más comunes para Angular en desarrollo
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache de CORS por una hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Públicas
                        .requestMatchers("/api/auth/**").permitAll()

                        // Catálogo: LECTURA pública (cualquiera puede navegar la tienda)
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tipos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tallas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/deportes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos/tipos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/imagenes/**").permitAll()

                        // Catálogo: ESCRITURA solo admin.
                        // Antes /categorias/** y /tallas/** eran permitAll para todos los
                        // métodos, así que cualquier anónimo podía crear categorías y tallas.
                        // Estas reglas van después de los GET de arriba, que ya los permiten.
                        .requestMatchers("/categorias/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/tallas/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/deportes/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/tipos/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/productos/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/productos/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAuthority("ROLE_ADMIN")

                        // Usuario autenticado
                        .requestMatchers(HttpMethod.POST, "/pedido/confirmar").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/pedido/mis-pedidos").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/pedido/*/factura").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/favoritos/**").hasAnyRole("USER", "ADMIN")


                                .anyRequest().authenticated()
                )


                // 4. Registro del filtro JWT antes del filtro de usuario/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .httpBasic(h -> h.disable())
                .formLogin(f -> f.disable());

        return http.build();
    }
}