package cl.duocuc.pagoflex.mscompromisos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String numeroComprobante;

    @ManyToOne
    @JoinColumn(name = "compromiso_id", nullable = false)
    private Compromiso compromiso;

    @Column(nullable = false, length = 16)
    private String fechaHora;

    @Column(nullable = false)
    private Integer montoOriginal;

    @Column(nullable = false)
    private Integer recargo;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false, length = 40)
    private String medio;

    @Column(nullable = false, length = 40)
    private String canal;
}
