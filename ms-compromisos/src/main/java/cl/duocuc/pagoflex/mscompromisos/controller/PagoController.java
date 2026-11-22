package cl.duocuc.pagoflex.mscompromisos.controller;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoPagoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.PagoDTO;
import cl.duocuc.pagoflex.mscompromisos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Pagos simulados y sus comprobantes")
public class PagoController {

    private final PagoService pagoService;

    @Operation(summary = "Lista todos los pagos (administrador)")
    @GetMapping
    public ResponseEntity<List<PagoDTO>> obtenerTodos() {
        return ResponseEntity.ok(pagoService.obtenerTodos());
    }

    @Operation(summary = "Lista los pagos de una persona")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PagoDTO>> obtenerDeUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(pagoService.obtenerDeUsuario(idUsuario));
    }

    @Operation(summary = "Paga un compromiso y genera su comprobante")
    @PostMapping
    public ResponseEntity<PagoDTO> pagar(@RequestBody NuevoPagoDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.pagar(datos));
    }
}
