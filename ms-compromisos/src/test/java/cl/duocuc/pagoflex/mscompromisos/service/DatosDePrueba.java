package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import cl.duocuc.pagoflex.mscompromisos.model.Empresa;

import java.time.LocalDate;

public class DatosDePrueba {

    public static Empresa empresa() {
        return new Empresa(8L, "Plaza Oriente", 7500);
    }

    public static Compromiso compromiso(String estado) {
        Compromiso c = new Compromiso();
        c.setId(101L);
        c.setFolio("CP-2026-0101");
        c.setIdUsuario(1L);
        c.setNombreUsuario("Camila Rojas");
        c.setRutUsuario("15.834.207-3");
        c.setEmpresa(empresa());
        c.setConcepto("Arriendo octubre");
        c.setNumeroCuota(10);
        c.setTotalCuotas(12);
        c.setMonto(520000);
        c.setFechaVencimiento(LocalDate.of(2026, 10, 10));
        c.setEstado(estado);
        return c;
    }
}
