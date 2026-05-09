<div align="center">

# 🧠 AIBERT — Motor de Planificación Inteligente

### *"Tu tiempo al máximo, tu estrés al mínimo"*

---

### 🛠️ Stack Tecnológico

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)

### ☁️ Infraestructura & Calidad

![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![SonarCloud](https://img.shields.io/badge/SonarCloud-Analysis-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

### 🏗️ Arquitectura

![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=for-the-badge)
![Clean Architecture](https://img.shields.io/badge/Clean-Architecture-blue?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST-API-009688?style=for-the-badge)

</div>

---

## 📑 Tabla de Contenidos

1. [👤 Equipo AIBERT](#1--equipo-aibert)
2. [🎯 Objetivo del Microservicio](#2--objetivo-del-microservicio)
3. [⚡ Funcionalidades Principales](#3--funcionalidades-principales)
4. [⚙️ Tecnologías Utilizadas](#4--tecnologias-utilizadas)
5. [🧩 Endpoints y Flujo](#5--endpoints-y-flujo)
6. [⚠️ Manejo de Errores](#6--manejo-de-errores)
7. [🧪 Pruebas y Cobertura](#7--pruebas-y-cobertura)
8. [🗂️ Organización del Código](#8--organizacion-del-codigo)
9. [🚀 Ejecución del Proyecto](#9--ejecucion-del-proyecto)
10. [☁️ CI/CD](#10--ci-cd)

---

## 1. 👤 Equipo AIBERT

**Módulo 4 — Motor de Planificación Inteligente**  
**Institución:** Escuela Colombiana de Ingeniería Julio Garavito

- Juan Esteban Sánchez García
- Juan Carlos Bohórquez Monroy
- Jeyder Nicolay Leon Lancheros
- Nicolás Guillermo Ibañez León

---

## 2. 🎯 Objetivo del Microservicio

El **Motor de Planificación Inteligente (`engineplaning-service`)** es el núcleo de decisión del sistema AIBERT. No se limita a ser una simple lista de tareas (To-Do list); su verdadero valor radica en su capacidad analítica y algorítmica para optimizar la vida académica del estudiante.

Este microservicio se encarga de:
- **Calcular prioridades** automáticamente basándose en peso académico, proximidad de entrega y tiempo estimado.
- **Distribuir la carga de trabajo** en los bloques de tiempo libre del estudiante.
- **Balancear el esfuerzo** para evitar días sobrecargados (burnout) y aprovechar días vacíos.
- **Re-planificar dinámicamente** cuando el estudiante reporta que no pudo cumplir con un bloque de estudio programado.

---

## 3. ⚡ Funcionalidades Principales

<div align="center">

<table>
  <thead>
    <tr>
      <th>💡 Funcionalidad</th>
      <th>Descripción</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Priorización Matemática</strong></td>
      <td>Aplica una fórmula ponderada (Prox: 40%, Académico: 35%, Tiempo: 25%) para asignar un puntaje (0-100) y nivel (CRITICAL, HIGH, MEDIUM, LOW) a cada tarea pendiente.</td>
    </tr>
    <tr>
      <td><strong>Balanceo de Carga</strong></td>
      <td>Detecta días sobrecargados (>80% de ocupación) y sugiere mover tareas de baja prioridad a días con disponibilidad libre (>80% libre).</td>
    </tr>
    <tr>
      <td><strong>Distribución Semanal</strong></td>
      <td>Asigna automáticamente las tareas a los bloques de tiempo libre del estudiante, dando prioridad estricta a las urgentes e informando si alguna tarea no alcanza a ser programada antes de su entrega.</td>
    </tr>
    <tr>
      <td><strong>Re-balanceo Dinámico</strong></td>
      <td>Si un estudiante falla en un bloque de estudio, el motor re-asigna el tiempo faltante en los días posteriores de la semana de forma inmediata.</td>
    </tr>
  </tbody>
</table>

</div>

---

## 4. ⚙️ Tecnologías Utilizadas

| **Tecnología / Herramienta** | **Uso principal en el proyecto** |
|------------------------------|----------------------------------|
| **Java 21** | Lenguaje de programación base, aprovechando características modernas. |
| **Spring Boot 3.4.3** | Framework principal para la inyección de dependencias y exposición REST. |
| **Spring Cloud OpenFeign** | Cliente HTTP declarativo para comunicarse con `task-service` y `profile-service`. |
| **Resilience4j** | Patrón Circuit Breaker para tolerar fallos en servicios externos. |
| **MapStruct** | Mapeo rápido y seguro entre entidades de dominio y DTOs de Request/Response. |
| **Lombok** | Reducción de código repetitivo (Boilerplate) como Getters, Setters, y Builders. |
| **JUnit 5 / Mockito** | Frameworks de pruebas unitarias para validar la lógica de priorización y balanceo. |
| **JaCoCo** | Generación de reportes de cobertura de código para asegurar la calidad. |
| **Spring Security / JWT** | Validación de tokens para asegurar que el estudiante solo planifique sus propias tareas. |

---

## 5. 🧩 Endpoints y Flujo

### 1️⃣ Priorizar Tareas (`GET /planning/prioritization`)

Obtiene las tareas pendientes del estudiante y les asigna un puntaje de prioridad usando la fórmula configurada en `application.yml`.

**Request Params:**
- `studentId` (String): ID del estudiante.
- `forceRecalculate` (boolean): Forzar recálculo.

**Response:**
```json
{
  "message": "Tasks prioritized successfully",
  "data": [
    {
      "taskId": "task-001",
      "title": "Proyecto Final DOSW",
      "priorityScore": 85.5,
      "priorityLevel": "CRITICAL"
    }
  ]
}
```

### 2️⃣ Sugerencias de Balanceo (`GET /planning/balance`)

Revisa el horario semanal y sugiere mover tareas de días con carga alta a días libres para prevenir el estrés académico.

**Request Params:**
- `studentId` (String): ID del estudiante.

**Response:**
```json
{
  "message": "Workload balance suggestions generated successfully",
  "data": {
    "studentId": "std-123",
    "suggestions": [
      {
        "task": { "title": "Lectura Física" },
        "fromDate": "2026-05-10",
        "toDate": "2026-05-12",
        "reason": "Day 2026-05-10 is overloaded. Day 2026-05-12 has free time."
      }
    ]
  }
}
```

### 3️⃣ Distribución Automática (`POST /planning/distribution`)

Asigna el tiempo necesario para cada tarea dentro de los bloques libres del estudiante en la semana, dando prelación a las críticas.

**Request Params:**
- `studentId` (String): ID del estudiante.

**Response:** Retorna la lista de bloques programados (`assignedBlocks`) y notifica qué tareas no pudieron programarse por falta de tiempo (`unassignedTasks`).

### 4️⃣ Reportar Fallo y Re-balancear (`POST /planning/rebalance/failure`)

El estudiante informa que no pudo completar su bloque de estudio y el motor re-asigna esas horas inmediatamente al resto de la semana.

**Request Body:**
```json
{
  "studentId": "std-123",
  "taskId": "task-001",
  "failedDate": "2026-05-10",
  "hoursMissed": 2.0,
  "reason": "Me quedé dormido"
}
```

---

## 6. ⚠️ Manejo de Errores

Implementamos un manejador global de excepciones (`@RestControllerAdvice`) que estandariza las respuestas ante problemas del dominio o validaciones.

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** |
|:------------------:|:----------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Errores lógicos (`PlanningDomainException`) o campos inválidos (Javax Validation). |
| ![403](https://img.shields.io/badge/403-Forbidden-orange?style=flat) | Token JWT no válido o no coincide con el `studentId`. |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Fallos de sistema o conexión a microservicios externos. |

</div>

Ejemplo de respuesta de error:
```json
{
  "message": "Error en los datos enviados",
  "path": "/planning/distribution",
  "timestamp": "2026-05-08T15:30:00",
  "errors": ["studentId cannot be null"]
}
```

---

## 7. 🧪 Pruebas y Cobertura

La lógica analítica está fuertemente probada usando **JUnit 5** y **Mockito**. Validamos la prioridad matemática y los escenarios de escasez de tiempo en la distribución.

Comando para ejecutar pruebas:
```bash
mvn clean test jacoco:report
```

El reporte se genera en `target/site/jacoco/index.html`. 
La meta de este microservicio es mantener una cobertura superior al **80%**.

---

## 8. 🗂️ Organización del Código

Implementa **Arquitectura Hexagonal (Ports & Adapters)**, asegurando que la lógica algorítmica no dependa de frameworks de persistencia o controladores HTTP.

```text
src/main/java/com/aibert/dosw/
 ├── domain/              # 🟢 Entidades, Lógica Matemática (PriorityScore) y Puertos (In/Out).
 ├── application/         # 🔵 Casos de uso (UseCases) que coordinan los puertos.
 ├── entrypoints/         # 🟠 Adaptadores de entrada: Controladores REST y manejo de excepciones.
 └── infrastructure/      # 🟠 Adaptadores de salida: Feign (task-service), config y JWT.
```

---

## 9. 🚀 Ejecución del Proyecto

### Prerrequisitos
- **Java 21**
- **Maven 3.8+**

### Ejecución Local

1. Clonar el repositorio.
2. Compilar el proyecto.
3. Ejecutar:
```bash
./mvnw spring-boot:run
```

El servicio iniciará en el puerto **8004**.
La documentación Swagger estará disponible en: `http://localhost:8004/swagger-ui.html`

### Configuración (Variables de Entorno)
```properties
JWT_SECRET=tu_secreto_jwt
FEIGN_TASK_SERVICE_URL=http://localhost:8002
FEIGN_PROFILE_SERVICE_URL=http://localhost:8001
```

---

## 10. ☁️ CI/CD

Contamos con un pipeline en **GitHub Actions** (`.github/workflows/CI ingineplaning.yml`) que valida la integridad en cada integración a `develop` y `main`.

**Etapas del Workflow:**
1. **Compile:** Compilación con Maven y Java 21.
2. **Test:** Ejecución automatizada de pruebas y generación del artifact target.
3. **Analyze:** Creación del reporte de cobertura JaCoCo y análisis estático con **SonarCloud**.
