# Matriz de pruebas de seguridad - Sesión 10

**Actividad:** Autónoma, PharmaBackend  
**Rama:** `feature/seguridad-pruebas-osorio`  
**Estado:** Resultados cotejados con las capturas recibidas. Los casos incompletos o no ejecutados se señalan explícitamente.
**Evidencias:** Las referencias de página apuntan a `Evidencias_Actividad_Autonoma_S10.docx`.


## Preparación

- API local: `http://localhost:8080/api/v1`.
- Crear la colección `PharmaSoft - S10 Seguridad` importando `postman/PharmaSoft-S10-Seguridad.json`.
- Confirmar los IDs reales con `GET /empleados` y `GET /roles` antes de crear o modificar usuarios.
- La guía usa a María Huamán (empleado 6, VENDEDOR rol 2) para U-02. En una base que ya tenga la cuenta `mhuaman`, preparar una base/fixture limpia o acordar otro empleado de prueba antes de ejecutar la parte de creación; no borrar datos productivos.
- Para T-03 y T-04, cambiar temporalmente la configuración local según la guía y restaurarla al terminar. No guardar el secreto alternativo en Git.

## Parte A - Matriz base y casos propios

| ID | Grupo | Usuario / token | Petición | Resultado esperado (código y mensaje) | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| A-01 | Autenticación | admin, vendedor, almacen, supervisor | Login correcto de las cuatro cuentas | 200; cada respuesta incluye roles y permisos; supervisor recibe la unión de roles | 200 OK en los cuatro logins; se observan roles y permisos, incluida la unión de roles de supervisora. | Pasa | p. 2, 3 |
| A-02 | Autenticación | usuario inexistente / contraseña incorrecta | Dos logins inválidos | 401 y el mismo mensaje genérico en ambos casos | Ambos intentos: 401 «Usuario o contraseña incorrectos». | Pasa | p. 4 |
| A-03 | Autenticación | inactivo | Login con contraseña correcta de cuenta inactiva | 401; el mensaje no revela que la cuenta existe | 401 «Usuario o contraseña incorrectos» para la cuenta inactiva. | Pasa | p. 5 |
| A-04 | Autenticación | VENDEDOR | Login escribiendo el username en mayúsculas | 200; búsqueda insensible a mayúsculas, misma identidad y permisos | 200 OK con username VENDEDOR en mayúsculas. | Pasa | p. 5 |
| A-05 | Autenticación (propio) | vendedor | Login con contraseña vacía | 400; validación de campo obligatorio | 400 Bad Request; contraseña obligatoria. | Pasa | p. 6 |
| T-01 | Token | token válido sin prefijo Bearer | GET a ruta protegida; Authorization contiene solo el token | 401 «Debe iniciar sesión para acceder a este recurso» | 401; «Debe iniciar sesión para acceder a este recurso». | Pasa | p. 6 |
| T-02 | Token | token de vendedor alterado | Cambiar un carácter de la firma/payload y llamar una ruta protegida | 401 «El token no es válido» | 401; «El token no es válido». | Pasa | p. 7 |
| T-03 | Token | token emitido con secreto anterior | Cambiar temporalmente `app.jwt.secret`, reiniciar y usar token anterior; restaurar el secreto después | 401 «El token no es válido»; login funciona con el secreto vigente | 401; «El token no es válido». | Pasa | p. 7 |
| T-04 | Token | token vencido | Configurar expiración de 1 minuto, iniciar sesión, esperar 2 minutos y llamar ruta protegida; restaurar configuración | 401 «El token ha expirado. Inicie sesión nuevamente» | 401; «El token ha expirado. Inicie sesión nuevamente». | Pasa | p. 8 |
| T-05 | Token | token de vendedor | Decodificar payload y revisar `sub`, `authorities`, `iat`, `exp | Claims correctos; no hay contraseñas ni otros datos sensibles en el payload | GET /auth/me devuelve 200 y datos del usuario; no se documenta la decodificación de sub, authorities, iat y exp. | Falla (evidencia incompleta) | p. 8 |
| T-06 | Token (propio) | token malformado | Enviar `Authorization: Bearer token-invalido` a ruta protegida | 401 «El token no es válido» | 401; «El token no es válido». | Pasa | p. 9 |
| P-01 | Permisos | vendedor | POST, PUT y DELETE de categorías | 403 en las tres operaciones | POST, PUT y DELETE /categorias: 403 en las tres operaciones. | Pasa | p. 9, 10 |
| P-02 | Permisos | almacen | GET `/clientes` y POST `/ventas | 403 en ambas operaciones | GET /clientes y POST /ventas: 403 en ambas operaciones. | Pasa | p. 11 |
| P-03 | Permisos | vendedor y almacen | GET `/reportes/productos-mas-vendidos` con cada token | 403 para ambos; rechazo de `@PreAuthorize | GET /reportes/productos-mas-vendidos con vendedor y almacenero: 403 en ambas. | Pasa | p. 12 |
| P-04 | Permisos | admin | Una lectura autorizada en catálogo, clientes, ventas, reportes y usuarios | 2xx en cada módulo | Catálogo, clientes, ventas, reportes y usuarios: 200 OK en las cinco consultas. | Pasa | p. 13, 14, 15 |
| P-05 | Permisos (propio) | almacen | DELETE de un producto existente | 403; almacenero no tiene permiso para borrar catálogo | DELETE /productos/62 con almacenero: 403 Forbidden. | Pasa | p. 15 |
| U-01 | Usuarios | admin | Intentar crear usuario con contraseña `abcdefgh` y, por separado, con `rolIds: [] | 400 en ambos; requiere letra+número y al menos un rol | Contraseña débil y roles vacíos: 400 en los dos casos. | Pasa | p. 16 |
| U-02 | Usuarios | admin | Empleado que ya tiene cuenta y luego María Huamán sin cuenta; verificar respuesta y tablas `usuarios`/`usuario_roles | 409 para empleado duplicado; 201 para alta; respuesta sin contraseña, hash BCrypt en BD y nueva relación de rol | Empleado con cuenta: 409. Alta de María: no ejecutada porque ya tenía cuenta; no hay evidencia SQL de un alta nueva. | Parcial | p. 17 |
| U-03 | Usuarios (propio) | admin | Intentar crear cuenta para un empleado inactivo con datos válidos | 409; indica que el empleado está inactivo | No ejecutada: no había empleado inactivo sin usuario asociado. | No ejecutada | No ejecutada; sin captura |
| C-01 | CORS | SPA `localhost:4200 | Observar OPTIONS previo y GET real con token desde la SPA y pestaña Red | Preflight 200; GET 200 o 401; sin error CORS del navegador | OPTIONS 200 y GET desde localhost:4200 con 200; no se observa error CORS. | Pasa | p. 18 |

