-- ====================================================
-- MIGRACION V1: esquema inicial de UniConnect
-- ====================================================
-- Flyway ejecuta este script automaticamente al arrancar el servidor, contra la
-- base indicada en DB_NAME. No incluye CREATE DATABASE ni USE: la conexion ya
-- apunta a esa base (ver createDatabaseIfNotExist=true en application.properties).
--
-- Esta es la misma fuente de verdad documentada en README.md. Cualquier cambio
-- de estructura se hace aqui, en una migracion V2 nueva -- nunca editando este
-- archivo una vez que alguien ya lo haya ejecutado (Flyway lo rechazaria por
-- checksum distinto).

-- 1. ROLES Y USUARIOS (Login compartido)
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE -- 'Administrativo', 'Estudiante', 'Profesor'
) ENGINE=InnoDB;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rol_id INT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    matricula_empleado_id VARCHAR(50) UNIQUE, -- Matrícula para estudiantes, ID para empleados
    telefono VARCHAR(20),
    estado ENUM('Activo', 'Inactivo', 'Suspendido') DEFAULT 'Activo',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 2. ESTRUCTURA ACADÉMICA (Admin)
CREATE TABLE carreras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    descripcion TEXT,
    duracion_periodos INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE materias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    carrera_id INT,
    nombre VARCHAR(150) NOT NULL,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    creditos INT NOT NULL,
    FOREIGN KEY (carrera_id) REFERENCES carreras(id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- 3. SECCIONES Y HORARIOS (Admin, Profesor, Estudiante)
CREATE TABLE secciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    materia_id INT NOT NULL,
    profesor_id INT NOT NULL,
    periodo VARCHAR(20) NOT NULL, -- Ej: '2026-C3'
    cupo_maximo INT NOT NULL,
    aula VARCHAR(50),
    horario_descripcion VARCHAR(150), -- Ej: 'Lu-Mi 18:00 - 20:00'
    estado ENUM('Abierta', 'Cerrada', 'En Curso', 'Finalizada') DEFAULT 'Abierta',
    FOREIGN KEY (materia_id) REFERENCES materias(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (profesor_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4. INSCRIPCIONES (Estudiante, Admin)
CREATE TABLE inscripciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id INT NOT NULL,
    seccion_id INT NOT NULL,
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('Inscrito', 'Retirado', 'Aprobado', 'Reprobado') DEFAULT 'Inscrito',
    UNIQUE(estudiante_id, seccion_id), -- Evita que un estudiante se inscriba dos veces en la misma sección
    FOREIGN KEY (estudiante_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (seccion_id) REFERENCES secciones(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. GESTIÓN DE CLASES: ASISTENCIAS, TAREAS Y MATERIALES (Profesor, Estudiante)
CREATE TABLE asistencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id INT NOT NULL,
    fecha DATE NOT NULL,
    estado ENUM('Presente', 'Ausente', 'Tardanza', 'Excusa') NOT NULL,
    observaciones VARCHAR(255),
    FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seccion_id INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_entrega DATETIME NOT NULL,
    archivo_adjunto_url VARCHAR(255),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seccion_id) REFERENCES secciones(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE entregas_tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tarea_id INT NOT NULL,
    estudiante_id INT NOT NULL,
    archivo_url VARCHAR(255) NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    calificacion DECIMAL(5,2),
    comentarios_profesor TEXT,
    FOREIGN KEY (tarea_id) REFERENCES tareas(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (estudiante_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE materiales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seccion_id INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    tipo_archivo VARCHAR(50), -- Ej: PDF, Video, Enlace
    url_archivo VARCHAR(255) NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seccion_id) REFERENCES secciones(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. CALIFICACIONES FINALES (Profesor, Estudiante)
CREATE TABLE calificaciones_finales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id INT NOT NULL UNIQUE,
    nota_numerica DECIMAL(5,2) NOT NULL,
    literal VARCHAR(2), -- Ej: A, B, C, F
    fecha_publicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. COMUNICACIONES Y MENSAJES (Todos los perfiles)
CREATE TABLE mensajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    remitente_id INT NOT NULL,
    destinatario_id INT NOT NULL,
    asunto VARCHAR(150),
    cuerpo TEXT NOT NULL,
    leido TINYINT(1) DEFAULT 0, -- MySQL maneja los booleanos como TINYINT(1)
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (remitente_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. CONFIGURACIÓN DEL SISTEMA (Admin)
CREATE TABLE configuracion_sistema (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor VARCHAR(255) NOT NULL,
    descripcion TEXT
) ENGINE=InnoDB;

-- INSERCIÓN DE ROLES BÁSICOS
INSERT INTO roles (nombre) VALUES ('Administrativo'), ('Profesor'), ('Estudiante');
