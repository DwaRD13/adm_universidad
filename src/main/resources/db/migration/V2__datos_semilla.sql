-- ====================================================
-- MIGRACION V2: datos de prueba
-- ====================================================
-- Flyway ejecuta esto UNA SOLA VEZ, en el primer arranque contra una base nueva
-- (queda registrado en flyway_schema_history). Arrancar el servidor de nuevo
-- despues no vuelve a insertar ni a borrar nada de aqui.
--
-- Para resetear los datos de demo mas adelante no se puede simplemente volver a
-- arrancar el servidor: Flyway no reejecuta una migracion ya aplicada. Hay que
-- correr este archivo a mano contra la base (los TRUNCATE de abajo lo dejan listo
-- para reinsertar sin duplicados):
--   mysql -h <host> -u <usuario> -p uniconnect < src/main/resources/db/migration/V2__datos_semilla.sql
--
-- CREDENCIALES DE DEMO (todas con hash BCrypt real):
--   Estudiante      ana.martinez@uniconnect.edu.do    / uniconnect123
--   Estudiante      luis.peralta@uniconnect.edu.do    / uniconnect123
--   Estudiante      carla.reyes@uniconnect.edu.do     / uniconnect123
--   Profesor        r.gomez@uniconnect.edu.do         / profesor123
--   Profesor        m.santana@uniconnect.edu.do       / profesor123
--   Profesor        j.encarnacion@uniconnect.edu.do   / profesor123
--   Administrativo  admin@uniconnect.edu.do           / admin123
-- ====================================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE calificaciones_finales;
TRUNCATE TABLE asistencias;
TRUNCATE TABLE entregas_tareas;
TRUNCATE TABLE tareas;
TRUNCATE TABLE materiales;
TRUNCATE TABLE inscripciones;
TRUNCATE TABLE secciones;
TRUNCATE TABLE materias;
TRUNCATE TABLE carreras;
TRUNCATE TABLE mensajes;
TRUNCATE TABLE configuracion_sistema;
TRUNCATE TABLE usuarios;
SET FOREIGN_KEY_CHECKS = 1;

-- Los roles ya vienen sembrados por V1; se asegura su existencia por si se corre a mano.
INSERT INTO roles (id, nombre) VALUES (1, 'Administrativo'), (2, 'Profesor'), (3, 'Estudiante')
    ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- ---------- Configuracion ----------
INSERT INTO configuracion_sistema (clave, valor, descripcion) VALUES
    ('periodo_activo', '2026-C3', 'Periodo académico vigente para inscripciones y horarios.'),
    ('nombre_institucion', 'Universidad UniConnect', 'Nombre que se muestra en la aplicación.'),
    ('nota_minima_aprobacion', '70', 'Nota mínima para aprobar una asignatura.');

-- ---------- Usuarios ----------
-- Contraseñas: estudiantes 'uniconnect123', profesores 'profesor123', admin 'admin123'.
INSERT INTO usuarios (id, rol_id, nombres, apellidos, email, password_hash, matricula_empleado_id, telefono, estado) VALUES
    (1, 1, 'Patricia', 'Núñez',       'admin@uniconnect.edu.do',         '$2a$10$kaUiC9M6aZPNZ3QRXXHFPebAvysOMtKASeEuxdGJ/g6KnPo0Kquj2', 'EMP-0001', '809-555-0100', 'Activo'),
    (2, 2, 'Ricardo', 'Gómez',        'r.gomez@uniconnect.edu.do',       '$2a$10$TXN3SJc.SRsjcZAVxjplh.q79SAU/a7HShxAwvm8ABreInXlmJLGO', 'EMP-0021', '809-555-0121', 'Activo'),
    (3, 2, 'Mariela', 'Santana',      'm.santana@uniconnect.edu.do',     '$2a$10$TXN3SJc.SRsjcZAVxjplh.q79SAU/a7HShxAwvm8ABreInXlmJLGO', 'EMP-0022', '809-555-0122', 'Activo'),
    (4, 2, 'Julio',   'Encarnación',  'j.encarnacion@uniconnect.edu.do', '$2a$10$TXN3SJc.SRsjcZAVxjplh.q79SAU/a7HShxAwvm8ABreInXlmJLGO', 'EMP-0023', '809-555-0123', 'Activo'),
    (5, 3, 'Ana',     'Martínez',     'ana.martinez@uniconnect.edu.do',  '$2a$10$4dpXDT0uAZ1HLc0pZ5Uzk.NdZiZcybD3.B2Z8yp6ceOZhFsvoFlWO', '2022-0451', '809-555-0451', 'Activo'),
    (6, 3, 'Luis',    'Peralta',      'luis.peralta@uniconnect.edu.do',  '$2a$10$4dpXDT0uAZ1HLc0pZ5Uzk.NdZiZcybD3.B2Z8yp6ceOZhFsvoFlWO', '2022-0478', '809-555-0478', 'Activo'),
    (7, 3, 'Carla',   'Reyes',        'carla.reyes@uniconnect.edu.do',   '$2a$10$4dpXDT0uAZ1HLc0pZ5Uzk.NdZiZcybD3.B2Z8yp6ceOZhFsvoFlWO', '2023-0112', '809-555-0112', 'Activo');

