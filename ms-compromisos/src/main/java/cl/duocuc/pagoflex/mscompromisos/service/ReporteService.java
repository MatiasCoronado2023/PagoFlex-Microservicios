package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.ReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.ResolverReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.TomarReporteDTO;
import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.model.Reporte;
import cl.duocuc.pagoflex.mscompromisos.repository.CompromisoRepository;
import cl.duocuc.pagoflex.mscompromisos.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final CompromisoRepository compromisoRepository;

    private ReporteDTO toDTO(Reporte r) {
        Compromiso c = r.getCompromiso();
        return new ReporteDTO(r.getId(), r.getNumero(), c.getId(), c.getFolio(), c.getConcepto(), c.getEmpresa().getNombre(),
                c.getNombreUsuario(), r.getMotivo(), r.getComentario(), r.getFecha(), r.getEstado(), r.getIdAdmin(),
                r.getNombreAdmin(), r.getRespuesta(), r.getFechaResolucion());
    }

    private List<ReporteDTO> toLista(List<Reporte> reportes) {
        List<ReporteDTO> lista = new ArrayList<>();
        for (Reporte r : reportes) {
            lista.add(toDTO(r));
        }
        return lista;
    }

    private Reporte buscar(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));
    }

    public List<ReporteDTO> obtenerTodos() {
        return toLista(reporteRepository.findAllByOrderByFecha());
    }

    public List<ReporteDTO> obtenerDeUsuario(Long idUsuario) {
        return toLista(reporteRepository.findByCompromisoIdUsuarioOrderByFechaDesc(idUsuario));
    }

    @Transactional
    public ReporteDTO crear(NuevoReporteDTO datos) {
        Compromiso c = compromisoRepository.findById(datos.getIdCompromiso())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compromiso no encontrado"));
        if (!reporteRepository.findByCompromisoIdAndEstadoNot(c.getId(), "RESUELTO").isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya tienes un reporte en curso para este compromiso");
        }
        if (!c.getEstado().equals("PENDIENTE")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este compromiso no se puede reportar");
        }
        if (datos.getMotivo().equals("Otro") && (datos.getComentario() == null || datos.getComentario().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario es obligatorio si eliges \"Otro\"");
        }
        Reporte reporte = new Reporte();
        reporte.setCompromiso(c);
        reporte.setMotivo(datos.getMotivo());
        reporte.setComentario(datos.getComentario());
        reporte.setFecha(datos.getFecha());
        reporte.setEstado("ABIERTO");
        Reporte guardado = reporteRepository.save(reporte);
        guardado.setNumero("REP-" + String.format("%04d", guardado.getId()));
        c.setEstado("EN_REVISION");
        compromisoRepository.save(c);
        return toDTO(guardado);
    }

    @Transactional
    public ReporteDTO tomar(Long id, TomarReporteDTO datos) {
        Reporte reporte = buscar(id);
        if (!reporte.getEstado().equals("ABIERTO")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Otro administrador ya tomó este reporte");
        }
        reporte.setEstado("EN_GESTION");
        reporte.setIdAdmin(datos.getIdAdmin());
        reporte.setNombreAdmin(datos.getNombreAdmin());
        reporte.setFechaToma(datos.getFecha());
        return toDTO(reporteRepository.save(reporte));
    }

    @Transactional
    public ReporteDTO resolver(Long id, ResolverReporteDTO datos) {
        Reporte reporte = buscar(id);
        if (!reporte.getEstado().equals("EN_GESTION") || !reporte.getIdAdmin().equals(datos.getIdAdmin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo quien tomó el reporte puede resolverlo");
        }
        if (datos.getRespuesta() == null || datos.getRespuesta().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe una respuesta para la persona");
        }
        Compromiso c = reporte.getCompromiso();
        if (c.getEstado().equals("EN_REVISION")) {
            if (datos.isAnular()) {
                if (datos.getMotivoAnulacion() == null || datos.getMotivoAnulacion().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elige el motivo de la anulación");
                }
                c.setEstado("ANULADO");
                c.setMotivoAnulacion(datos.getMotivoAnulacion());
                c.setIdAdminAnulacion(datos.getIdAdmin());
                c.setFechaAnulacion(datos.getFecha());
            } else {
                c.setEstado("PENDIENTE");
            }
            compromisoRepository.save(c);
        }
        reporte.setEstado("RESUELTO");
        reporte.setRespuesta(datos.getRespuesta().trim());
        reporte.setFechaResolucion(datos.getFecha());
        return toDTO(reporteRepository.save(reporte));
    }
}
