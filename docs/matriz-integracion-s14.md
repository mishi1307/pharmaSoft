# Matriz de integración S14

Marcar el resultado después de recorrer PharmaSoft con un usuario vendedor y otro administrador. No marcar como cumplido solo por tener el código implementado.

| ID | Origen → destino | Comprobación | Resultado |
|---|---|---|---|
| I-01 | Clientes → Venta | Un cliente recién creado aparece al buscarlo en una venta nueva. | Pendiente de verificación manual |
| I-02 | Catálogo → Venta | La búsqueda ofrece productos activos con stock y precio vigente. | Pendiente de verificación manual |
| I-03 | Venta → Catálogo | Tras vender dos unidades, Productos muestra el stock actualizado sin recargar el navegador. | Pendiente de verificación manual |
| I-04 | Venta → Reportes | La venta se refleja en el reporte de productos más vendidos. | Pendiente de verificación manual |
| I-05 | Venta → Auditoría | Se registra `VENTA_REGISTRADA` con el usuario y el `requestId` de la solicitud. | Pendiente de verificación manual |
| I-06 | Catálogo → Auditoría | Un cambio de precio registra el valor anterior y el nuevo; `modificadoPor` identifica al usuario. | Pendiente de verificación manual |
| I-07 | Seguridad → módulos | Si vence el token durante la operación, la SPA lleva al login y luego permite volver a la ruta pedida. | Pendiente de verificación manual |
| I-08 | Paginación → filtros | En Ventas, cambiar fechas desde una página posterior vuelve a la primera página. | Pendiente de verificación manual |
