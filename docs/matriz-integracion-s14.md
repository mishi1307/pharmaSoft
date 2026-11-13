# Matriz de integración S14

Resultados registrados a partir de `actividad practica.docx` y sus capturas. Las capturas se referencian por su orden dentro del Word.

## Pruebas manuales documentadas

| ID | Comprobación | Resultado | Evidencia / observación |
|---|---|---|---|
| M-01 | Buscar un cliente para una nueva venta. | Pasa | Captura 2: aparece Carlos Gómez al buscarlo. |
| M-02 | Buscar un producto disponible para la venta. | Pasa | Captura 3: Paracetamol 500 mg aparece con precio y stock. |
| M-03 | Verificar que el stock baje después de registrar la venta. | Pasa | Capturas 4 y 6: Paracetamol pasa de 91 a 89 unidades; captura 5 muestra la venta por dos unidades. |
| M-04 | Revisar la venta en el reporte. | Parcial | Capturas 7 y 8: el comprobante identifica el producto y el reporte muestra totales por categoría, pero no el producto individual. |
| M-05 | Verificar la auditoría de la venta. | Parcial | Captura 9 muestra `VENTA_REGISTRADA`, usuario `vendedor`, `requestId` y entidad 20090. El comprobante adjunto es 20093; no se confirma que sea el mismo evento. |
| M-06 | Verificar el identificador cuando ocurre un error. | Pendiente | El Word indica que no se produjo un error, por lo que no hubo `requestId` de error que revisar. |
| M-07 | Revisar health y acceso a métricas. | Pasa | Capturas 10 y 11: `/actuator/metrics` responde 401 y `/actuator/health` muestra `UP`. |
| M-08 | Verificar permisos del almacenero. | Pasa | Captura 13: al abrir Ventas directamente aparece “Acceso denegado”. La captura 12 muestra la sesión del almacenero. |

## Comprobaciones de integración indicadas en la guía

| ID | Origen → destino | Comprobación | Resultado |
|---|---|---|---|
| I-01 | Clientes → Venta | Un cliente recién creado aparece al buscarlo en una venta nueva. | Parcial: se confirmó que Carlos Gómez aparece, pero la evidencia no muestra la creación reciente del cliente. Ver M-01. |
| I-02 | Catálogo → Venta | La búsqueda ofrece productos activos con stock y precio vigente. | Pasa. Ver M-02. |
| I-03 | Venta → Catálogo | Tras vender dos unidades, Productos muestra el stock actualizado. | Pasa: 91 → 89. Ver M-03. |
| I-04 | Venta → Reportes | La venta se refleja en un reporte de productos más vendidos. | No comprobado: el reporte capturado agrupa montos y unidades por categoría, no por producto. Ver M-04. |
| I-05 | Venta → Auditoría | Se registra `VENTA_REGISTRADA` con usuario y `requestId` de la solicitud. | Parcial: la captura muestra esos campos para la venta 20090, no para el comprobante 20093. Ver M-05. |
| I-06 | Catálogo → Auditoría | Un cambio de precio registra valor anterior/nuevo y `modificadoPor`. | Pendiente: no hay resultado de esta comprobación en el Word. |
| I-07 | Seguridad → módulos | Al vencer el token, la SPA vuelve al login y luego recupera la ruta. | Pendiente: no hay resultado de esta comprobación en el Word. |
| I-08 | Paginación → filtros | En Ventas, cambiar fechas desde otra página vuelve a la primera. | Pendiente: no hay resultado de esta comprobación en el Word. |

La matriz anterior registra que las 7 pruebas E2E de la versión previa pasaron en tres corridas (21/21). No se encontró el reporte HTML en los archivos disponibles para verificar ese dato. La suite actual incorpora seis casos nuevos; sus tres corridas deben ejecutarse y guardarse para cerrar S14. Este dato previo no reemplaza las comprobaciones manuales pendientes de esta matriz.
