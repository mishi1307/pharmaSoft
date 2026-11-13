# Registro de incidencias S14

Las fechas indican cuándo quedó documentada cada incidencia en esta entrega; las fechas originales de detección no se conservaron en todos los registros. El estado se actualizará si las nuevas corridas encuentran una regresión.

## INC-01 - Listado de productos sin paginación de servidor

- **Fecha de registro:** 2026-11-12. **Módulo:** Productos. **Severidad:** Alta. **Estado:** Corregida.
- **Pasos:** abrir Productos en una base con más de 100 registros y recorrer el listado. **Esperado:** el backend devuelve páginas y el total correcto. **Obtenido antes del arreglo:** la API cargaba toda la colección y la SPA recortaba localmente los resultados a los primeros 100.
- **Causa raíz:** consulta `findAll()` sin paginación y recorte en el navegador.
- **Corrección:** consulta con `Pageable`, orden permitido y página/tamaño preservados en la URL.
- **Commit de corrección:** backend `f3ccbbd`; frontend `a891c3a`.
- **Prevención:** `e2e/paginacion.spec.ts` verifica tamaño de página, URL, respuesta HTTP y recarga.

## INC-02 - Búsqueda de productos de venta limitada al primer bloque

- **Fecha de registro:** 2026-11-12. **Módulo:** Venta y catálogo. **Severidad:** Alta. **Estado:** Corregida.
- **Pasos:** en Nueva venta, buscar un producto activo cuyo nombre no aparezca entre los primeros 100 productos. **Esperado:** aparece si está activo y tiene stock. **Obtenido antes del arreglo:** la búsqueda se hacía sobre una lista limitada descargada en el navegador.
- **Causa raíz:** la búsqueda del formulario no consultaba el endpoint paginado con el texto ingresado.
- **Corrección:** búsqueda del servidor con parámetros de página y consulta con debounce.
- **Commit de corrección:** backend `f3ccbbd`; frontend `a891c3a`.
- **Prevención:** `e2e/flujo-venta.spec.ts` cubre la búsqueda del producto y el registro de venta; agregar un caso específico con un producto fuera de la primera página cuando los datos de volumen estén disponibles.

## INC-03 - Filtros de ventas y página no sincronizados con la URL

- **Fecha de registro:** 2026-11-12. **Módulo:** Ventas. **Severidad:** Media. **Estado:** Corregida.
- **Pasos:** abrir Ventas, cambiar de página, modificar fechas y recargar. **Esperado:** el filtro devuelve la vista a la primera página y la URL representa el estado consultado. **Obtenido antes del arreglo:** el estado permanecía solo en el componente y podía perderse al recargar.
- **Causa raíz:** el componente no utilizaba de forma consistente los parámetros de búsqueda como fuente del estado.
- **Corrección:** lectura y escritura de filtros, orden, página y tamaño mediante query parameters.
- **Commit de corrección:** backend `71b9680`; frontend `447139a`.
- **Prevención:** ejecutar el caso de filtros y página de ventas tres veces; incorporar cobertura E2E específica al ampliar la suite.

## INC-04 - Parámetros opcionales de orden llegaban como `undefined`

- **Fecha de registro:** 2026-11-12. **Módulo:** Productos. **Severidad:** Media. **Estado:** Corregida.
- **Pasos:** entrar a Productos con parámetros opcionales de orden ausentes y observar la consulta de red. **Esperado:** se usan los valores por defecto permitidos. **Obtenido antes del arreglo:** Angular podía entregar `undefined` y el API respondía 409 por un orden no permitido.
- **Causa raíz:** entradas ligadas a parámetros de ruta sin transformación/normalización efectiva.
- **Corrección:** normalización de página, tamaño, campo de orden y dirección antes de llamar al servicio.
- **Commit de corrección:** frontend `a891c3a`.
- **Prevención:** `e2e/paginacion.spec.ts` cubre la carga inicial y el cambio de página; se debe conservar la consulta inicial sin 409.

## INC-05 - Ventas sintéticas asignadas al mismo cliente

- **Fecha de registro:** 2026-11-12. **Módulo:** Datos de demostración. **Severidad:** Media. **Estado:** Corregida.
- **Pasos:** cargar el conjunto de volumen y revisar varias páginas recientes de Ventas. **Esperado:** la carga sintética se distribuye entre clientes de prueba. **Obtenido antes del arreglo:** todas las filas sintéticas se asociaban a Juan Pérez, lo que confundía las comprobaciones manuales.
- **Causa raíz:** el SQL elegía siempre el cliente con ID mínimo.
- **Corrección:** distribuir las ventas de carga entre los seis clientes; las ventas reales permanecen sin cambios.
- **Commit de corrección:** backend `85fb971`.
- **Prevención:** verificar varias páginas del conjunto generado y distinguir las ventas marcadas como carga de las ventas reales.

## INC-06 - No había pantalla de auditoría en la SPA

- **Fecha de registro:** 2026-11-12. **Módulo:** Auditoría y autorización. **Severidad:** Media. **Estado:** Corregida en los dos repositorios; confirmación E2E pendiente.
- **Pasos:** iniciar sesión como administrador e intentar consultar `/auditoria` desde la SPA. **Esperado:** ruta protegida por `USUARIOS_ADMINISTRAR`, opción visible solo para administración, filtros en URL y lista paginada. **Obtenido antes del arreglo:** el API estaba disponible, pero no había ruta, servicio ni vista frontend.
- **Causa raíz:** se había implementado el endpoint del backend sin conectar una página de administración en Angular.
- **Corrección:** se añadió una vista paginada con filtros, ruta con permiso y opción de menú condicionada.
- **Commit de corrección:** backend `1b00186`; frontend `dafd8e4`.
- **Prevención:** `e2e/estabilizacion.spec.ts` incluye acceso de administrador y denegación al vendedor; ejecutar las tres corridas indicadas por la guía.

## Resumen

| Severidad | Total | Abiertas |
|---|---:|---:|
| Crítica | 0 | 0 |
| Alta | 2 | 0 |
| Media | 4 | 1 |
| Baja | 0 | 0 |

La única pendiente de verificación es INC-06: el cambio ya está publicado en las ramas de trabajo, pero falta comprobar la suite contra Oracle y el backend en marcha. No hay incidencias críticas ni altas abiertas.