-- ---------- Estructura académica ----------
INSERT INTO carreras (id, nombre, codigo, descripcion, duracion_periodos) VALUES
    (1, 'Ingeniería en Sistemas Computacionales', 'ISC', 'Formación en desarrollo de software, redes y bases de datos.', 12),
    (2, 'Administración de Empresas', 'ADM', 'Gestión empresarial, contabilidad y mercadeo.', 12);

INSERT INTO materias (id, carrera_id, nombre, codigo, creditos) VALUES
    (1, 1, 'Programación I',            'SIS-101', 4),
    (2, 1, 'Estructuras de Datos',      'SIS-202', 4),
    (3, 1, 'Bases de Datos',            'SIS-201', 4),
    (4, 1, 'Redes de Computadoras',     'SIS-301', 3),
    (5, 1, 'Ingeniería de Software',    'SIS-302', 4),
    (6, 1, 'Matemática Discreta',       'MAT-201', 4),
    (7, 1, 'Cálculo I',                 'MAT-101', 4),
    (8, 2, 'Contabilidad I',            'ADM-101', 3),
    (9, 2, 'Mercadeo',                  'ADM-201', 3),
    (10, 2, 'Gestión de Recursos Humanos', 'ADM-202', 3);

-- ---------- Secciones ----------
-- Periodo anterior (2026-C2), finalizado: sostiene el historial de calificaciones.
INSERT INTO secciones (id, materia_id, profesor_id, periodo, cupo_maximo, aula, horario_descripcion, estado) VALUES
    (1, 1, 2, '2026-C2', 30, 'Lab-201', 'Lu-Mi 08:00 - 10:00', 'Finalizada'),
    (2, 7, 3, '2026-C2', 35, 'A-104',   'Ma-Ju 10:00 - 12:00', 'Finalizada'),
    (3, 6, 3, '2026-C2', 30, 'A-210',   'Vi 08:00 - 12:00',    'Finalizada'),
    (4, 8, 4, '2026-C2', 40, 'B-101',   'Lu-Mi 14:00 - 16:00', 'Finalizada');

-- Periodo activo (2026-C3): abiertas para inscripción.
INSERT INTO secciones (id, materia_id, profesor_id, periodo, cupo_maximo, aula, horario_descripcion, estado) VALUES
    (5,  2, 2, '2026-C3', 25, 'Lab-202', 'Lu-Mi 08:00 - 10:00', 'Abierta'),
    (6,  3, 2, '2026-C3', 30, 'Lab-203', 'Ma-Ju 10:00 - 12:00', 'Abierta'),
    (7,  5, 3, '2026-C3', 28, 'A-305',   'Lu-Mi 14:00 - 16:00', 'Abierta'),
    (8,  4, 4, '2026-C3', 20, 'Lab-101', 'Ma-Ju 16:00 - 18:00', 'Abierta'),
    (9,  9, 4, '2026-C3', 35, 'B-202',   'Vi 08:00 - 12:00',    'Abierta'),
    (10, 6, 3, '2026-C3', 30, 'A-210',   'Sa 08:00 - 12:00',    'Abierta'),
    (11, 10, 4, '2026-C3', 30, 'B-203',  'Lu-Mi 18:00 - 20:00', 'Abierta'),
    (12, 1, 2, '2026-C3',  2, 'Lab-201', 'Ma-Ju 08:00 - 10:00', 'Abierta');
