package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private String numeroComprobante;
    private Long idCompromiso;
    private String fechaHora;
    private Integer montoOriginal;
    private Integer recargo;
    private Integer total;
    private String medio;
    private String canal;
    private String folio;
    private String concepto;
    private Integer numeroCuota;
    private Integer totalCuotas;
    private String vencimiento;
    private String empresa;
    private String pagador;
    private String rutPagador;
}
