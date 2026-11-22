package cl.duocuc.pagoflex.mscompromisos.repository;

import cl.duocuc.pagoflex.mscompromisos.model.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Long> {

    List<Logro> findByIdUsuarioOrderByFecha(Long idUsuario);

    boolean existsByIdUsuarioAndNombre(Long idUsuario, String nombre);
}
