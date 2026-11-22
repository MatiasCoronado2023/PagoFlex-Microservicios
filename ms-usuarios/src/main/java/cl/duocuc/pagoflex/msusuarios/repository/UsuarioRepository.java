package cl.duocuc.pagoflex.msusuarios.repository;

import cl.duocuc.pagoflex.msusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByRut(String rut);

    Optional<Usuario> findByCorreo(String correo);
}
