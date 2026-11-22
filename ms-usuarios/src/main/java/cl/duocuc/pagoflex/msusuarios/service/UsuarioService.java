package cl.duocuc.pagoflex.msusuarios.service;

import cl.duocuc.pagoflex.msusuarios.dto.CambiarClaveDTO;
import cl.duocuc.pagoflex.msusuarios.dto.LoginDTO;
import cl.duocuc.pagoflex.msusuarios.dto.RegistroDTO;
import cl.duocuc.pagoflex.msusuarios.dto.UsuarioDTO;
import cl.duocuc.pagoflex.msusuarios.model.Usuario;
import cl.duocuc.pagoflex.msusuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    public static final long ROL_USUARIO = 2L;

    private final UsuarioRepository usuarioRepository;

    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(u.getId(), u.getRut(), u.getNombre(), u.getCorreo(), u.getTelefono(), u.getRolId());
    }

    public String hash(String correo, String clave) {
        try {
            String texto = correo.trim().toLowerCase() + ":" + clave;
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();
            for (byte b : bytes) {
                resultado.append(String.format("%02x", b));
            }
            return resultado.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<UsuarioDTO> obtenerTodos() {
        List<UsuarioDTO> lista = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAll()) {
            lista.add(toDTO(u));
        }
        return lista;
    }

    public Optional<UsuarioDTO> buscarPorRut(String rut) {
        Optional<Usuario> op = usuarioRepository.findByRut(rut);
        if (op.isPresent()) {
            return Optional.of(toDTO(op.get()));
        }
        return Optional.empty();
    }

    public UsuarioDTO login(LoginDTO datos) {
        Optional<Usuario> op = usuarioRepository.findByCorreo(datos.getCorreo().trim().toLowerCase());
        if (op.isEmpty() || op.get().getClaveHash().isEmpty()
                || !op.get().getClaveHash().equals(hash(datos.getCorreo(), datos.getClave()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
        return toDTO(op.get());
    }

    public UsuarioDTO registrar(RegistroDTO datos) {
        String correo = datos.getCorreo().trim().toLowerCase();
        Optional<Usuario> conEseRut = usuarioRepository.findByRut(datos.getRut());
        if (conEseRut.isPresent() && !conEseRut.get().getClaveHash().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con este RUT");
        }
        Optional<Usuario> conEseCorreo = usuarioRepository.findByCorreo(correo);
        boolean correoDeOtro = conEseCorreo.isPresent()
                && (conEseRut.isEmpty() || !conEseCorreo.get().getId().equals(conEseRut.get().getId()));
        if (correoDeOtro) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con este correo");
        }
        Usuario usuario = conEseRut.orElse(new Usuario());
        usuario.setRut(datos.getRut());
        usuario.setNombre(datos.getNombre().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(datos.getTelefono());
        usuario.setClaveHash(hash(correo, datos.getClave()));
        usuario.setRolId(ROL_USUARIO);
        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO crearPersona(UsuarioDTO datos) {
        String correo = datos.getCorreo().trim().toLowerCase();
        if (usuarioRepository.findByRut(datos.getRut()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese RUT ya está registrado");
        }
        if (usuarioRepository.findByCorreo(correo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado");
        }
        Usuario usuario = new Usuario(null, datos.getRut(), datos.getNombre().trim(), correo, datos.getTelefono(), "", ROL_USUARIO);
        return toDTO(usuarioRepository.save(usuario));
    }

    public void cambiarClave(Long id, CambiarClaveDTO datos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!usuario.getClaveHash().equals(hash(usuario.getCorreo(), datos.getActual()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        usuario.setClaveHash(hash(usuario.getCorreo(), datos.getNueva()));
        usuarioRepository.save(usuario);
    }
}