-- La sección 12 tiene cupo 2 a propósito: permite probar el error de "sin cupo".

-- ---------- Inscripciones ----------
-- Ana (5) cursó cuatro materias en 2026-C2 y está inscrita en cinco de 2026-C3.
INSERT INTO inscripciones (id, estudiante_id, seccion_id, fecha_inscripcion, estado) VALUES
    (1, 5, 1, '2026-01-15 09:12:00', 'Aprobado'),
    (2, 5, 2, '2026-01-15 09:14:00', 'Aprobado'),
    (3, 5, 3, '2026-01-15 09:16:00', 'Aprobado'),
    (4, 5, 4, '2026-01-15 09:18:00', 'Reprobado'),
    (5, 5, 5, '2026-05-20 10:02:00', 'Inscrito'),
    (6, 5, 6, '2026-05-20 10:04:00', 'Inscrito'),
    (7, 5, 7, '2026-05-20 10:06:00', 'Inscrito'),
    (8, 5, 8, '2026-05-20 10:08:00', 'Inscrito'),
    (9, 5, 9, '2026-05-20 10:10:00', 'Inscrito');

-- Luis (6) y Carla (7) ocupan cupo y sirven para probar el aislamiento de datos.
INSERT INTO inscripciones (id, estudiante_id, seccion_id, fecha_inscripcion, estado) VALUES
    (10, 6, 5,  '2026-05-21 08:30:00', 'Inscrito'),
    (11, 6, 6,  '2026-05-21 08:32:00', 'Inscrito'),
    (12, 6, 12, '2026-05-21 08:34:00', 'Inscrito'),
    (13, 7, 12, '2026-05-22 11:00:00', 'Inscrito'),
    (14, 7, 9,  '2026-05-22 11:02:00', 'Inscrito'),
    (15, 7, 11, '2026-05-22 11:04:00', 'Inscrito');
-- Con 12 y 13, la sección 12 queda con su cupo de 2 agotado.

-- ---------- Calificaciones finales (periodo 2026-C2) ----------
INSERT INTO calificaciones_finales (inscripcion_id, nota_numerica, literal, fecha_publicacion) VALUES
    (1, 92.50, 'A', '2026-05-08 16:00:00'),
    (2, 85.00, 'B', '2026-05-08 16:05:00'),
    (3, 78.25, 'C', '2026-05-09 09:30:00'),
    (4, 61.00, 'F', '2026-05-09 09:45:00');

-- ---------- Asistencias del periodo activo ----------
-- Estructuras de Datos (inscripción 5): Lu-Mi.
INSERT INTO asistencias (inscripcion_id, fecha, estado, observaciones) VALUES
    (5, '2026-07-06', 'Presente', NULL),
    (5, '2026-07-08', 'Presente', NULL),
    (5, '2026-07-13', 'Tardanza', 'Llegó 15 minutos tarde.'),
    (5, '2026-07-15', 'Presente', NULL),
    (5, '2026-07-20', 'Presente', NULL),
    (5, '2026-07-22', 'Ausente',  NULL),
    (5, '2026-07-27', 'Presente', NULL),
    (5, '2026-07-29', 'Presente', NULL),
    (5, '2026-08-03', 'Presente', NULL),
    (5, '2026-08-05', 'Presente', NULL);

-- Bases de Datos (inscripción 6): Ma-Ju.
INSERT INTO asistencias (inscripcion_id, fecha, estado, observaciones) VALUES
    (6, '2026-07-07', 'Presente', NULL),
    (6, '2026-07-09', 'Presente', NULL),
    (6, '2026-07-14', 'Presente', NULL),
    (6, '2026-07-16', 'Excusa',   'Cita médica justificada.'),
    (6, '2026-07-21', 'Presente', NULL),
    (6, '2026-07-23', 'Presente', NULL),
    (6, '2026-07-28', 'Presente', NULL),
    (6, '2026-07-30', 'Presente', NULL),
    (6, '2026-08-04', 'Presente', NULL),
    (6, '2026-08-06', 'Presente', NULL);

