package cl.duocuc.pagoflex.mscompromisos.service;

import cl.duocuc.pagoflex.mscompromisos.model.Logro;
import cl.duocuc.pagoflex.mscompromisos.repository.LogroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogroService {

    private final LogroRepository logroRepository;

    public List<Logro> obtenerDeUsuario(Long idUsuario) {
        return logroRepository.findByIdUsuarioOrderByFecha(idUsuario);
    }

    public boolean otorgar(Logro logro) {
        if (logroRepository.existsByIdUsuarioAndNombre(logro.getIdUsuario(), logro.getNombre())) {
            return false;
        }
        logro.setId(null);
        logroRepository.save(logro);
        return true;
    }
}
