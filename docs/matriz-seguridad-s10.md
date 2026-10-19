# Matriz de pruebas de seguridad - Sesión 10

**Actividad:** Autónoma, PharmaBackend  
**Rama:** `feature/seguridad-pruebas-osorio`  
**Estado:** Plantilla de trabajo. Completar resultados y evidencias durante las pruebas manuales con la estudiante.

No usar tokens reales de producción. Las celdas de resultado obtenido, estado y evidencia se completarán al ejecutar cada caso en Postman/Swagger y, para C-01, en la SPA y las herramientas del navegador.

## Preparación

- API local: `http://localhost:8080/api/v1`.
- Crear la colección `PharmaSoft - S10 Seguridad` importando `postman/PharmaSoft-S10-Seguridad.json`.
- Confirmar los IDs reales con `GET /empleados` y `GET /roles` antes de crear o modificar usuarios.
- La guía usa a María Huamán (empleado 6, VENDEDOR rol 2) para U-02. En una base que ya tenga la cuenta `mhuaman`, preparar una base/fixture limpia o acordar otro empleado de prueba antes de ejecutar la parte de creación; no borrar datos productivos.
- Para T-03 y T-04, cambiar temporalmente la configuración local según la guía y restaurarla al terminar. No guardar el secreto alternativo en Git.

## Parte A - Matriz base y casos propios

| ID | Grupo | Usuario / token | Petición y precondición | Resultado esperado | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| A-01 | Autenticación | admin, vendedor, almacen, supervisor | Login correcto de las cuatro cuentas | 200; cada respuesta incluye roles y permisos; supervisor recibe la unión de roles | Pendiente | Pendiente | `A-01_admin.png`, `A-01_vendedor.png`, `A-01_almacen.png`, `A-01_supervisor.png` |
| A-02 | Autenticación | usuario inexistente / contraseña incorrecta | Dos logins inválidos | 401 y el mismo mensaje genérico en ambos casos | Pendiente | Pendiente | `A-02_inexistente.png`, `A-02_password.png` |
| A-03 | Autenticación | inactivo | Login con contraseña correcta de cuenta inactiva | 401; el mensaje no revela que la cuenta existe | Pendiente | Pendiente | `A-03_postman.png` |
| A-04 | Autenticación | VENDEDOR | Login escribiendo el username en mayúsculas | 200; búsqueda insensible a mayúsculas, misma identidad y permisos | Pendiente | Pendiente | `A-04_postman.png` |
| A-05 | Autenticación (propio) | vendedor | Login con contraseña vacía | 400; validación de campo obligatorio | Pendiente | Pendiente | `A-05_postman.png` |
| T-01 | Token | token válido sin prefijo Bearer | GET a ruta protegida; Authorization contiene solo el token | 401 «Debe iniciar sesión para acceder a este recurso» | Pendiente | Pendiente | `T-01_postman.png` |
| T-02 | Token | token de vendedor alterado | Cambiar un carácter de la firma/payload y llamar una ruta protegida | 401 «El token no es válido» | Pendiente | Pendiente | `T-02_postman.png` |
| T-03 | Token | token emitido con secreto anterior | Cambiar temporalmente `app.jwt.secret`, reiniciar y usar token anterior; restaurar el secreto después | 401 «El token no es válido»; login funciona con el secreto vigente | Pendiente | Pendiente | `T-03_postman.png` |
| T-04 | Token | token vencido | Configurar expiración de 1 minuto, iniciar sesión, esperar 2 minutos y llamar ruta protegida; restaurar configuración | 401 «El token ha expirado. Inicie sesión nuevamente» | Pendiente | Pendiente | `T-04_postman.png` |
| T-05 | Token | token de vendedor | Decodificar payload y revisar `sub`, `authorities`, `iat`, `exp` | Claims correctos; no hay contraseñas ni otros datos sensibles en el payload | Pendiente | Pendiente | `T-05_postman.png` |
| T-06 | Token (propio) | token malformado | Enviar `Authorization: Bearer token-invalido` a ruta protegida | 401 «El token no es válido» | Pendiente | Pendiente | `T-06_postman.png` |
| P-01 | Permisos | vendedor | POST, PUT y DELETE de categorías | 403 en las tres operaciones | Pendiente | Pendiente | `P-01_post.png`, `P-01_put.png`, `P-01_delete.png` |
| P-02 | Permisos | almacen | GET `/clientes` y POST `/ventas` | 403 en ambas operaciones | Pendiente | Pendiente | `P-02_clientes.png`, `P-02_ventas.png` |
| P-03 | Permisos | vendedor y almacen | GET `/reportes/productos-mas-vendidos` con cada token | 403 para ambos; rechazo de `@PreAuthorize` | Pendiente | Pendiente | `P-03_vendedor.png`, `P-03_almacen.png` |
| P-04 | Permisos | admin | Una lectura autorizada en catálogo, clientes, ventas, reportes y usuarios | 2xx en cada módulo | Pendiente | Pendiente | `P-04_catalogo.png`, `P-04_clientes.png`, `P-04_ventas.png`, `P-04_reportes.png`, `P-04_usuarios.png` |
| P-05 | Permisos (propio) | almacen | DELETE de un producto existente | 403; almacenero no tiene permiso para borrar catálogo | Pendiente | Pendiente | `P-05_postman.png` |
| U-01 | Usuarios | admin | Intentar crear usuario con contraseña `abcdefgh` y, por separado, con `rolIds: []` | 400 en ambos; requiere letra+número y al menos un rol | Pendiente | Pendiente | `U-01_password.png`, `U-01_roles.png` |
| U-02 | Usuarios | admin | Empleado que ya tiene cuenta y luego María Huamán sin cuenta; verificar respuesta y tablas `usuarios`/`usuario_roles` | 409 para empleado duplicado; 201 para alta; respuesta sin contraseña, hash BCrypt en BD y nueva relación de rol | Pendiente | Pendiente | `U-02_duplicado.png`, `U-02_maria.png`, captura SQL `U-02_sql.png` |
| U-03 | Usuarios (propio) | admin | Intentar crear cuenta para un empleado inactivo con datos válidos | 409; indica que el empleado está inactivo | Pendiente | Pendiente | `U-03_postman.png` |
| C-01 | CORS | SPA `localhost:4200` | Observar OPTIONS previo y GET real con token desde la SPA y pestaña Red | Preflight 200; GET 200 o 401; sin error CORS del navegador | Pendiente | Pendiente | `C-01_network.png` |

