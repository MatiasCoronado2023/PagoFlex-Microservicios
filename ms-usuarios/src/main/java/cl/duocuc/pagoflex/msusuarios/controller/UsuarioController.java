package cl.duocuc.pagoflex.msusuarios.controller;

import cl.duocuc.pagoflex.msusuarios.dto.CambiarClaveDTO;
import cl.duocuc.pagoflex.msusuarios.dto.LoginDTO;
import cl.duocuc.pagoflex.msusuarios.dto.RegistroDTO;
import cl.duocuc.pagoflex.msusuarios.dto.UsuarioDTO;
import cl.duocuc.pagoflex.msusuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Inicio de sesión, registro y datos de las personas")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Lista todos los usuarios")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @Operation(summary = "Busca una persona por su RUT")
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDTO> buscarPorRut(@RequestParam String rut) {
        return usuarioService.buscarPorRut(rut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Inicia sesión con correo y contraseña")
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginDTO datos) {
        return ResponseEntity.ok(usuarioService.login(datos));
    }

    @Operation(summary = "Crea la cuenta de una persona")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody RegistroDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(datos));
    }

    @Operation(summary = "Registra a una persona sin cuenta (lo usa el administrador)")
    @PostMapping("/persona")
    public ResponseEntity<UsuarioDTO> crearPersona(@RequestBody UsuarioDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearPersona(datos));
    }

    @Operation(summary = "Cambia la contraseña de un usuario")
    @PutMapping("/{id}/clave")
    public ResponseEntity<Void> cambiarClave(@PathVariable Long id, @RequestBody CambiarClaveDTO datos) {
        usuarioService.cambiarClave(id, datos);
        return ResponseEntity.noContent().build();
    }
}
