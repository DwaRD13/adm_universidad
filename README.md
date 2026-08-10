# Base de datos UniConnect

Los datos de conexión (host, puerto, base, usuario y contraseña) NO van aquí:
se configuran mediante variables de entorno en el archivo `.env` de este mismo
directorio, que no se versiona. Usa `.env.example` como plantilla. Además de las
credenciales de MySQL, el `.env` debe definir `JWT_SECRETO` (mínimo 32 caracteres);
sin él la aplicación no arranca.

## Migraciones automáticas

El esquema **y los datos de demostración se crean solos** al arrancar el servidor
(`mvnw spring-boot:run`), no requiere ningún paso manual. Lo hace
[Flyway](https://flywaydb.org/), configurado con dos piezas:

- `spring.datasource.url` incluye `?createDatabaseIfNotExist=true`: si `DB_NAME` no
  existe todavía en el servidor, el driver de MySQL la crea en la primera conexión
  (el usuario de `DB_USER` necesita privilegio `CREATE` a nivel de servidor).
- Flyway aplica las migraciones de [`src/main/resources/db/migration/`](src/main/resources/db/migration/)
  contra esa base, en orden, antes de que Hibernate levante el contexto:
  - `V1__esquema_inicial.sql` crea las 13 tablas y siembra los tres roles. Es la
    fuente de verdad del modelo de datos, no este documento.
  - `V2__datos_semilla.sql` carga el juego de datos de demostración (ver abajo).

`spring.jpa.hibernate.ddl-auto=validate` se mantiene como red de seguridad: Hibernate
nunca modifica el esquema, solo confirma que las entidades JPA calzan con lo que
Flyway acaba de aplicar. **Cualquier cambio de estructura futuro va en una migración
nueva** (`V3__algo.sql`, `V4__algo.sql`...), nunca editando `V1` o `V2` una vez
aplicadas — Flyway rechaza una migración ya ejecutada si su contenido cambió (error
de checksum).

### Resetear los datos de demostración

Flyway solo aplica cada migración **una vez** (queda registrada en
`flyway_schema_history`); arrancar el servidor de nuevo no vuelve a ejecutar `V2`. Si
quieres regenerar los datos de demo desde cero más adelante, corre el archivo a mano
—los `TRUNCATE` del principio lo dejan listo para reinsertar sin duplicados—:

```
mysql -h <host> -u <usuario> -p uniconnect < src/main/resources/db/migration/V2__datos_semilla.sql
```

## Datos de prueba

`V2__datos_semilla.sql` carga carreras, materias, secciones del periodo `2026-C3`,
inscripciones, asistencias, tareas en varios estados y una conversación de mensajes.

### Usuarios de demostración

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Estudiante | `ana.martinez@uniconnect.edu.do` | `uniconnect123` |
| Estudiante | `luis.peralta@uniconnect.edu.do` | `uniconnect123` |
| Estudiante | `carla.reyes@uniconnect.edu.do` | `uniconnect123` |
| Profesor | `r.gomez@uniconnect.edu.do` | `profesor123` |
| Administrativo | `admin@uniconnect.edu.do` | `admin123` |

Ana Martínez es la cuenta principal de la demo: tiene cinco materias en curso,
historial de calificaciones, asistencias, tareas en varios estados y conversaciones.
Luis y Carla existen para comprobar que un estudiante nunca ve datos de otro.

## Resumen del modelo de datos

El detalle columna por columna vive en
[`src/main/resources/db/migration/V1__esquema_inicial.sql`](src/main/resources/db/migration/V1__esquema_inicial.sql).
Relaciones entre las 13 tablas:

- `roles` → `usuarios` (una sola tabla de usuarios para los tres perfiles; el rol discrimina).
- `carreras` → `materias` → `secciones` (una sección es materia + profesor + periodo, ej. `2026-C3`).
- `inscripciones` es la tabla pivote estudiante↔sección: `asistencias` y `calificaciones_finales` cuelgan de ahí, no de `usuarios`.
- `tareas`, `materiales` cuelgan de `secciones`; `entregas_tareas` de `tarea` + `estudiante`.
- `mensajes` (remitente/destinatario → `usuarios`) y `configuracion_sistema` son transversales.
