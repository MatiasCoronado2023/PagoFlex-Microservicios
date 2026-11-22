package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.dto.NuevoPagoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.PagoDTO;
import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.model.Pago;
import cl.duocuc.pagoflex.mscompromisos.repository.CompromisoRepository;
import cl.duocuc.pagoflex.mscompromisos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final CompromisoRepository compromisoRepository;

    private PagoDTO toDTO(Pago p) {
        Compromiso c = p.getCompromiso();
        return new PagoDTO(p.getId(), p.getNumeroComprobante(), c.getId(), p.getFechaHora(), p.getMontoOriginal(),
                p.getRecargo(), p.getTotal(), p.getMedio(), p.getCanal(), c.getFolio(), c.getConcepto(),
                c.getNumeroCuota(), c.getTotalCuotas(), c.getFechaVencimiento().toString(), c.getEmpresa().getNombre(),
                c.getNombreUsuario(), c.getRutUsuario());
    }

    private List<PagoDTO> toLista(List<Pago> pagos) {
        List<PagoDTO> lista = new ArrayList<>();
        for (Pago p : pagos) {
            lista.add(toDTO(p));
        }
        return lista;
    }

    public List<PagoDTO> obtenerTodos() {
        return toLista(pagoRepository.findAllByOrderByFechaHoraDesc());
    }

    public List<PagoDTO> obtenerDeUsuario(Long idUsuario) {
        return toLista(pagoRepository.findByCompromisoIdUsuarioOrderByFechaHoraDesc(idUsuario));
    }

    @Transactional
    public PagoDTO pagar(NuevoPagoDTO datos) {
        Compromiso c = compromisoRepository.findById(datos.getIdCompromiso())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compromiso no encontrado"));
        if (!c.getEstado().equals("PENDIENTE")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este compromiso ya no se puede pagar");
        }
        int recargo = datos.getRecargo() == null ? 0 : datos.getRecargo();
        Pago pago = new Pago();
        pago.setCompromiso(c);
        pago.setFechaHora(datos.getFecha());
        pago.setMontoOriginal(c.getMonto());
        pago.setRecargo(recargo);
        pago.setTotal(c.getMonto() + recargo);
        pago.setMedio(datos.getMedio());
        pago.setCanal("App");
        Pago guardado = pagoRepository.save(pago);
        guardado.setNumeroComprobante("CPR-" + String.format("%06d", guardado.getId()));
        c.setEstado("PAGADO");
        compromisoRepository.save(c);
        return toDTO(guardado);
    }
}
