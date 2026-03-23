package com.padelplay.server.service;
import com.padelplay.server.entity.Usuario;
import com.padelplay.server.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean registrar(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return false; // email ya existe
        }

        Usuario entity = new Usuario();
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail());
        entity.setPassword(usuario.getPassword());

        usuarioRepository.save(entity);
        return true;
    }
}
