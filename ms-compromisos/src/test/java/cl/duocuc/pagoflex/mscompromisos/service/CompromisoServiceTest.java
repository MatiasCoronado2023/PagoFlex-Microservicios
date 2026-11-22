package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.dto.AnularDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.CompromisoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.NuevoCompromisoDTO;
import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.repository.CompromisoRepository;
import cl.duocuc.pagoflex.mscompromisos.repository.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompromisoServiceTest {

    @Mock
    private CompromisoRepository compromisoRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private CompromisoService compromisoService;

    private NuevoCompromisoDTO nuevo(String concepto, Integer cuota, Integer total, Integer monto) {
        return new NuevoCompromisoDTO(1L, "Camila Rojas", "15.834.207-3", 8L, concepto, cuota, total, monto,
                "2026-11-10", 9L, "2026-10-06 10:00");
    }

    @Test
    void toDTO_copiaLosDatosDeLaEmpresa() {
        CompromisoDTO dto = compromisoService.toDTO(DatosDePrueba.compromiso("PENDIENTE"));

        assertEquals("Plaza Oriente", dto.getEmpresa());
        assertEquals(7500, dto.getRecargoEmpresa());
        assertEquals("2026-10-10", dto.getVencimiento());
    }

    @Test
    void registrar_compromisoNuevo_quedaPendienteConFolio() {
        when(empresaRepository.findById(8L)).thenReturn(Optional.of(DatosDePrueba.empresa()));
        when(compromisoRepository.findByIdUsuarioOrderByFechaVencimiento(1L)).thenReturn(List.of());
        when(compromisoRepository.save(any(Compromiso.class))).thenAnswer(invocacion -> {
            Compromiso c = invocacion.getArgument(0);
            c.setId(1200L);
            return c;
        });

        CompromisoDTO dto = compromisoService.registrar(nuevo("Arriendo noviembre", 11, 12, 520000));

        assertEquals("PENDIENTE", dto.getEstado());
        assertEquals("CP-2026-1200", dto.getFolio());
    }

    @Test
    void registrar_compromisoRepetido_responde409() {
        when(empresaRepository.findById(8L)).thenReturn(Optional.of(DatosDePrueba.empresa()));
        when(compromisoRepository.findByIdUsuarioOrderByFechaVencimiento(1L))
                .thenReturn(List.of(DatosDePrueba.compromiso("PENDIENTE")));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> compromisoService.registrar(nuevo("arriendo OCTUBRE", 10, 12, 520000)));

        assertEquals(409, error.getStatusCode().value());
        verify(compromisoRepository, never()).save(any());
    }

    @Test
    void registrar_cuotaMayorQueElTotal_responde400() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> compromisoService.registrar(nuevo("Arriendo", 13, 12, 520000)));

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void registrar_montoCero_responde400() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> compromisoService.registrar(nuevo("Arriendo", null, null, 0)));

        assertEquals("El monto debe ser mayor que 0", error.getReason());
    }

    @Test
    void anular_compromisoPendiente_quedaAnuladoConMotivo() {
        Compromiso c = DatosDePrueba.compromiso("PENDIENTE");
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(c));
        when(compromisoRepository.save(c)).thenReturn(c);

        CompromisoDTO dto = compromisoService.anular(101L, new AnularDTO("Repactación", 9L, "2026-10-06 11:00"));

        assertEquals("ANULADO", dto.getEstado());
        assertEquals("Repactación", dto.getMotivoAnulacion());
        assertEquals(9L, c.getIdAdminAnulacion());
    }

    @Test
    void anular_compromisoPagado_responde409() {
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(DatosDePrueba.compromiso("PAGADO")));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> compromisoService.anular(101L, new AnularDTO("Repactación", 9L, "2026-10-06 11:00")));

        assertEquals(409, error.getStatusCode().value());
    }
}
