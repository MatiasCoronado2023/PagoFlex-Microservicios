package cl.duocuc.pagoflex.msusuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosApplication.class, args);
        System.out.println("ms-usuarios corriendo en: http://localhost:8082");
        System.out.println("Swagger: http://localhost:8082/swagger-ui.html");
    }
}
