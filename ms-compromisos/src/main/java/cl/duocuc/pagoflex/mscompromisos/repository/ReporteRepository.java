package cl.duocuc.pagoflex.mscompromisos.repository;

import cl.duocuc.pagoflex.mscompromisos.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findAllByOrderByFecha();

    List<Reporte> findByCompromisoIdUsuarioOrderByFechaDesc(Long idUsuario);

    List<Reporte> findByCompromisoIdAndEstadoNot(Long idCompromiso, String estado);
}
