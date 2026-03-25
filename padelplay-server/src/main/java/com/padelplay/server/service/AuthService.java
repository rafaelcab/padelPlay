package com.padelplay.server.service;


import com.padelplay.common.dto.AuthResponseDto;
import com.padelplay.common.dto.GoogleAuthRequestDto;
import com.padelplay.common.dto.RegistroRequestDto;
import com.padelplay.common.dto.LoginRequestDto;

import com.padelplay.server.entity.AuthProvider;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.UsuarioRepository;
import com.padelplay.server.service.GoogleAuthService.GoogleUserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private GoogleAuthService googleAuthService;

    public Optional<AuthResponseDto> login(LoginRequestDto request) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email());

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getPassword() == null ||
            !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            return Optional.empty();
        }

        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());

        boolean necesitaSeleccionarPerfil = usuario.requiereSeleccionPerfil();

        AuthResponseDto response = new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getPictureUrl(),
                necesitaSeleccionarPerfil
        );

        return Optional.of(response);
    }

    
    public Optional<AuthResponseDto> register(RegistroRequestDto request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setAuthProvider(AuthProvider.LOCAL);

        usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());

        AuthResponseDto response = new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                null,
                true
        );

        return Optional.of(response);
    }

    public AuthResponseDto authenticateWithGoogle(GoogleAuthRequestDto request) {
        GoogleUserInfo googleUser = googleAuthService.verificarToken(request.getCredential());

        if (googleUser == null) {
            return null;
        }

        if (!googleUser.emailVerificado()) {
            return null;
        }

        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(googleUser.email());

        boolean nuevoUsuario;
        Usuario usuario;

        if (usuarioExistente.isPresent()) {
            usuario = usuarioExistente.get();

            if (usuario.getGoogleId() == null) {
                usuario.vincularGoogle(googleUser.googleId(), googleUser.pictureUrl());
                usuarioRepository.save(usuario);
            }

            nuevoUsuario = usuario.requiereSeleccionPerfil();

        } else {
            usuario = new Usuario(
                    googleUser.email(),
                    googleUser.nombre(),
                    googleUser.pictureUrl(),
                    googleUser.googleId(),
                    AuthProvider.GOOGLE
            );
            usuarioRepository.save(usuario);
            nuevoUsuario = true;
        }

        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail());

        return new AuthResponseDto(
                token,
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getPictureUrl(),
                nuevoUsuario
        );
    }

    public Map<String, Object> verifyToken(String token) {
        if (jwtService.validarToken(token)) {
            String email = jwtService.extraerEmail(token);
            return Map.of(
                    "valid", true,
                    "email", email
            );
        }

        return null;
    }
}
