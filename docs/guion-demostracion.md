# Guion de demostración de PharmaSoft

Duración objetivo: 10 minutos. Usar datos de demostración recién restablecidos y una venta que pueda seguirse hasta la bitácora. Ensayar dos veces con cronómetro y anotar la duración real en la checklist.

| Tiempo | Bloque | Qué mostrar y decir |
|---|---|---|
| 0:00–1:00 | Contexto | Presentar el problema de la botica, los perfiles de usuario y el diagrama de arquitectura. Explicar que la SPA consume una API Spring Boot conectada a Oracle y Redis. |
| 1:00–4:30 | Flujo principal | Iniciar sesión como vendedor; buscar un cliente y un producto; registrar la venta; abrir el comprobante y mostrar que el stock disminuyó. |
| 4:30–6:00 | Seguridad | Mostrar el menú según el perfil, intentar abrir Auditoría como vendedor y comprobar el acceso denegado. Mostrar un 401 por token vencido y un 403 en la API con un perfil sin permiso. |
| 6:00–7:30 | Volumen y rendimiento | Abrir Productos con los datos de volumen; mostrar página y filtros en la URL, respuesta paginada, comportamiento de Redis y health/métricas de Actuator según permisos. |
| 7:30–8:30 | Auditoría | Abrir la fila de la venta de demostración y relacionar usuario, evento, ID de venta y `requestId` con el log del backend. |
| 8:30–10:00 | Calidad | Ejecutar la suite E2E, resumir sus tres corridas y explicar las incidencias corregidas y una prueba que evita cada regresión relevante. |

## Ensayos

| Ensayo | Fecha | Duración | Desvíos y ajustes |
|---|---|---|---|
| 1 | Pendiente | Pendiente | Completar después del ensayo cronometrado. |
| 2 | Pendiente | Pendiente | Completar después del segundo ensayo cronometrado. |

No presentar como ejecutado un bloque que no se haya recorrido en el entorno real. Si falla un servicio, usar el video o las capturas de respaldo y explicar la incidencia.
