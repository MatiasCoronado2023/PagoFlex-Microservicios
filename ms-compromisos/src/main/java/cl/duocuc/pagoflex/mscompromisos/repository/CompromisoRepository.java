package cl.duocuc.pagoflex.mscompromisos.repository;

import cl.duocuc.pagoflex.mscompromisos.model.Compromiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompromisoRepository extends JpaRepository<Compromiso, Long> {

    List<Compromiso> findAllByOrderByFechaVencimientoDesc();

    List<Compromiso> findByIdUsuarioOrderByFechaVencimiento(Long idUsuario);
}
