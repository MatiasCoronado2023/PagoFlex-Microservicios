package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TomarReporteDTO {
    private Long idAdmin;
    private String nombreAdmin;
    private String fecha;
}
