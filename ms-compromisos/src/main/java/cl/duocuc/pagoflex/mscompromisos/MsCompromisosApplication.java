package cl.duocuc.pagoflex.mscompromisos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsCompromisosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCompromisosApplication.class, args);
        System.out.println("ms-compromisos corriendo en: http://localhost:8083");
        System.out.println("Swagger: http://localhost:8083/swagger-ui.html");
    }
}
