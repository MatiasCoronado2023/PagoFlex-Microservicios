package cl.duocuc.pagoflex.msusuarios.controller;

import cl.duocuc.pagoflex.msusuarios.dto.UsuarioDTO;
import cl.duocuc.pagoflex.msusuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO camila;

    @BeforeEach
    void preparar() {
        camila = new UsuarioDTO(1L, "15.834.207-3", "Camila Rojas", "crojas@correo.ejemplo", null, 2L);
    }

    @Test
    void buscarPorRut_existente_responde200() {
        when(usuarioService.buscarPorRut("15.834.207-3")).thenReturn(Optional.of(camila));

        ResponseEntity<UsuarioDTO> respuesta = usuarioController.buscarPorRut("15.834.207-3");

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals("Camila Rojas", respuesta.getBody().getNombre());
    }

    @Test
    void buscarPorRut_noExiste_responde404() {
        when(usuarioService.buscarPorRut("11.111.111-1")).thenReturn(Optional.empty());

        ResponseEntity<UsuarioDTO> respuesta = usuarioController.buscarPorRut("11.111.111-1");

        assertEquals(404, respuesta.getStatusCode().value());
    }

    @Test
    void obtenerTodos_devuelveLaListaDelServicio() {
        when(usuarioService.obtenerTodos()).thenReturn(List.of(camila));

        ResponseEntity<List<UsuarioDTO>> respuesta = usuarioController.obtenerTodos();

        assertEquals(1, respuesta.getBody().size());
    }
}
