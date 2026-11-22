package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.ReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.ResolverReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.TomarReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.model.Reporte;
import cl.duocuc.pagoflex.mscompromisos.repository.CompromisoRepository;
import cl.duocuc.pagoflex.mscompromisos.repository.ReporteRepository;
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
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private CompromisoRepository compromisoRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Reporte reporte(Compromiso c, String estado, Long idAdmin) {
        return new Reporte(11L, "REP-0011", c, "Ya pagué por otro medio", null, "2026-10-03 09:30", estado,
                idAdmin, idAdmin == null ? null : "Andrés Lagos", null, null, null);
    }

    @Test
    void crear_reporte_dejaElCompromisoEnRevision() {
        Compromiso c = DatosDePrueba.compromiso("PENDIENTE");
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(c));
        when(reporteRepository.findByCompromisoIdAndEstadoNot(101L, "RESUELTO")).thenReturn(List.of());
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(invocacion -> {
            Reporte r = invocacion.getArgument(0);
            r.setId(12L);
            return r;
        });

        ReporteDTO dto = reporteService.crear(new NuevoReporteDTO(101L, "No reconozco este cobro", null, "2026-10-06 10:00"));

        assertEquals("REP-0012", dto.getNumero());
        assertEquals("ABIERTO", dto.getEstado());
        assertEquals("EN_REVISION", c.getEstado());
    }

    @Test
    void crear_conReporteEnCurso_responde409() {
        Compromiso c = DatosDePrueba.compromiso("EN_REVISION");
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(c));
        when(reporteRepository.findByCompromisoIdAndEstadoNot(101L, "RESUELTO")).thenReturn(List.of(reporte(c, "ABIERTO", null)));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> reporteService.crear(new NuevoReporteDTO(101L, "Otro", "texto", "2026-10-06 10:00")));

        assertEquals(409, error.getStatusCode().value());
    }

    @Test
    void crear_motivoOtroSinComentario_responde400() {
        when(compromisoRepository.findById(101L)).thenReturn(Optional.of(DatosDePrueba.compromiso("PENDIENTE")));
        when(reporteRepository.findByCompromisoIdAndEstadoNot(101L, "RESUELTO")).thenReturn(List.of());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> reporteService.crear(new NuevoReporteDTO(101L, "Otro", " ", "2026-10-06 10:00")));

        assertEquals(400, error.getStatusCode().value());
    }

    @Test
    void tomar_reporteAbierto_quedaEnGestionConResponsable() {
        Reporte r = reporte(DatosDePrueba.compromiso("EN_REVISION"), "ABIERTO", null);
        when(reporteRepository.findById(11L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(r)).thenReturn(r);

        ReporteDTO dto = reporteService.tomar(11L, new TomarReporteDTO(9L, "Andrés Lagos", "2026-10-06 10:00"));

        assertEquals("EN_GESTION", dto.getEstado());
        assertEquals("Andrés Lagos", dto.getNombreAdmin());
        assertEquals("2026-10-06 10:00", r.getFechaToma());
    }

    @Test
    void resolver_porOtroAdministrador_responde409() {
        Reporte r = reporte(DatosDePrueba.compromiso("EN_REVISION"), "EN_GESTION", 10L);
        when(reporteRepository.findById(11L)).thenReturn(Optional.of(r));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> reporteService.resolver(11L, new ResolverReporteDTO(9L, "Listo", false, null, "2026-10-06 10:00")));

        assertEquals("Solo quien tomó el reporte puede resolverlo", error.getReason());
    }

    @Test
    void resolver_sinAnular_elCompromisoVuelveAPendiente() {
        Compromiso c = DatosDePrueba.compromiso("EN_REVISION");
        Reporte r = reporte(c, "EN_GESTION", 9L);
        when(reporteRepository.findById(11L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(r)).thenReturn(r);

        ReporteDTO dto = reporteService.resolver(11L, new ResolverReporteDTO(9L, "El cobro es correcto", false, null, "2026-10-06 10:00"));

        assertEquals("RESUELTO", dto.getEstado());
        assertEquals("PENDIENTE", c.getEstado());
    }

    @Test
    void resolver_anulando_elCompromisoQuedaAnulado() {
        Compromiso c = DatosDePrueba.compromiso("EN_REVISION");
        Reporte r = reporte(c, "EN_GESTION", 9L);
        when(reporteRepository.findById(11L)).thenReturn(Optional.of(r));
        when(reporteRepository.save(r)).thenReturn(r);

        reporteService.resolver(11L, new ResolverReporteDTO(9L, "Se anuló", true, "Pagado directamente a la empresa", "2026-10-06 10:00"));

        assertEquals("ANULADO", c.getEstado());
        assertEquals("Pagado directamente a la empresa", c.getMotivoAnulacion());
    }
}
