-- Solo ejecutar en un esquema local de desarrollo o pruebas, tras V4.
-- Añade 5 000 productos y 20 000 ventas sin detalle (estas ventas solo sirven
-- para medir paginación; no deben usarse para validar reportes).
insert into productos (nombre, precio, stock, estado, id_categoria, fecha_creacion, creado_por)
select 'Producto de prueba ' || lpad(level, 5, '0'),
       round(dbms_random.value(1, 200), 2),
       trunc(dbms_random.value(0, 500)),
       1,
       (select min(id) from categorias),
       systimestamp,
       'carga'
from dual connect by level <= 5000;

insert into ventas (fecha, total, estado, cliente_id, creado_por)
select systimestamp - numtodsinterval(dbms_random.value(0, 365), 'DAY'),
       round(dbms_random.value(5, 500), 2),
       'REGISTRADA',
       c.id,
       'carga'
from (select level n from dual connect by level <= 20000) secuencia
join (
    select id, row_number() over (order by id) rn, count(*) over () total
    from clientes
) c on c.rn = mod(secuencia.n - 1, c.total) + 1;
commit;
