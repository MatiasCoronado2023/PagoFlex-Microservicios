package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoPagoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.PagoDTO;
import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.model.Pago;
import cl.duocuc.pagoflex.mscompromisos.repository.CompromisoRepository;
import cl.duocuc.pagoflex.mscompromisos.repository.PagoRepository;
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
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private CompromisoRepository compromisoRepository;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void pagar_compromisoPendiente_generaComprobanteYQuedaPagado() {
        Compromiso c = DatosDePrueba.compromiso("PENDIENTE");
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(c));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocacion -> {
            Pago p = invocacion.getArgument(0);
            p.setId(454L);
            return p;
        });

        PagoDTO pago = pagoService.pagar(new NuevoPagoDTO(101L, "Transferencia", 7500, "2026-10-06 12:00"));

        assertEquals("CPR-000454", pago.getNumeroComprobante());
        assertEquals(527500, pago.getTotal());
        assertEquals("App", pago.getCanal());
        assertEquals("PAGADO", c.getEstado());
        verify(compromisoRepository).save(c);
    }

    @Test
    void pagar_compromisoYaPagado_responde409YNoGuardaNada() {
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(DatosDePrueba.compromiso("PAGADO")));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoService.pagar(new NuevoPagoDTO(101L, "Transferencia", 0, "2026-10-06 12:00")));

        assertEquals(409, error.getStatusCode().value());
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void pagar_compromisoEnRevision_responde409() {
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(DatosDePrueba.compromiso("EN_REVISION")));

        assertThrows(ResponseStatusException.class,
                () -> pagoService.pagar(new NuevoPagoDTO(101L, "Transferencia", 0, "2026-10-06 12:00")));
    }

    @Test
    void pagar_compromisoQueNoExiste_responde404() {
        when(compromisoRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoService.pagar(new NuevoPagoDTO(999L, "Transferencia", 0, "2026-10-06 12:00")));

        assertEquals(404, error.getStatusCode().value());
    }
}
