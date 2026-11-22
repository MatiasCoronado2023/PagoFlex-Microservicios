package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.dto.AnularDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.CompromisoDTO;
import cl.duocuc.pagoflex.mscompromisos.dto.NuevoCompromisoDTO;
import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.model.Empresa;
import cl.duocuc.pagoflex.mscompromisos.repository.CompromisoRepository;
import cl.duocuc.pagoflex.mscompromisos.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompromisoService {

    private final CompromisoRepository compromisoRepository;
    private final EmpresaRepository empresaRepository;

    public CompromisoDTO toDTO(Compromiso c) {
        return new CompromisoDTO(c.getId(), c.getFolio(), c.getIdUsuario(), c.getNombreUsuario(), c.getRutUsuario(),
                c.getEmpresa().getId(), c.getEmpresa().getNombre(), c.getEmpresa().getRecargo(), c.getConcepto(),
                c.getNumeroCuota(), c.getTotalCuotas(), c.getMonto(), c.getFechaVencimiento().toString(),
                c.getEstado(), c.getMotivoAnulacion());
    }

    private List<CompromisoDTO> toLista(List<Compromiso> compromisos) {
        List<CompromisoDTO> lista = new ArrayList<>();
        for (Compromiso c : compromisos) {
            lista.add(toDTO(c));
        }
        return lista;
    }

    public List<Empresa> obtenerEmpresas() {
        return empresaRepository.findAllByOrderByNombre();
    }

    public List<CompromisoDTO> obtenerTodos() {
        return toLista(compromisoRepository.findAllByOrderByFechaVencimientoDesc());
    }

    public List<CompromisoDTO> obtenerDeUsuario(Long idUsuario) {
        return toLista(compromisoRepository.findByIdUsuarioOrderByFechaVencimiento(idUsuario));
    }

    @Transactional
    public CompromisoDTO registrar(NuevoCompromisoDTO datos) {
        if (datos.getConcepto() == null || datos.getConcepto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El concepto es obligatorio");
        }
        if (datos.getMonto() == null || datos.getMonto() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto debe ser mayor que 0");
        }
        if (datos.getNumeroCuota() != null && datos.getTotalCuotas() != null && datos.getNumeroCuota() > datos.getTotalCuotas()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cuota no puede ser mayor que el total de cuotas");
        }
        Empresa empresa = empresaRepository.findById(datos.getIdEmpresa())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La empresa no existe"));
        for (Compromiso c : compromisoRepository.findByIdUsuarioOrderByFechaVencimiento(datos.getIdUsuario())) {
            boolean mismaEmpresa = c.getEmpresa().getId().equals(empresa.getId());
            boolean mismoConcepto = c.getConcepto().equalsIgnoreCase(datos.getConcepto().trim());
            boolean mismaCuota = Objects.equals(c.getNumeroCuota(), datos.getNumeroCuota());
            if (mismaEmpresa && mismoConcepto && mismaCuota) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese compromiso ya está registrado para esta persona y empresa");
            }
        }
        Compromiso nuevo = new Compromiso();
        nuevo.setIdUsuario(datos.getIdUsuario());
        nuevo.setNombreUsuario(datos.getNombreUsuario());
        nuevo.setRutUsuario(datos.getRutUsuario());
        nuevo.setEmpresa(empresa);
        nuevo.setConcepto(datos.getConcepto().trim());
        nuevo.setNumeroCuota(datos.getNumeroCuota());
        nuevo.setTotalCuotas(datos.getTotalCuotas());
        nuevo.setMonto(datos.getMonto());
        nuevo.setFechaVencimiento(LocalDate.parse(datos.getVencimiento()));
        nuevo.setEstado("PENDIENTE");
        nuevo.setIdAdminRegistro(datos.getIdAdmin());
        nuevo.setFechaRegistro(datos.getFecha());
        Compromiso guardado = compromisoRepository.save(nuevo);
        guardado.setFolio("CP-" + datos.getFecha().substring(0, 4) + "-" + String.format("%04d", guardado.getId()));
        return toDTO(guardado);
    }

    @Transactional
    public CompromisoDTO anular(Long id, AnularDTO datos) {
        Compromiso c = compromisoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compromiso no encontrado"));
        if (datos.getMotivo() == null || datos.getMotivo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elige un motivo");
        }
        if (!c.getEstado().equals("PENDIENTE") && !c.getEstado().equals("EN_REVISION")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden anular compromisos pendientes, vencidos o en revisión");
        }
        c.setEstado("ANULADO");
        c.setMotivoAnulacion(datos.getMotivo());
        c.setIdAdminAnulacion(datos.getIdAdmin());
        c.setFechaAnulacion(datos.getFecha());
        return toDTO(compromisoRepository.save(c));
    }
}
