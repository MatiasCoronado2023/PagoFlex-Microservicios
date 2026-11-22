package cl.duocuc.pagoflex.mscompromisos.controller;

import cl.duocuc.pagoflex.mscompromisos.dto.AnularDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.CompromisoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.NuevoCompromisoDTO;
import cl.duocuc.pagoflex.mscompromisos.service.CompromisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compromisos")
@RequiredArgsConstructor
@Tag(name = "Compromisos", description = "Compromisos de pago de las personas")
public class CompromisoController {

    private final CompromisoService compromisoService;

    @Operation(summary = "Lista todos los compromisos (administrador)")
    @GetMapping
    public ResponseEntity<List<CompromisoDTO>> obtenerTodos() {
        return ResponseEntity.ok(compromisoService.obtenerTodos());
    }

    @Operation(summary = "Lista los compromisos de una persona")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<CompromisoDTO>> obtenerDeUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(compromisoService.obtenerDeUsuario(idUsuario));
    }

    @Operation(summary = "Registra un compromiso nuevo en estado Pendiente")
    @PostMapping
    public ResponseEntity<CompromisoDTO> registrar(@RequestBody NuevoCompromisoDTO datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compromisoService.registrar(datos));
    }

    @Operation(summary = "Anula un compromiso con un motivo")
    @PutMapping("/{id}/anular")
    public ResponseEntity<CompromisoDTO> anular(@PathVariable Long id, @RequestBody AnularDTO datos) {
        return ResponseEntity.ok(compromisoService.anular(id, datos));
    }
}
