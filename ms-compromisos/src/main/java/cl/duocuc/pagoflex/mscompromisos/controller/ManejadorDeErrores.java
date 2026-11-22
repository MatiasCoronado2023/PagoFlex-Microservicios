package cl.duocuc.pagoflex.mscompromisos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejar(ResponseStatusException e) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("status", e.getStatusCode().value());
        cuerpo.put("message", e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(cuerpo);
    }
}
