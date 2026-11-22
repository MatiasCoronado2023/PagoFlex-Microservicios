package cl.duocuc.pagoflex.mscompromisos.controller;

import cl.duocuc.pagoflex.mscompromisos.model.Empresa;
import cl.duocuc.pagoflex.mscompromisos.service.CompromisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas", description = "Empresas clientes y su recargo por atraso")
public class EmpresaController {

    private final CompromisoService compromisoService;

    @Operation(summary = "Lista las empresas")
    @GetMapping
    public ResponseEntity<List<Empresa>> obtenerTodas() {
        return ResponseEntity.ok(compromisoService.obtenerEmpresas());
    }
}
