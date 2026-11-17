package com.eventos.api.service;

import com.eventos.api.dto.CadastroRequest;
import com.eventos.api.dto.LoginRequest;
import com.eventos.api.dto.LoginResponse;
import com.eventos.api.dto.UsuarioResponse;
import com.eventos.api.exception.ConflitoException;
import com.eventos.api.exception.CredenciaisInvalidasException;
import com.eventos.api.exception.RecursoNaoEncontradoException;
import com.eventos.api.model.Perfil;
import com.eventos.api.model.Usuario;
import com.eventos.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration-minutes:60}")
    private long expirationMinutes;

    public LoginResponse login(LoginRequest req) {
        // mesma mensagem para e-mail ou senha errados: não revela se o e-mail existe
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos."));

        if (!usuario.isAtivo() || !passwordEncoder.matches(req.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos.");
        }

        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getEmail())
                .issuedAt(agora)
                .expiresAt(agora.plus(expirationMinutes, ChronoUnit.MINUTES))
                .claim("perfil", usuario.getPerfil().name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new LoginResponse(token, "Bearer", expirationMinutes * 60, usuario.getPerfil().name());
    }

    public UsuarioResponse cadastrar(CadastroRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ConflitoException("Já existe um usuário cadastrado com esse e-mail.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        usuario.setSenha(passwordEncoder.encode(req.senha()));
        usuario.setPerfil(Perfil.PARTICIPANTE); // o perfil nunca vem do cliente
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    public UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return UsuarioResponse.from(usuario);
    }
}