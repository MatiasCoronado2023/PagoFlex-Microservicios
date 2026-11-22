package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuevoReporteDTO {
    private Long idCompromiso;
    private String motivo;
    private String comentario;
    private String fecha;
}