-- Ingeniería de Software (inscripción 7): Lu-Mi.
INSERT INTO asistencias (inscripcion_id, fecha, estado, observaciones) VALUES
    (7, '2026-07-06', 'Presente', NULL),
    (7, '2026-07-08', 'Ausente',  NULL),
    (7, '2026-07-13', 'Presente', NULL),
    (7, '2026-07-15', 'Presente', NULL),
    (7, '2026-07-20', 'Ausente',  NULL),
    (7, '2026-07-22', 'Presente', NULL),
    (7, '2026-07-27', 'Presente', NULL),
    (7, '2026-07-29', 'Tardanza', NULL),
    (7, '2026-08-03', 'Presente', NULL),
    (7, '2026-08-05', 'Presente', NULL);

-- Redes (inscripción 8): Ma-Ju.
INSERT INTO asistencias (inscripcion_id, fecha, estado, observaciones) VALUES
    (8, '2026-07-07', 'Presente', NULL),
    (8, '2026-07-09', 'Presente', NULL),
    (8, '2026-07-14', 'Presente', NULL),
    (8, '2026-07-16', 'Presente', NULL),
    (8, '2026-07-21', 'Presente', NULL),
    (8, '2026-07-23', 'Presente', NULL),
    (8, '2026-07-28', 'Presente', NULL),
    (8, '2026-07-30', 'Presente', NULL);

-- Mercadeo (inscripción 9): viernes.
INSERT INTO asistencias (inscripcion_id, fecha, estado, observaciones) VALUES
    (9, '2026-07-10', 'Presente', NULL),
    (9, '2026-07-17', 'Presente', NULL),
    (9, '2026-07-24', 'Ausente',  'No justificada.'),
    (9, '2026-07-31', 'Presente', NULL),
    (9, '2026-08-07', 'Presente', NULL);

-- ---------- Tareas ----------
-- Mezcla intencionada: vencidas sin entregar, entregadas, calificadas y próximas.
INSERT INTO tareas (id, seccion_id, titulo, descripcion, fecha_entrega, archivo_adjunto_url, creado_en) VALUES
    (1, 5, 'Implementar una lista enlazada',
        'Implementa una lista simplemente enlazada con las operaciones insertar, eliminar y buscar. Entrega el código fuente en un archivo comprimido.',
        '2026-07-18 23:59:00', NULL, '2026-07-06 10:00:00'),
    (2, 5, 'Análisis de complejidad',
        'Calcula la complejidad temporal y espacial de los algoritmos vistos en clase. Justifica cada respuesta.',
        '2026-08-14 23:59:00', NULL, '2026-08-03 10:00:00'),
    (3, 6, 'Modelo entidad-relación',
        'Diseña el modelo entidad-relación de un sistema de biblioteca. Incluye cardinalidades y llaves foráneas.',
        '2026-07-25 23:59:00', NULL, '2026-07-09 11:00:00'),
    (4, 6, 'Consultas SQL avanzadas',
        'Resuelve los diez ejercicios de JOIN, subconsultas y funciones de agregación del documento adjunto.',
        '2026-08-13 23:59:00', NULL, '2026-08-04 11:00:00'),
    (5, 7, 'Documento de requisitos',
        'Redacta el documento de especificación de requisitos del proyecto de tu equipo, siguiendo la plantilla IEEE 830.',
        '2026-08-21 23:59:00', NULL, '2026-08-05 15:00:00'),
    (6, 7, 'Diagrama de casos de uso',
        'Entrega el diagrama de casos de uso de tu proyecto en formato PDF.',
        '2026-07-24 23:59:00', NULL, '2026-07-13 15:00:00'),
    (7, 8, 'Configuración de subredes',
        'Divide la red 192.168.10.0/24 en seis subredes y documenta el direccionamiento resultante.',
        '2026-08-11 20:00:00', NULL, '2026-08-01 16:30:00'),
    (8, 9, 'Plan de mercadeo digital',
        'Elabora un plan de mercadeo digital para una empresa local. Máximo diez páginas.',
        '2026-08-28 23:59:00', NULL, '2026-08-07 09:00:00');

