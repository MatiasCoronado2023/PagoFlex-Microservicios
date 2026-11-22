package cl.duocuc.pagoflex.mscompromisos.controller;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoPagoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.PagoDTO;
import cl.duocuc.pagoflex.mscompromisos.service.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private PagoDTO pago() {
        return new PagoDTO(454L, "CPR-000454", 101L, "2026-10-06 12:00", 520000, 0, 520000, "Transferencia", "App",
                "CP-2026-0101", "Arriendo octubre", 10, 12, "2026-10-10", "Plaza Oriente", "Camila Rojas", "15.834.207-3");
    }

    @Test
    void pagar_responde201ConElComprobante() {
        NuevoPagoDTO datos = new NuevoPagoDTO(101L, "Transferencia", 0, "2026-10-06 12:00");
        when(pagoService.pagar(datos)).thenReturn(pago());

        ResponseEntity<PagoDTO> respuesta = pagoController.pagar(datos);

        assertEquals(201, respuesta.getStatusCode().value());
        assertEquals("CPR-000454", respuesta.getBody().getNumeroComprobante());
    }

    @Test
    void obtenerDeUsuario_devuelveLosPagosDeEsaPersona() {
        when(pagoService.obtenerDeUsuario(1L)).thenReturn(List.of(pago()));

        ResponseEntity<List<PagoDTO>> respuesta = pagoController.obtenerDeUsuario(1L);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals(1, respuesta.getBody().size());
    }
}
