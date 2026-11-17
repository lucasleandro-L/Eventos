package com.eventos.api.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecretKey secretKey;

    // a chave vem da variável de ambiente JWT_SECRET (ver application.properties)
    public SecurityConfig(@Value("${jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET deve ter pelo menos 32 caracteres");
        }
        this.secretKey = new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // públicas
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/cadastro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/certificados/validar/*").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                        // inscrições e certificados (rotas mais específicas primeiro)
                        .requestMatchers(HttpMethod.POST, "/api/v1/eventos/*/inscricoes").hasRole("PARTICIPANTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/eventos/*/inscricoes").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/inscricoes/*/cancelar").hasAnyRole("ADMIN", "PARTICIPANTE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/inscricoes/*/presenca").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/inscricoes/*/certificado").hasAnyRole("ADMIN", "ORGANIZADOR")
                        // usuários
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/*/inscricoes", "/api/v1/usuarios/*/certificados").authenticated()
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        // categorias e eventos
                        .requestMatchers(HttpMethod.POST, "/api/v1/categorias/**", "/api/v1/eventos/**").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**", "/api/v1/eventos/**").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/eventos/**").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**", "/api/v1/eventos/**").hasRole("ADMIN")
                        // o resto exige apenas estar logado
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint((req, resp, ex) -> escrever(resp, 401, "Não autenticado",
                                "Token ausente, inválido ou expirado.", req.getRequestURI()))
                        .accessDeniedHandler((req, resp, ex) -> escrever(resp, 403, "Acesso negado",
                                "Seu perfil não tem permissão para esta operação.", req.getRequestURI())));
        return http.build();
    }

    private void escrever(HttpServletResponse resp, int status, String erro, String mensagem, String caminho)
            throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        if (status == 401) {
            resp.setHeader("WWW-Authenticate", "Bearer");
        }
        resp.getWriter().write("{\"status\":" + status + ",\"erro\":\"" + erro + "\",\"mensagem\":\""
                + mensagem + "\",\"caminho\":\"" + caminho + "\"}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("perfil");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}