-- Entregas de Ana: una calificada, una entregada sin calificar, una tardía.
INSERT INTO entregas_tareas (tarea_id, estudiante_id, archivo_url, fecha_envio, calificacion, comentarios_profesor) VALUES
    (1, 5, 'https://drive.google.com/uc?id=demo-lista-enlazada', '2026-07-18 21:40:00', 95.00,
        'Excelente implementación. Cuida los comentarios del código.'),
    (3, 5, 'https://drive.google.com/uc?id=demo-modelo-er',       '2026-07-25 22:15:00', 88.50,
        'Buen modelo. Faltó normalizar la tabla de préstamos.'),
    (6, 5, 'https://drive.google.com/uc?id=demo-casos-de-uso',    '2026-07-26 10:05:00', NULL, NULL);
-- La tarea 7 (Redes) queda pendiente y vence pronto: alimenta "Próximas entregas".

-- ---------- Materiales ----------
INSERT INTO materiales (seccion_id, titulo, descripcion, tipo_archivo, url_archivo, fecha_subida) VALUES
    (5, 'Guía de estructuras lineales',    'Apuntes de listas, pilas y colas con ejemplos en Java.', 'PDF',    'https://ejemplo.uniconnect.edu.do/materiales/estructuras-lineales.pdf', '2026-07-06 09:00:00'),
    (5, 'Video: recorridos de árboles',    'Clase grabada del 20 de julio.',                        'Video',  'https://www.youtube.com/watch?v=demo-arboles',                          '2026-07-20 18:00:00'),
    (6, 'Manual de MySQL 8',               'Referencia oficial resumida para el curso.',            'PDF',    'https://ejemplo.uniconnect.edu.do/materiales/manual-mysql8.pdf',        '2026-07-07 09:30:00'),
    (6, 'Scripts de la clase de JOIN',     'Archivo .sql con todos los ejemplos vistos en clase.',  'Enlace', 'https://github.com/uniconnect-demo/sql-joins',                          '2026-07-30 12:00:00'),
    (7, 'Plantilla IEEE 830',              'Plantilla para el documento de requisitos.',            'PDF',    'https://ejemplo.uniconnect.edu.do/materiales/plantilla-ieee830.pdf',    '2026-08-05 14:00:00'),
    (7, 'Lectura: metodologías ágiles',    'Capítulo 3 del libro de la asignatura.',                'PDF',    'https://ejemplo.uniconnect.edu.do/materiales/agiles-cap3.pdf',          '2026-07-14 14:00:00'),
    (8, 'Topologías de red',               'Presentación de la primera unidad.',                    'PDF',    'https://ejemplo.uniconnect.edu.do/materiales/topologias.pdf',           '2026-07-07 16:00:00'),
    (9, 'Casos de éxito en mercadeo',      'Selección de casos para analizar en clase.',            'Enlace', 'https://ejemplo.uniconnect.edu.do/materiales/casos-mercadeo',           '2026-07-10 08:30:00');

-- ---------- Mensajes ----------
-- Conversación de Ana con el profesor Ricardo Gómez y un mensaje sin leer de Mariela Santana.
INSERT INTO mensajes (remitente_id, destinatario_id, asunto, cuerpo, leido, fecha_envio) VALUES
    (5, 2, 'Duda sobre la lista enlazada',
        'Buenas tardes profesor. Tengo una duda con el método de eliminación cuando el nodo es el primero de la lista. ¿Podría orientarme?',
        1, '2026-07-16 15:20:00'),
    (2, 5, 'Re: Duda sobre la lista enlazada',
        'Hola Ana. Cuando eliminas el primer nodo debes actualizar la cabeza de la lista antes de liberar el nodo. Revisa el ejemplo de la página 12 de la guía.',
        1, '2026-07-16 17:45:00'),
    (5, 2, 'Re: Duda sobre la lista enlazada',
        '¡Muchas gracias profesor! Ya me funcionó.',
        1, '2026-07-16 18:02:00'),
    (2, 5, 'Calificación de la primera tarea',
        'Ana, ya publiqué la calificación de la lista enlazada. Excelente trabajo, sigue así.',
        0, '2026-07-20 09:10:00'),
    (3, 5, 'Recordatorio: documento de requisitos',
        'Recuerden que el documento de requisitos se entrega el 21 de agosto. Los equipos que aún no me han enviado su conformación deben hacerlo esta semana.',
        0, '2026-08-06 11:00:00'),
    (4, 7, 'Bienvenida a Mercadeo',
        'Bienvenidos al curso. La primera evaluación será la última semana de agosto.',
        0, '2026-07-10 07:45:00');
