package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompromisoDTO {
    private Long id;
    private String folio;
    private Long idUsuario;
    private String nombreUsuario;
    private String rutUsuario;
    private Long idEmpresa;
    private String empresa;
    private Integer recargoEmpresa;
    private String concepto;
    private Integer numeroCuota;
    private Integer totalCuotas;
    private Integer monto;
    private String vencimiento;
    private String estado;
    private String motivoAnulacion;
}
