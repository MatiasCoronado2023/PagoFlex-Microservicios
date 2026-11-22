package cl.duocuc.pagoflex.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {
    private String nombre;
    private String rut;
    private String correo;
    private String telefono;
    private String clave;
}
