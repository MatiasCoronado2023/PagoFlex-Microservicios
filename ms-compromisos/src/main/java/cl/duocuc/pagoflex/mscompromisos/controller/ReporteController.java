package cl.duocuc.pagoflex.mscompromisos.controller;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.ReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.ResolverReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.TomarReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes de problemas con un compromiso")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Lista todos los reportes (administrador)")
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> obtenerTodos() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @Operation(summary = "Lista los reportes de una persona")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReporteDTO>> obtenerDeUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(reporteService.obtenerDeUsuario(idUsuario));
    }

    @Operation(summary = "Crea un reporte; el compromiso queda En revisión")
    @PostMapping
    public ResponseEntity<ReporteDTO> crear(@RequestBody NuevoReporteDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.crear(datos));
    }

    @Operation(summary = "Un administrador toma el reporte")
    @PutMapping("/{id}/tomar")
    public ResponseEntity<ReporteDTO> tomar(@PathVariable Long id, @RequestBody TomarReporteDTO datos) {
        return ResponseEntity.ok(reporteService.tomar(id, datos));
    }

    @Operation(summary = "Resuelve el reporte con una respuesta para la persona")
    @PutMapping("/{id}/resolver")
    public ResponseEntity<ReporteDTO> resolver(@PathVariable Long id, @RequestBody ResolverReporteDTO datos) {
        return ResponseEntity.ok(reporteService.resolver(id, datos));
    }
}
