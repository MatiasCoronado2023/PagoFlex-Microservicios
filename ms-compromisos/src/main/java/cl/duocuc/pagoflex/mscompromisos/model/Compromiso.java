package cl.duocuc.pagoflex.mscompromisos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compromisos")
public class Compromiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String folio;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nombreUsuario;

    @Column(nullable = false, length = 12)
    private String rutUsuario;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 150)
    private String concepto;

    private Integer numeroCuota;

    private Integer totalCuotas;

    @Column(nullable = false)
    private Integer monto;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 250)
    private String motivoAnulacion;

    private Long idAdminRegistro;

    @Column(length = 16)
    private String fechaRegistro;

    private Long idAdminAnulacion;

    @Column(length = 16)
    private String fechaAnulacion;
}
