package cl.duocuc.pagoflex.mscompromisos.controller;

import cl.duocuc.pagoflex.mscompromisos.model.Logro;
import cl.duocuc.pagoflex.mscompromisos.service.LogroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
@RequiredArgsConstructor
@Tag(name = "Logros", description = "Reconocimientos por pagar a tiempo")
public class LogroController {

    private final LogroService logroService;

    @Operation(summary = "Lista los logros de una persona")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Logro>> obtenerDeUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(logroService.obtenerDeUsuario(idUsuario));
    }

    @Operation(summary = "Otorga un logro (si la persona ya lo tiene, no hace nada)")
    @PostMapping
    public ResponseEntity<Void> otorgar(@RequestBody Logro logro) {
        if (logroService.otorgar(logro)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.ok().build();
    }
}
