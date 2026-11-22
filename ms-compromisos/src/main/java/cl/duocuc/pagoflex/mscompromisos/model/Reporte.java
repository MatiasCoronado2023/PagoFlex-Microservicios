package cl.duocuc.pagoflex.mscompromisos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "compromiso_id", nullable = false)
    private Compromiso compromiso;

    @Column(nullable = false, length = 60)
    private String motivo;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false, length = 16)
    private String fecha;

    @Column(nullable = false, length = 20)
    private String estado;

    private Long idAdmin;

    @Column(length = 100)
    private String nombreAdmin;

    @Column(length = 16)
    private String fechaToma;

    @Column(length = 500)
    private String respuesta;

    @Column(length = 16)
    private String fechaResolucion;
}
