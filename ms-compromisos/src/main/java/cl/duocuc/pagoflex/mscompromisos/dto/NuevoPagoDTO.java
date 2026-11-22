package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuevoPagoDTO {
    private Long idCompromiso;
    private String medio;
    private Integer recargo;
    private String fecha;
}
