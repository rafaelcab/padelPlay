package com.padelplay.server.service;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.UsuarioRepository;
import com.padelplay.server.session.SessionData;
import com.padelplay.server.session.SessionStore;
import com.padelplay.common.dto.LoginResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SessionStore sessionStore;
    

    public boolean registrar(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return false; // email ya existe
        }

        Usuario entity = new Usuario();
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setPassword(usuario.getPassword());

        entity.setPassword(passwordEncoder.encode(usuario.getPassword()));

        usuarioRepository.save(entity);
        return true;
    }

    
    public LoginResponseDto login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return null;
        }

        SessionData sessionData = new SessionData(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );

        String token = sessionStore.createSession(sessionData);

        return new LoginResponseDto(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }

    public SessionData getSession(String token) {
        return sessionStore.getSession(token);
    }

    public void logout(String token) {
        sessionStore.removeSession(token);
    }
}
