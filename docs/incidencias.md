# Incidencias de integración y estabilización

| ID | Módulo | Descripción y pasos | Severidad | Causa | Corrección | Estado |
|---|---|---|---|---|---|---|
| INC-01 | Productos | El listado cargaba todos los productos y el frontend solo mostraba los primeros 100. | Alta | El endpoint usaba `findAll()` y la SPA recortaba localmente el resultado. | Se agregó paginación en servidor, orden permitido y estado de página en la URL. | Corregida; pendiente validar con datos de volumen. |
| INC-02 | Venta / catálogo | Al buscar productos para una venta, los que quedaban fuera de los primeros 100 no aparecían. | Alta | El formulario descargaba un bloque fijo y hacía la búsqueda solo en el navegador. | Se agregó búsqueda paginada al API y consulta con espera de 300 ms desde el formulario. | Corregida; pendiente validar con datos locales. |
| INC-03 | Ventas | La página y el tamaño del listado no se conservaban al recargar; los filtros no reiniciaban la URL en la primera página. | Media | El estado de paginación se guardaba solo en señales del componente. | Los filtros, orden, página y tamaño ahora se leen y escriben en los parámetros de la URL. | Corregida; pendiente validar con Playwright. |

Los cambios están implementados en la rama de trabajo actual. El estado final de cada incidencia se completa después de validar los casos con la base de datos de desarrollo.
