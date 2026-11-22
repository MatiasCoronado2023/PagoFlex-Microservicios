package cl.duocuc.pagoflex.msusuarios.service;

import cl.duocuc.pagoflex.msusuarios.dto.CambiarClaveDTO;
import cl.duocuc.pagoflex.msusuarios.dto.LoginDTO;
import cl.duocuc.pagoflex.msusuarios.dto.RegistroDTO;
import cl.duocuc.pagoflex.msusuarios.dto.UsuarioDTO;
import cl.duocuc.pagoflex.msusuarios.model.Usuario;
import cl.duocuc.pagoflex.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario camila;

    @BeforeEach
    void preparar() {
        camila = new Usuario(1L, "15.834.207-3", "Camila Rojas", "crojas@correo.ejemplo", null,
                usuarioService.hash("crojas@correo.ejemplo", "pago123"), 2L);
    }

    @Test
    void hash_noDependeDeMayusculasNiEspaciosDelCorreo() {
        assertEquals(usuarioService.hash("ana@duoc.cl", "pago123"), usuarioService.hash("  ANA@duoc.cl ", "pago123"));
        assertNotEquals(usuarioService.hash("ana@duoc.cl", "pago123"), usuarioService.hash("ana@duoc.cl", "otra"));
    }

    @Test
    void login_conClaveCorrecta_devuelveElUsuario() {
        when(usuarioRepository.findByCorreo("crojas@correo.ejemplo")).thenReturn(Optional.of(camila));

        UsuarioDTO resultado = usuarioService.login(new LoginDTO("CRojas@correo.ejemplo", "pago123"));

        assertEquals(1L, resultado.getId());
        assertEquals("Camila Rojas", resultado.getNombre());
    }

    @Test
    void login_conClaveIncorrecta_responde401() {
        when(usuarioRepository.findByCorreo("crojas@correo.ejemplo")).thenReturn(Optional.of(camila));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> usuarioService.login(new LoginDTO("crojas@correo.ejemplo", "mala")));

        assertEquals(401, error.getStatusCode().value());
        assertEquals("Correo o contraseña incorrectos", error.getReason());
    }

    @Test
    void registrar_conRutQueYaTieneCuenta_responde409() {
        when(usuarioRepository.findByRut("15.834.207-3")).thenReturn(Optional.of(camila));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> usuarioService.registrar(new RegistroDTO("Camila", "15.834.207-3", "otro@correo.cl", null, "123456")));

        assertEquals(409, error.getStatusCode().value());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_personaRegistradaPorElAdministrador_activaSuCuenta() {
        Usuario sinCuenta = new Usuario(20L, "11.111.111-1", "Pedro", "pedro@correo.cl", null, "", 2L);
        when(usuarioRepository.findByRut("11.111.111-1")).thenReturn(Optional.of(sinCuenta));
        when(usuarioRepository.findByCorreo("pedro@correo.cl")).thenReturn(Optional.of(sinCuenta));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        UsuarioDTO resultado = usuarioService.registrar(new RegistroDTO("Pedro Soto", "11.111.111-1", "pedro@correo.cl", null, "123456"));

        assertEquals(20L, resultado.getId());
        assertEquals("Pedro Soto", resultado.getNombre());
        assertEquals(usuarioService.hash("pedro@correo.cl", "123456"), sinCuenta.getClaveHash());
    }

    @Test
    void crearPersona_conCorreoRepetido_responde409() {
        when(usuarioRepository.findByRut("11.111.111-1")).thenReturn(Optional.empty());
        when(usuarioRepository.findByCorreo("crojas@correo.ejemplo")).thenReturn(Optional.of(camila));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> usuarioService.crearPersona(new UsuarioDTO(null, "11.111.111-1", "Pedro", "crojas@correo.ejemplo", "+56911112222", 2L)));

        assertEquals("Ese correo ya está registrado", error.getReason());
    }

    @Test
    void cambiarClave_conClaveActualIncorrecta_responde400() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(camila));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> usuarioService.cambiarClave(1L, new CambiarClaveDTO("mala", "nueva123")));

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void cambiarClave_conClaveActualCorrecta_guardaLaNueva() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(camila));

        usuarioService.cambiarClave(1L, new CambiarClaveDTO("pago123", "nueva123"));

        assertEquals(usuarioService.hash("crojas@correo.ejemplo", "nueva123"), camila.getClaveHash());
        verify(usuarioRepository).save(camila);
    }
}