## Parte B - Cambio de contraseña, roles y estado

Los casos B-01 a B-08 son adicionales a los 20 anteriores. Cuando una fila incluya varios subcasos, guardar una captura por cada respuesta relevante con sufijo (`a`, `b`, etc.).

| ID | Grupo | Usuario / token | Petición | Resultado esperado (código y mensaje) | Resultado obtenido | Estado | Evidencia |
|---|---|---|---|---|---|---|---|
| B-01 | Contraseña | cualquier usuario autenticado | PUT `/auth/password` con passwordActual correcta y passwordNueva válida | 204 No Content; contraseña almacenada con BCrypt; log solo con username | Cambio y restauración de contraseña: 204 en ambos. El log no está evidenciado en las capturas. | Parcial | p. 19 |
| B-02 | Contraseña | usuario autenticado | PUT `/auth/password` con contraseña actual incorrecta | 409 «La contraseña actual no es correcta» | 409; «La contraseña actual no es correcta». | Pasa | p. 20 |
| B-03 | Contraseña | usuario autenticado | PUT `/auth/password` con nueva igual a la actual | 409 «La nueva contraseña debe ser distinta de la actual» | 409; «La nueva contraseña debe ser distinta de la actual». | Pasa | p. 20 |
| B-04 | Contraseña | usuario autenticado | PUT `/auth/password` con nueva contraseña que incumple longitud o letra+número | 400; errores de validación en la respuesta | 400; validación de nueva contraseña. | Pasa | p. 21 |
| B-05 | Roles | admin y usuario de prueba | PUT `/usuarios/{id}/roles` con rolIds válidos; repetir petición con el mismo token del usuario afectado | 200; reemplaza roles/permisos y el permiso nuevo aplica en la siguiente petición | Asignación de roles: 200. Petición con el token existente: 404 de recurso inexistente, no 403. | Pasa | p. 21, 22 |
| B-06 | Roles | admin | Enviar rolIds vacío; luego un usuario inexistente y un rol inexistente | 400 para roles vacío; 404 para usuario o rol inexistente | Roles vacíos: 400; usuario inexistente: 404; rol inexistente: 404. | Pasa | p. 22, 23 |
| B-07 | Estado / roles | admin | ADMIN intenta quitarse ADMIN y desactivarse; incluir usuario inexistente en PATCH estado | 409 en ambos intentos propios; 404 para usuario inexistente | Quitar ADMIN propio: 409; desactivarse: 409; usuario inexistente: 404. | Pasa | p. 24, 25 |
| B-08 | Estado | admin y cuenta de prueba separada | Desactivar otro usuario; usar su token anterior y volver a iniciar sesión | PATCH 200; token existente 401 «El usuario está deshabilitado»; login 401 | Desactivar, reactivar y restaurar roles: 200. Token previo y login: 401. El mensaje del token es genérico («Debe iniciar sesión…»), así que la captura no demuestra por sí sola el rechazo específico por desactivación. | Falla (no concluyente) | p. 25, 26, 27 |


## Criterio de lectura

- `Pasa` indica coincidencia entre resultado esperado y observado; `Parcial` significa que el endpoint respondió según lo esperado, pero falta verificar algún criterio; `Falla` señala una diferencia o evidencia insuficiente; `No ejecutada` indica que no estaban disponibles los datos o precondiciones.
- Las capturas están incorporadas en el documento de evidencias y agrupadas por ID.

## Observaciones de cierre

- T-05 queda incompleto: la captura es de `/auth/me`, no de la decodificación del JWT y sus claims.
- U-02 para María Huamán no se ejecutó porque ya existía una cuenta; no se afirma que se haya creado una fila nueva ni se adjuntó consulta SQL.
- U-03 no se ejecutó porque no se encontró un empleado inactivo sin usuario.
- B-08 devuelve 401 para token previo y login, pero el mensaje del token es genérico y la evidencia no permite atribuir ese 401 con certeza a la desactivación. Repetir con token recién emitido para comprobar el mensaje específico que pide la guía.
- B-01 confirma los dos 204 de cambio/restauración, pero no se revisaron logs ni hash BCrypt; por eso figura como parcial.
