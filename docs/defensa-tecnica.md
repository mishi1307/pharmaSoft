# Defensa técnica de PharmaSoft

Respuestas de preparación para explicar decisiones del proyecto. Revisar los archivos de código señalados y expresar las respuestas con palabras propias durante la sustentación.

1. **¿Por qué la paginación se hace en el servidor? ¿Qué consultas ejecuta Oracle?**  
   La API pide a Oracle solo el bloque de filas de la página y calcula el total para que la interfaz pueda mostrar cuántas páginas existen. Spring Data aplica el `Pageable`; por ello normalmente ejecuta una consulta de contenido y otra de conteo. Esto evita traer todo el catálogo a la SPA. Código: `src/main/java/pe/edu/upeu/PharmaBackend/service/impl/ProductoServiceImpl.java` y `ProductoRepository.java`.

2. **¿Cómo evitas dejar una venta a medias y que aparezca auditoría si falla?**  
   El servicio registra la venta dentro de una transacción. Publica el evento de auditoría al terminar el guardado, y el listener lo procesa solo en `AFTER_COMMIT`; si la transacción se revierte, ese evento no se escribe. Código: `VentaServiceImpl.java` y `AuditoriaListener.java`.

3. **¿Qué pasa cuando vence el token a mitad de una venta?**  
   El temporizador de la SPA cierra la sesión y conserva la ruta en `returnUrl`. Si una solicitud recibe 401 antes de vencer el temporizador, el interceptor también cierra la sesión. El usuario vuelve al login y puede regresar a la ruta solicitada después de autenticarse. Código: `src/app/core/auth/auth.service.ts`, `error.interceptor.ts` y `login.ts`.

4. **¿Por qué se invalida la caché de productos al registrar una venta?**  
   Una venta reduce el stock. Si se dejara la entrada Redis vigente, el catálogo podría mostrar el stock antiguo hasta que expire el TTL. `@CacheEvict` retira las entradas de productos al registrar o anular una venta. Código: `VentaServiceImpl.java`.

5. **¿Qué diferencia hay entre un log, una columna de auditoría y un evento de bitácora?**  
   El log ayuda a diagnosticar ejecución y errores; `creado_por`/`modificado_por` atribuyen cambios de una fila; el evento de bitácora conserva una acción de negocio con entidad, usuario y `requestId`. Código: `AuditoriaEvento.java`, `AuditoriaListener.java` y las entidades que usan auditoría JPA.

6. **¿Cómo decides si una prueba E2E es estable? ¿Qué haces con una que falla a veces?**  
   Debe comprobar comportamiento visible o respuestas esperadas sin pausas fijas y repetir de forma consistente. La configuración de Playwright deja `retries: 0`; una intermitente se registra, se analiza con la traza y se corrige antes de volver a ejecutarla. Código: `playwright.config.ts` y `e2e/`.

7. **¿Cómo se protege la página y el endpoint de Auditoría?**  
   La ruta Angular exige `USUARIOS_ADMINISTRAR`, el menú filtra las opciones por permiso y Spring Security aplica la misma autorización en `/api/v1/auditoria`. Código: `src/app/app.routes.ts`, `src/app/core/config/menu.ts` y `SecurityConfig.java`.

8. **¿Cómo funcionan los filtros y cómo se conserva la página?**  
   La SPA lee usuario, acción, fechas, página y tamaño desde la URL y los vuelve a escribir al buscar o cambiar de página. El servicio manda esos parámetros al endpoint, que devuelve una respuesta paginada. Código: `src/app/features/auditoria/pages/auditoria-list/auditoria-list.ts` y `AuditoriaServiceImpl.java`.

9. **¿Qué relaciona una operación de la SPA con un log del backend?**  
   `X-Request-Id` identifica la solicitud, el filtro lo coloca en MDC y la respuesta lo devuelve en su cabecera. El evento de auditoría conserva ese valor para buscar las líneas del mismo recorrido. Código: `RequestIdFilter.java` y `EventoAuditoria.java`.

10. **¿Qué cambiarías antes de desplegar a producción?**  
    Usaría HTTPS en el proxy, guardaría credenciales y JWT en un gestor de secretos, restringiría Oracle, Redis y Actuator a redes autorizadas, y probaría copias de seguridad y alertas. La configuración local no debe reutilizar contraseñas de desarrollo. Referencia: `README.md`, sección “Recomendaciones priorizadas para los tres locales”.
