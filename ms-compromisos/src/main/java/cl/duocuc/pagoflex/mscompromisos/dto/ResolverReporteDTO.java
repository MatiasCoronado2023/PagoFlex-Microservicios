package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolverReporteDTO {
    private Long idAdmin;
    private String respuesta;
    private boolean anular;
    private String motivoAnulacion;
    private String fecha;
}
