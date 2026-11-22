package cl.duocuc.pagoflex.mscompromisos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDTO {
    private Long id;
    private String numero;
    private Long idCompromiso;
    private String folio;
    private String concepto;
    private String empresa;
    private String nombreUsuario;
    private String motivo;
    private String comentario;
    private String fecha;
    private String estado;
    private Long idAdmin;
    private String nombreAdmin;
    private String respuesta;
    private String fechaResolucion;
}