## Parte B - Cambio de contraseña, roles y estado

Los casos B-01 a B-08 son adicionales a los 20 anteriores. Cuando una fila incluya varios subcasos, guardar una captura por cada respuesta relevante con sufijo (`a`, `b`, etc.).

| ID | Grupo | Usuario / token | Petición y precondición | Resultado esperado | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| B-01 | Contraseña | cualquier usuario autenticado | PUT `/auth/password` con passwordActual correcta y passwordNueva válida | 204 No Content; contraseña almacenada con BCrypt; log solo con username | Pendiente | Pendiente | `B-01_postman.png` |
| B-02 | Contraseña | usuario autenticado | PUT `/auth/password` con contraseña actual incorrecta | 409 «La contraseña actual no es correcta» | Pendiente | Pendiente | `B-02_postman.png` |
| B-03 | Contraseña | usuario autenticado | PUT `/auth/password` con nueva igual a la actual | 409 «La nueva contraseña debe ser distinta de la actual» | Pendiente | Pendiente | `B-03_postman.png` |
| B-04 | Contraseña | usuario autenticado | PUT `/auth/password` con nueva contraseña que incumple longitud o letra+número | 400; errores de validación en la respuesta | Pendiente | Pendiente | `B-04_postman.png` |
| B-05 | Roles | admin y usuario de prueba | PUT `/usuarios/{id}/roles` con rolIds válidos; repetir petición con el mismo token del usuario afectado | 200; reemplaza roles/permisos y el permiso nuevo aplica en la siguiente petición | Pendiente | Pendiente | `B-05_roles.png`, `B-05_permiso.png` |
| B-06 | Roles | admin | Enviar rolIds vacío; luego un usuario inexistente y un rol inexistente | 400 para roles vacío; 404 para usuario o rol inexistente | Pendiente | Pendiente | `B-06_vacio.png`, `B-06_usuario.png`, `B-06_rol.png` |
| B-07 | Estado / roles | admin | ADMIN intenta quitarse ADMIN y desactivarse; incluir usuario inexistente en PATCH estado | 409 en ambos intentos propios; 404 para usuario inexistente | Pendiente | Pendiente | `B-07_autoroles.png`, `B-07_autoestado.png`, `B-07_inexistente.png` |
| B-08 | Estado | admin y cuenta de prueba separada | Desactivar otro usuario; usar su token anterior y volver a iniciar sesión | PATCH 200; token existente 401 «El usuario está deshabilitado»; login 401 | Pendiente | Pendiente | `B-08_estado.png`, `B-08_token.png`, `B-08_login.png` |

## Reglas para cerrar la matriz

- Reemplazar «Pendiente» únicamente después de ejecutar y observar cada caso.
- Registrar el código HTTP y el mensaje realmente recibidos; marcar `Pasa` o `Falla` comparándolos con lo esperado.
- Guardar cada evidencia en `docs/evidencias-s10/` con el nombre indicado en su fila.
- No incluir tokens utilizables en capturas compartidas ni en variables exportadas de Postman.
