# Resultados de pruebas S14

## Estado del código y pruebas locales

| Verificación | Fecha | Resultado | Observación |
|---|---|---|---|
| Compilación del backend (`mvnw.cmd -DskipTests package`) | 2026-11-12 | Correcta | El artefacto se generó. Esta orden compila, pero no ejecuta las pruebas. |
| Pruebas del backend (`mvnw.cmd test`) | 2026-11-12 | Parcial: 14 pasan, 1 error | El test `PharmaBackendApplicationTests.contextLoads` no pudo iniciar el contexto porque Oracle no estaba accesible (ORA-17820). |
| Compilación de producción del frontend (`npm run build`) | 2026-11-12 | Correcta | Bundle inicial: 320.28 kB sin comprimir; 90.65 kB estimados de transferencia. |
| Pruebas unitarias del frontend (`npm test -- --watch=false`) | 2026-11-12 | 11 pasan | 9 archivos de prueba. |
| Descubrimiento E2E (`npm run e2e -- --list`) | 2026-11-12 | 13 encontradas | Esto enumera los casos, no demuestra que se hayan ejecutado. |
| Suite E2E repetida tres veces | Pendiente | No ejecutada | Docker Desktop no pudo iniciar; Oracle, Redis, backend y frontend no estaban disponibles en sus puertos de prueba. |
| Colección Postman S14 | Pendiente | No ejecutada | Requiere API activa y credenciales locales cargadas en las variables de la colección. |

El commit que se debe declarar en el informe de la suite es el hash de esta entrega cuando se publique. No usar el HTML de una corrida anterior como resultado S14.

## Tabla de tres corridas E2E

Ejecutar con `npx playwright test --repeat-each=3 --reporter=list,html` una vez que Oracle, Redis y el backend estén disponibles. Guardar el enlace o el reporte HTML en `docs/evidencias/s14-estabilizacion/`.

| Corrida | Fecha | Commit | Pruebas ejecutadas | Aprobadas | Fallidas | Duración | Informe HTML |
|---|---|---|---:|---:|---:|---|---|
| 1 | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| 2 | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |
| 3 | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente | Pendiente |

## Casos Postman previstos

La colección `docs/postman/PharmaBackend.postman_collection.json` espera verificar 200, 201, 400, 401, 403, 404 y 409 en consultas de productos, creación de cliente, validación de ventas y autorización de auditoría. Configurar en Postman las variables `adminUser`, `adminPassword`, `vendedorUser` y `vendedorPassword`; no escribir contraseñas en la documentación ni subir valores reales.

## Verificación manual

La guía también requiere tres recorridos manuales del flujo principal, el cotejo de una venta entre comprobante, autoría, auditoría y log, dos ensayos cronometrados y un video de respaldo de hasta 10 minutos. Registrar fecha, duración y desvíos en `checklist-demo.md`, completar los ensayos en `guion-demostracion.md` y agregar las evidencias a `docs/evidencias/s14-estabilizacion/` después de realizarlos.
