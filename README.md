# PagoFlex - Microservicios

Backend de la app móvil PagoFlex (DSY1105 - Desarrollo de Aplicaciones Móviles, Duoc UC).

## Integrantes

- Matias Coronado

## Arquitectura

```
App Android (Retrofit)
        |
   api-gateway (8081)  <-->  eurekaserver (8761)
        |
   +----+-------------------+
   |                        |
ms-usuarios (8082)     ms-compromisos (8083)
   |                        |
db_usuarios (MySQL)    db_compromisos (MySQL)
```

| Proyecto | Puerto | Qué hace |
|---|---|---|
| eurekaserver | 8761 | Registra dónde está cada microservicio |
| apigateway | 8081 | Única entrada para la app; envía cada petición al microservicio que corresponde |
| ms-usuarios | 8082 | Inicio de sesión, registro, personas y cambio de contraseña |
| ms-compromisos | 8083 | Empresas, compromisos, pagos con comprobante, reportes y logros |

## Tecnologías

Java 21 · Spring Boot 4.0.6 · Spring Cloud 2025.1.1 (Eureka y Gateway) · Spring Data JPA · MySQL 8 · Lombok · Swagger (springdoc) · JUnit 5 y Mockito

## Cómo ejecutar

1. Tener MySQL corriendo en el puerto 3306 con usuario `root` y contraseña `rootpass`.
   Las bases `db_usuarios` y `db_compromisos` se crean solas, con sus datos de prueba.
2. Abrir cada carpeta en IntelliJ IDEA y ejecutar, en este orden:
   1. `eurekaserver`
   2. `ms-usuarios` y `ms-compromisos`
   3. `apigateway`
3. Revisar en http://localhost:8761 que aparezcan los tres servicios registrados.

## Documentación Swagger

- ms-usuarios: http://localhost:8082/swagger-ui.html
- ms-compromisos: http://localhost:8083/swagger-ui.html

## Pruebas unitarias

En cada proyecto: clic derecho en `src/test/java` → **Run 'All Tests'**, o `mvnw test`.

## Cuentas de prueba

Contraseña: `pago123`

| Rol | Correo |
|---|---|
| Usuario | crojas@correo.ejemplo |
| Usuario | lmunoz@correo.ejemplo |
| Administrador | alagos@pagoflex.ejemplo |
