# Auditoría de PharmaSoft

La bitácora permite explicar quién ejecutó una operación importante, sobre qué registro y bajo qué solicitud. Se guarda después de confirmar la transacción de negocio para que no quede un evento de éxito si el cambio principal se revierte.

## Eventos registrados

| Acción | Cuándo se registra | Entidad e identificador | Detalle guardado |
|---|---|---|---|
| `LOGIN_EXITOSO` | Después de validar credenciales | Usuario; sin ID numérico | Usuario autenticado; no se guarda el token. |
| `VENTA_REGISTRADA` | Después del commit de una venta | Venta y su ID | Total y cantidad de líneas; sin datos de pago. |
| `VENTA_ANULADA` | Después de anular una venta y restituir stock | Venta y su ID | Confirmación de anulación y restitución de stock. |
| `PRECIO_MODIFICADO` | Después de guardar un cambio de precio | Producto y su ID | Precio anterior y precio nuevo. |
| `PRODUCTO_ELIMINADO` | Después de eliminar un producto | Producto y su ID | Nombre del producto. |
| `CLIENTE_REGISTRADO` | Después de guardar un cliente nuevo | Cliente y su ID | Confirmación del alta; no se incluyen DNI, correo, teléfono ni dirección. |

La guía solicita eventos de negocio autenticados. Por eso un intento fallido de inicio de sesión no crea una fila de auditoría: todavía no se ha verificado la identidad que podría atribuirse a la operación. El backend devuelve el error de autenticación y no conserva la contraseña ni el token enviado.

## Datos de cada fila

La tabla `auditoria_eventos` guarda fecha, usuario, acción, entidad, identificador de entidad, detalle y `request_id`. El endpoint `GET /api/v1/auditoria` filtra por usuario, acción y fechas, y pagina los resultados. Solo usuarios con `USUARIOS_ADMINISTRAR` pueden consultarlo.

La auditoría responde qué operación de negocio se completó. Las columnas de autoría de las entidades, como `creado_por` y `modificado_por`, indican quién creó o actualizó una fila. Los logs técnicos describen el flujo de ejecución y sus errores. Son registros distintos y no deben duplicar información sensible.

## Momento de registro

Los eventos de ventas, clientes y productos se publican durante la transacción y el listener los persiste en `AFTER_COMMIT`. Así se evita registrar una operación que luego haya hecho rollback. El listener usa una transacción nueva para guardar la bitácora sin reabrir la transacción de negocio.

El login exitoso se guarda directamente después de autenticar al usuario, con un evento reducido que solo incluye el nombre de usuario. Los errores de autenticación no se atribuyen como acciones de un usuario autenticado.

## Datos que nunca se guardan

- Contraseñas, ni en texto ni cifradas dentro del detalle.
- Tokens JWT, encabezados `Authorization` ni cookies de sesión.
- DNI completo, teléfono, correo o dirección del cliente.
- Contenido completo de solicitudes o respuestas HTTP.
- Datos de tarjeta o pago.

## Trazabilidad de una venta

`X-Request-Id` identifica la solicitud HTTP. El filtro de backend lo coloca en MDC y lo devuelve en el encabezado de respuesta. Al registrar una venta, el evento conserva ese mismo identificador. La venta queda vinculada además por su ID y por `creado_por`.

Para completar la evidencia de defensa, se debe elegir una venta reciente y cotejar el mismo ID entre `ventas.creado_por`, `auditoria_eventos.request_id` y las líneas del log. La matriz de integración actual advierte que las capturas disponibles muestran IDs de comprobante distintos; esa comprobación de punta a punta sigue pendiente hasta volver a capturar una sola venta identificable.

## Código relacionado

- `src/main/java/pe/edu/upeu/PharmaBackend/auditoria/AuditoriaListener.java`
- `src/main/java/pe/edu/upeu/PharmaBackend/service/impl/AuditoriaServiceImpl.java`
- `src/main/java/pe/edu/upeu/PharmaBackend/controller/AuditoriaController.java`
- `src/main/java/pe/edu/upeu/PharmaBackend/dto/EventoAuditoria.java`
- `src/main/resources/db/migration/V4__auditoria.sql`
