# README Corregido y Mejorado


<div align="center">

# 🧠 superOscholar-engineplanning-service

### *Motor de Planificación Inteligente — A.IBERT ECI Planner*

> Analiza, prioriza y distribuye automáticamente las tareas académicas del estudiante,
> optimizando su tiempo disponible para mejorar el rendimiento y evitar el estrés.

---

### 🛠️ Stack Tecnológico

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

### ☁️ Infraestructura & Calidad

![Azure](https://img.shields.io/badge/Azure-Cloud-0078D4?style=for-the-badge&logo=microsoft-azure&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-D1322B?style=for-the-badge)
![SonarQube](https://img.shields.io/badge/SonarQube-Quality-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)

### 🏗️ Arquitectura

![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=for-the-badge)
![Clean Architecture](https://img.shields.io/badge/Clean-Architecture-blue?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST-API-009688?style=for-the-badge)
![Microservices](https://img.shields.io/badge/Microservices-Pattern-FF6B35?style=for-the-badge)

</div>

---

## 📑 Tabla de Contenidos

1. [👤 Integrantes](#1--integrantes)
2. [🎯 Descripción del Módulo](#2--descripción-del-módulo)
3. [⚙️ Tecnologías Utilizadas](#3--tecnologías-utilizadas)
4. [🏗️ Cómo Funciona el Módulo](#4--cómo-funciona-el-módulo)
   - [4.1 Módulos con los que se comunica](#41-módulos-con-los-que-se-comunica)
   - [4.2 Patrones utilizados](#42-patrones-utilizados)
   - [4.3 Estilo de arquitectura detallado](#43-estilo-de-arquitectura-detallado)
   - [4.4 Algoritmo de priorización](#44-algoritmo-de-priorización)
5. [📊 Diagramas](#5--diagramas)
   - [5.1 Diagrama de Datos](#51-diagrama-de-datos)
   - [5.2 Diagrama de Clases](#52-diagrama-de-clases)
   - [5.3 Diagrama de Componentes](#53-diagrama-de-componentes)
   - [5.4 Diagrama de Secuencia](#54-diagrama-de-secuencia)
6. [⚡ Funcionalidades](#6--funcionalidades)
   - [6.1 R14 — Priorización de Tareas](#61-r14--priorización-de-tareas)
   - [6.2 R15 — Balance de Tiempo](#62-r15--balance-de-tiempo)
   - [6.3 R16 — Distribución Automática](#63-r16--distribución-automática)
   - [6.4 R17 — Rebalanceo Dinámico](#64-r17--rebalanceo-dinámico)
   - [6.5 AIB-22.1 — Priorización Automática](#65-aib-221--priorización-automática)
   - [6.6 AIB-22.2 — Recomendación de Tareas Críticas](#66-aib-222--recomendación-de-tareas-críticas)
   - [6.7 AIB-22.3 — Detección de Tareas de Alto Riesgo](#67-aib-223--detección-de-tareas-de-alto-riesgo)
   - [6.8 AIB-22.4 — Ajuste Automático de Estimaciones](#68-aib-224--ajuste-automático-de-estimaciones)
   - [6.9 AIB-23 — Balanceador de Tiempo](#69-aib-23--balanceador-de-tiempo)
   - [6.10 AIB-24 — Distribución Automática de Tareas](#610-aib-24--distribución-automática-de-tareas)
   - [6.11 AIB-25 — Protección del Tiempo Personal](#611-aib-25--protección-del-tiempo-personal)
   - [6.12 AIB-26 — Rebalanceo Dinámico de Tareas](#612-aib-26--rebalanceo-dinámico-de-tareas)
   - [6.13 AIB-27 — Protección de Sobrecarga Académica](#613-aib-27--protección-de-sobrecarga-académica)
7. [🔌 Conexiones con Servicios Externos](#7--conexiones-con-servicios-externos)
8. [⚠️ Manejo de Errores](#8--manejo-de-errores)
9. [📋 Estrategia de Versionamiento y Branches](#9--estrategia-de-versionamiento-y-branches)
   - [9.1 Convenciones para crear ramas](#91-convenciones-para-crear-ramas)
   - [9.2 Convenciones para crear commits](#92-convenciones-para-crear-commits)
10. [🧪 Evidencia de Pruebas Unitarias](#10--evidencia-de-pruebas-unitarias)
11. [📈 Evidencia de Análisis de Cobertura](#11--evidencia-de-análisis-de-cobertura)
12. [🗂️ Código Organizado por Carpetas](#12--código-organizado-por-carpetas)
13. [🚀 Cómo Ejecutar el Proyecto](#13--cómo-ejecutar-el-proyecto)
14. [☁️ CI/CD y Despliegue en Azure](#14--cicd-y-despliegue-en-azure)
    - [14.1 Pipeline de Desarrollo (DEV)](#141-pipeline-de-desarrollo-dev)
    - [14.2 Pipeline de Producción (PROD)](#142-pipeline-de-producción-prod)
    - [14.3 Evidencia del Despliegue](#143-evidencia-del-despliegue)
    - [14.4 Link Swagger en Azure](#144-link-swagger-en-azure)
15. [🔐 Variables de Entorno](#15--variables-de-entorno)
16. [📚 Referencias](#16--referencias)

---

## 1. 👤 Integrantes

**Módulo 4 — Motor de Planificación Inteligente**
**Proyecto:** A.IBERT — ECI Planner
**Institución:** Escuela Colombiana de Ingeniería Julio Garavito

<div align="center">

| 👨‍💻 Integrante                  | 🎓 Rol               |
|-------------------------------|----------------------|
| Juan Esteban Sánchez García   | Developer            |
| Juan Carlos Bohórquez Monroy  | Developer            |
| Jeyder Nicolay Leon Lancheros | Developer            |
| Nicolás Guillermo Ibañez León | Developer            |

</div>

---

## 2. 🎯 Descripción del Módulo

El **Motor de Planificación Inteligente** es el núcleo de decisión del sistema A.IBERT — ECI Planner.

No se limita a listar tareas, sino que analiza el contexto académico completo del estudiante para generar planes optimizados de estudio y trabajo.

<div align="center">

| ✅ **Qué hace** | ❌ **Problema que resuelve** |
|:---------------|:-----------------------------|
| Analiza el contexto académico del estudiante | Mala priorización de tareas |
| Calcula prioridades automáticamente con IA | Sobrecarga en días específicos |
| Distribuye la carga de trabajo semanalmente | Falta de planificación estratégica |
| Rebalancea el plan cuando el estudiante falla | Estrés académico acumulado |

</div>

### Microservicios del Módulo 4

| Microservicio           | Puerto | Responsabilidad        |
|-------------------------|--------|------------------------|
| planning-engine-service | 8004   | Motor de planificación |

---

## 3. ⚙️ Tecnologías Utilizadas

<div align="center">

| **Tecnología / Herramienta** | **Uso principal en el proyecto** |
|------------------------------|----------------------------------|
| **Java 21** | Lenguaje de programación base del microservicio backend. |
| **Spring Boot** | Framework principal para construir el microservicio, exponiendo APIs REST y gestionando configuración e inyección de dependencias. |
| **Spring Web** | Exposición de endpoints REST (controladores HTTP) dentro de la arquitectura hexagonal. |
| **Spring Security + JWT** | Autenticación y autorización mediante tokens JWT, asegurando el acceso a los endpoints. |
| **Spring Data JPA** | Integración con la base de datos relacional usando el patrón Repository. |
| **PostgreSQL** | Base de datos relacional para persistir planes, tareas priorizadas y distribuciones generadas. |
| **Apache Maven** | Gestión de dependencias, empaquetado del microservicio y automatización de builds en CI/CD. |
| **Lombok** | Reducción de código repetitivo con anotaciones como `@Getter`, `@Builder` y `@AllArgsConstructor`. |
| **JUnit 5** | Framework de pruebas unitarias para validar la lógica de dominio y casos de uso. |
| **Mockito** | Simulación de dependencias (puertos, repositorios, clientes externos) en pruebas unitarias. |
| **JaCoCo** | Generación de reportes de cobertura de código para evaluar la efectividad de las pruebas. |
| **SonarQube** | Análisis estático del código e identificación de vulnerabilidades y code smells. |
| **Swagger (OpenAPI 3)** | Generación automática de documentación interactiva de los endpoints REST. |
| **Postman** | Validación manual de peticiones y respuestas JSON de los endpoints. |
| **Docker** | Contenerización del microservicio para despliegues aislados y consistentes. |
| **Azure App Service** | Entorno de ejecución en la nube donde se despliega el contenedor Docker. |
| **Azure Container Registry (ACR)** | Almacenamiento y versionado de las imágenes Docker generadas en CI/CD. |
| **GitHub Actions** | Pipelines de integración y despliegue continuo (CI/CD). |
| **Gemini / Groq API** | Integración con modelos de IA para generación y optimización de planes académicos. |

</div>

> 🧠 **Stack seleccionado** para garantizar **escalabilidad**, **modularidad**, **seguridad** y **mantenibilidad**, aplicando buenas prácticas de ingeniería de software.

---

## 4. 🏗️ Cómo Funciona el Módulo

### 4.1 Módulos con los que se comunica

El `planning-engine-service` consume información de otros microservicios del ecosistema A.IBERT a través de HTTP REST:

```
planning-engine-service
 ├── task-service        → Obtiene las tareas académicas del estudiante
 ├── user-service        → Obtiene el perfil y disponibilidad del estudiante
 └── academic-service    → Obtiene el peso académico de la materia
```

<div align="center">

| 🌍 **Microservicio** | ⚙️ **Operación** | 📋 **Propósito** |
|:---------------------|:----------------|:-----------------|
| **task-service** | GET tareas del estudiante | Obtener lista de tareas pendientes para planificar |
| **user-service** | GET perfil del estudiante | Obtener disponibilidad horaria y carga académica |
| **academic-service** | GET peso académico | Obtener el peso de la materia en la carga semestral |

</div>

### 4.2 Patrones utilizados

<div align="center">

| 🎨 **Patrón** | 📋 **Descripción** |
|:-------------|:-------------------|
| **Ports & Adapters (Hexagonal)** | Separación entre lógica de negocio e infraestructura |
| **Repository Pattern** | Abstracción del acceso a datos con JPA |
| **Strategy Pattern** | Estrategias intercambiables para diferentes algoritmos de planificación |
| **Factory Method** | Creación centralizada y validada de entidades de dominio |
| **DTO Pattern** | Separación entre objetos de transferencia y entidades de dominio |
| **Feign Client** | Comunicación declarativa HTTP con otros microservicios |

</div>

### 4.3 Estilo de arquitectura detallado

El microservicio implementa **Clean Architecture** con enfoque **Hexagonal (Ports & Adapters)**:

```
┌─────────────────────────────────────────────────┐
│                  ENTRYPOINTS                    │
│         (Controllers REST / Swagger)            │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│                  APPLICATION                    │
│         (Use Cases / Services / DTOs)           │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│                    DOMAIN                       │
│    (Entities / Domain Logic / Port Interfaces)  │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│               INFRASTRUCTURE                    │
│   (JPA Repositories / Feign Clients / Config)   │
└─────────────────────────────────────────────────┘
```

**Flujo de dependencias:** `Entrypoints → Application → Domain ← Infrastructure`

> La capa de **Domain** no depende de ninguna otra capa; es el núcleo de la aplicación.

### 4.4 Algoritmo de priorización

El motor calcula un **score de prioridad** combinando tres factores ponderables:

```
minutosCorregidos = estimatedHours * 60 * timeCorrectionFactor

score = (pesoAcademico * 100) * weightAcademic
      + (puntuacionProximidad) * weightProximity
      + (puntuacionTiempo) * weightTime

score final → redondeado a Integer en [0, 100]
```

**Precisión del deadline:** cuando la tarea tiene un campo `dueDateTime` (`LocalDateTime`), se usa ese timestamp exacto para calcular las horas restantes; si no existe, se infiere `dueDate.atTime(23:59)`.

**Proximidad (horas restantes hasta el deadline):**

| Horas restantes | puntuacionProximidad |
|----------------|----------------------|
| ≤ 24 | CRITICAL automático (score = 100) |
| ≤ 48 | 80 |
| ≤ 120 | 60 |
| > 120 | 40 |

**Tiempo estimado (AIB-22.4):**

```
puntuacionTiempo = min((minutosCorregidos / 3), 100)
```

**Factor de corrección de duración (AIB-22.4):**  
Configurable vía `planning.priority.time-correction-factor` (default `1.0`). Permite ajustar los minutos estimados antes del cálculo sin modificar el dato original de la tarea.

**Pesos por defecto (configurables vía `PriorityWeightsProperties`):**

| Factor | Propiedad | Default |
|--------|-----------|---------|
| Proximidad al deadline | `planning.priority.weight-proximity` | 0.40 |
| Peso académico | `planning.priority.weight-academic` | 0.40 |
| Tiempo estimado | `planning.priority.weight-time` | 0.20 |
| Factor de corrección | `planning.priority.time-correction-factor` | 1.0 |

Si no hay deadline, la prioridad es `LOW` y el score es `0`.

---

## 5. 📊 Diagramas

### 5.1 Diagrama de Datos

> 📌 *Pendiente de implementación — Inserta aquí el diagrama de entidad-relación de la base de datos PostgreSQL.*

```
┌──────────────────┐         ┌───────────────────┐
│   PLAN_SEMANAL   │ 1     N │  TAREA_PLANIFICADA │
│──────────────────│─────────│───────────────────│
│ id (PK)          │         │ id (PK)            │
│ student_id       │         │ plan_id (FK)       │
│ fecha_inicio     │         │ task_id            │
│ fecha_fin        │         │ score_prioridad    │
│ estado           │         │ fecha_asignada     │
│ created_at       │         │ tiempo_estimado    │
└──────────────────┘         └───────────────────┘
```

> 📌 *Reemplaza el diagrama ASCII por la imagen real del diagrama de base de datos.*

<div align="center">
<img src="docs/images/diagrama-datos.png" alt="Diagrama de Datos" width="600"/>
</div>

---

### 5.2 Diagrama de Clases

> 📌 *Inserta aquí el diagrama de clases del dominio.*

<div align="center">
<img src="docs/images/diagrama-clases.png" alt="Diagrama de Clases" width="600"/>
</div>

**Resumen del diseño de dominio:**

- **`PlanningEngine`** — Entidad central que orquesta la generación del plan académico.
- **`Task`** — Objeto de valor con atributos: peso académico, fecha límite, tiempo estimado.
- **`WeeklyPlan`** — Entidad que representa la distribución semanal de tareas.
- **`PriorityScore`** — Objeto de valor inmutable que encapsula el resultado del algoritmo.
- **`PlanningStrategy`** — Interfaz (Port) que define el contrato de las estrategias de planificación.

---

### 5.3 Diagrama de Componentes

> 📌 *Inserta aquí el diagrama de componentes específico del microservicio.*

<div align="center">
<img src="docs/images/diagrama-componentes.png" alt="Diagrama de Componentes" width="600"/>
</div>

**Flujo principal:**

- **`PlanningController`** → Recibe solicitudes HTTP y delega al puerto `PlanningUseCases`.
- **`PlanningService`** → Orquesta la lógica: obtiene tareas, calcula prioridades, genera distribución.
- **`PriorityCalculator`** → Aplica el algoritmo de scoring sobre cada tarea.
- **`WeeklyDistributor`** → Distribuye las tareas priorizadas en los días disponibles.
- **`PlanningRepositoryAdapter`** → Persiste y recupera planes desde PostgreSQL.
- **`TaskServiceClient`** / **`UserServiceClient`** → Feign Clients que consultan microservicios externos.

---

### 5.4 Diagrama de Secuencia

> 📌 *Inserta aquí los diagramas de secuencia por funcionalidad.*

<div align="center">
<img src="docs/images/diagrama-secuencia-priorizacion.png" alt="Diagrama de Secuencia — Priorización" width="600"/>
</div>

---

## 6. ⚡ Funcionalidades

> **Versión actualizada:** Todos los endpoints ahora retornan mensajes en español, usan los niveles de prioridad `CRITICAL / HIGH / MEDIUM / LOW` según el requerimiento AIB-22, e incluyen los campos `movedTasks` (R17) y `weeklyLoadAnalysis` (R15).

---

### 6.1 R14 — Motor de Priorización de Tareas

Calcula automáticamente la prioridad de las tareas académicas del estudiante combinando peso académico, proximidad del deadline y tiempo estimado. Aplica escalado a **CRITICAL** cuando el deadline es menor o igual a 24 horas.

**Endpoint:**
`GET /planning/prioritization?forceRecalculate={bool}`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `X-Student-Id` | `String` | Obligatorio (Header) | Identificador del estudiante. |
| `forceRecalculate` | `Boolean` | Opcional (default: false) | Si es `true`, recalcula todos los scores aunque ya existan. |
| `forzarRecalculo` | `Boolean` | Opcional (alias español) | Equivalente a `forceRecalculate`. Cualquiera de los dos activa el recálculo. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `message` | `String` | `"¡Tareas priorizadas exitosamente!"` o `"No hay tareas activas para priorizar"` |
| `data[].id` | `String` | Alias de `taskId`. Identificador único de la tarea. |
| `data[].taskId` | `String` | Identificador único de la tarea. |
| `data[].title` | `String` | Nombre de la tarea académica. |
| `data[].subjectId` | `String` | ID de la materia asociada. |
| `data[].taskType` | `String` | Tipo: `TAREA` / `EXAMEN` / `PROYECTO` / `LECTURA` / `OTRO`. |
| `data[].deadline` | `LocalDateTime` | Fecha límite con hora exacta (ISO 8601). Preserva el tiempo original del task-service. |
| `data[].scheduledDate` | `LocalDateTime` | Fecha y hora de estudio asignada. `null` si no distribuida. |
| `data[].estimatedDurationMinutes` | `Integer` | Minutos estimados **corregidos** (tras aplicar `timeCorrectionFactor`). |
| `data[].status` | `String` | Estado: `TODO` / `IN_PROGRESS` / `COMPLETED` / `SCHEDULED`. |
| `data[].priorityScore` | `Integer` | Puntaje calculado redondeado, rango [0 – 100]. |
| `data[].priorityLevel` | `String` | Nivel: `CRITICAL` / `HIGH` / `MEDIUM` / `LOW`. |
| `data[].priority` | `String` | Alias de `priorityLevel`. |
| `data[].lastUpdated` | `LocalDateTime` | Timestamp ISO 8601 del último recálculo de prioridad. |

</div>

#### 🧮 Algoritmo de Prioridad (R14 / AIB-22.4)

```
minutosCorregidos = estimatedHours * 60 * timeCorrectionFactor

score = (pesoAcademico * 100) * weightAcademic
      + (puntuacionProximidad) * weightProximity
      + (puntuacionTiempo) * weightTime

resultado → Integer redondeado en [0, 100]

puntuacionProximidad (horasLeft calculadas sobre dueDateTime exacto):
  <= 24h  → CRITICAL automático (score = 100)
  <= 48h  → 80
  <= 120h → 60
  > 120h  → 40

puntuacionTiempo = min((minutosCorregidos / 3), 100)
```

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
GET /planning/prioritization?studentId=STU-001&forceRecalculate=true
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "¡Tareas priorizadas exitosamente!",
  "data": [
    {
      "id": "TASK-101",
      "taskId": "TASK-101",
      "title": "Parcial de Cálculo Diferencial",
      "subjectId": "CALC-201",
      "taskType": "EXAMEN",
      "deadline": "2026-05-13T23:59:00",
      "scheduledDate": "2026-05-12T08:00:00",
      "estimatedDurationMinutes": 180,
      "status": "TODO",
      "priorityScore": 100,
      "priorityLevel": "CRITICAL",
      "priority": "CRITICAL",
      "lastUpdated": "2026-05-12T19:00:00"
    },
    {
      "id": "TASK-102",
      "taskId": "TASK-102",
      "title": "Taller de Programación",
      "subjectId": "PROG-101",
      "taskType": "TAREA",
      "deadline": "2026-05-20T23:59:00",
      "scheduledDate": null,
      "estimatedDurationMinutes": 120,
      "status": "TODO",
      "priorityScore": 63,
      "priorityLevel": "MEDIUM",
      "priority": "MEDIUM",
      "lastUpdated": "2026-05-12T19:00:00"
    }
  ],
  "timestamp": "2026-05-12T19:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | studentId vacío | `"Student ID cannot be null or empty"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido o ausente | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ usuario autenticado | `"studentId does not match authenticated user"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error en algoritmo | `"Error calculating priority score"` |

</div>

---

### 6.2 R15 — Balanceador de Tiempo

Analiza la distribución semanal del estudiante, detecta días con sobrecarga (>80%) y días vacíos (<20%), y genera sugerencias de redistribución.

**Endpoint:**
`GET /planning/balance?studentId={id}&weekStartDate={fecha}`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `studentId` | `String` | Obligatorio (Query Param) | Identificador del estudiante. |
| `weekStartDate` | `Date` | Opcional (ISO 8601, default: lunes actual) | Lunes de la semana a analizar. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `message` | `String` | `"La semana está bien distribuida"` o `"Se detectaron días con sobrecarga, se sugiere redistribuir"` |
| `weeklyLoadAnalysis[]` | `Array` | Análisis por día: horasDisponibles, horasAsignadas, % ocupación, estado. |
| `overloadedDays[]` | `Array<String>` | Días con carga > 80% (RN-01). |
| `emptyDays[]` | `Array<String>` | Días con carga < 20% (RN-02). |
| `balanceSuggestions[].task` | `Object` | Tarea sugerida a mover. |
| `balanceSuggestions[].fromDate` | `Date` | Día original. |
| `balanceSuggestions[].toDate` | `Date` | Día sugerido. |
| `balanceSuggestions[].reason` | `String` | Razón de la sugerencia. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
GET /planning/balance?studentId=STU-001&weekStartDate=2026-05-11
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "Se detectaron días con sobrecarga, se sugiere redistribuir",
  "data": {
    "studentId": "STU-001",
    "weeklyLoadAnalysis": [
      {
        "date": "2026-05-11",
        "availableHours": 6.0,
        "assignedHours": 5.5,
        "occupancyPercent": 91.67,
        "status": "OVERLOADED"
      },
      {
        "date": "2026-05-12",
        "availableHours": 8.0,
        "assignedHours": 1.0,
        "occupancyPercent": 12.5,
        "status": "FREE"
      }
    ],
    "overloadedDays": ["lunes 2026-05-11"],
    "emptyDays": ["martes 2026-05-12"],
    "balanceSuggestions": [
      {
        "task": { "taskId": "TASK-103", "title": "Laboratorio de Física" },
        "fromDate": "2026-05-11",
        "toDate": "2026-05-12",
        "reason": "El día lunes 2026-05-11 está sobrecargado. El día martes 2026-05-12 tiene tiempo libre.",
        "suggestionMessage": "Move task 'Laboratorio de Física' from 2026-05-11 to 2026-05-12 to balance workload."
      }
    ]
  },
  "timestamp": "2026-05-12T19:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"studentId does not match authenticated user"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Error analyzing weekly balance"` |

</div>

> **Flujos alternos manejados:**
> - Sin disponibilidad configurada → `"Configura tu disponibilidad diaria para activar el balanceador."`
> - Sin tareas en la semana → `"No hay tareas registradas para esta semana."`

---

### 6.3 R16 — Distribución Automática de Tareas

Genera un plan semanal optimizado que distribuye las tareas priorizadas respetando la disponibilidad del estudiante. Los bloques marcados como `PERSONAL`, `DESCANSO` o `SOCIAL` nunca reciben tareas académicas (**RN-04**).

**Endpoint:**
`POST /planning/distribution?studentId={id}`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `studentId` | `String` | Obligatorio (Query Param) | Identificador del estudiante. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

> La disponibilidad horaria y las tareas pendientes se obtienen automáticamente desde `task-service` y `profile-service` vía Feign.

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `message` | `String` | `"¡Plan de trabajo generado exitosamente!"` o aviso de tareas sin asignar. |
| `fullyAssigned` | `Boolean` | `true` si todas las tareas fueron asignadas. |
| `assignedBlocks[]` | `Array` | Cada bloque: taskId, día, startTime, endTime, durationHours. |
| `unassignedTasks[]` | `Array` | Tareas no asignadas por falta de disponibilidad. |
| `criticalAlerts[]` | `Array` | Tareas con deadline < 24h que requieren atención inmediata. |
| `movedTasks[]` | `Array` | (En rebalanceo) taskId, bloque original y bloque nuevo. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
POST /planning/distribution?studentId=STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "¡Plan de trabajo generado exitosamente!",
  "data": {
    "studentId": "STU-001",
    "fullyAssigned": true,
    "assignedBlocks": [
      {
        "task": {
          "taskId": "TASK-101",
          "title": "Parcial de Cálculo",
          "priorityScore": 100.0,
          "priorityLevel": "CRITICAL"
        },
        "date": "2026-05-13",
        "startTime": "08:00",
        "endTime": "11:00",
        "durationHours": 3.0
      }
    ],
    "unassignedTasks": [],
    "criticalAlerts": [],
    "movedTasks": []
  },
  "timestamp": "2026-05-12T19:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"studentId does not match authenticated user"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error de generación | `"Error generating weekly distribution plan"` |

</div>

---

### 6.4 R17 — Rebalanceo Dinámico de Tareas

Reorganiza el plan cuando el estudiante no cumple con una tarea asignada, redistribuyendo la carga restante. Rastrea exactamente qué tareas se movieron (**`movedTasks`**) y nunca asigna tareas en bloques de tiempo personal o descanso (**RN-03**).

#### Endpoint 1 — Registrar Fallo y Rebalancear

`POST /planning/rebalance/failure`

---

#### 📦 Información de Entrada (Request — Failure Report)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `studentId` | `String` | Obligatorio | Identificador del estudiante. |
| `taskId` | `String` | Obligatorio | Tarea con bloque asignado no completada. |
| `failedDate` | `Date` | Obligatorio | Fecha en que ocurrió el fallo. |
| `hoursMissed` | `Float` | Obligatorio | Horas de estudio no completadas. |
| `reason` | `String` | Opcional | Razón del incumplimiento. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### Endpoint 2 — Reorganizar Plan Manualmente

`POST /planning/rebalance/reorganize?studentId={id}`

---

#### 📦 Información de Salida (Ambos endpoints)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `message` | `String` | `"Plan reorganizado exitosamente"` o `"Hay tareas críticas que requieren tu atención inmediata"` |
| `fullyAssigned` | `Boolean` | Si todo pudo ser reasignado. |
| `assignedBlocks[]` | `Array` | Nuevo plan semanal con bloques actualizados. |
| `unassignedTasks[]` | `Array` | Tareas que no pudieron ser ubicadas. |
| `criticalAlerts[]` | `Array` | Tareas con deadline < 24h sin tiempo disponible (RN-02). |
| `movedTasks[]` | `Array` | **Nuevo R17** — Lista de tareas reubicadas con bloque original y nuevo bloque. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```json
POST /planning/rebalance/failure
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "studentId": "STU-001",
  "taskId": "TASK-101",
  "failedDate": "2026-05-13",
  "hoursMissed": 2.0,
  "reason": "Tuve un imprevisto familiar"
}
```

**Response `200 OK`:**
```json
{
  "message": "Plan reorganizado exitosamente",
  "data": {
    "studentId": "STU-001",
    "fullyAssigned": true,
    "assignedBlocks": [...],
    "unassignedTasks": [],
    "criticalAlerts": [],
    "movedTasks": [
      {
        "taskId": "TASK-101",
        "taskTitle": "Parcial de Cálculo Diferencial",
        "originalDate": "2026-05-13",
        "originalStartTime": "08:00",
        "newDate": "2026-05-14",
        "newStartTime": "09:00",
        "reason": "Rebalanceo por tarea no completada el 2026-05-13"
      }
    ]
  },
  "timestamp": "2026-05-12T19:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Datos inválidos | `"Plan ID cannot be null or empty"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"studentId does not match authenticated user"` |
| ![422](https://img.shields.io/badge/422-Unprocessable-yellow?style=flat) | Sin tiempo disponible | `"No hay tiempo disponible para reorganizar. Te recomendamos revisar tus prioridades."` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Error during plan reorganization"` |

</div>

---

---

### 6.5 AIB-22.1 — Priorización Automática

Sub-funcionalidad del Motor de Priorización (AIB-22). El sistema recalcula y asigna automáticamente el nivel de prioridad a cada tarea sin intervención manual del estudiante, cada vez que se detecta un cambio relevante.

**Disparador:** Se ejecuta internamente al detectar eventos de cambio en tareas (registro, edición, completado).

---

#### 🔄 Flujo Básico

<div align="center">

| Paso | Actor | Descripción |
|:----:|-------|-------------|
| 1 | Estudiante | Registra, edita o completa una tarea en el sistema. |
| 2 | planning-service | Detecta el evento y activa `PrioritizeTasksUseCase`. |
| 3 | planning-service | Recupera todas las tareas activas (`TODO`/`IN_PROGRESS`) desde `task-service`. |
| 4 | planning-service | Recalcula `priorityScore` y `priorityLevel` para cada tarea. |
| 5 | Estudiante | Visualiza su lista de tareas actualizada y ordenada por prioridad. |

</div>

---

#### 📦 Datos de Entrada (Internos)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `taskList` | `List<TaskServiceResponse>` | Tareas activas recuperadas del task-service via Feign. |
| `id` | `String (UUID)` | Identificador de la tarea. |
| `deadline` | `LocalDateTime` | Fecha límite (ISO 8601). |
| `estimatedDurationMinutes` | `Integer` | Minutos estimados de trabajo. |
| `type` | `TaskType` | `TAREA` / `EXAMEN` / `PROYECTO` / `LECTURA` / `OTRO` |
| `status` | `TaskStatus` | Solo `TODO` o `IN_PROGRESS`. |

</div>

---

#### 📦 Datos de Salida

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `taskId` | `String` | Identificador de la tarea (UUID). |
| `id` | `String` | Alias de `taskId`. |
| `title` | `String` | Título de la tarea (máx. 200 caracteres). |
| `subjectId` | `String` | ID de la materia asociada. |
| `taskType` | `String` | `TAREA` / `EXAMEN` / `PROYECTO` / `LECTURA` / `OTRO` |
| `deadline` | `LocalDateTime` | Fecha límite con hora exacta (ISO 8601). |
| `priorityScore` | `Integer` | Puntuación entera [0 – 100] (mayor = más urgente). |
| `priorityLevel` | `String` | `LOW` (< 40) / `MEDIUM` (40-69) / `HIGH` (≥ 70) / `CRITICAL` (deadline < 24h). |
| `priority` | `String` | Alias de `priorityLevel`. |
| `estimatedDurationMinutes` | `Integer` | Minutos estimados corregidos por `timeCorrectionFactor`. |
| `status` | `String` | `TODO` / `IN_PROGRESS`. |
| `scheduledDate` | `LocalDateTime` | Fecha y hora de estudio asignada. `null` si no distribuida. |
| `lastUpdated` | `LocalDateTime` | Timestamp ISO 8601 del último recálculo. |

</div>

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | El recálculo se ejecuta automáticamente al registrar, editar o completar una tarea. |
| RN-02 | El proceso no interrumpe la navegación del estudiante (corre en segundo plano). |
| RN-03 | El evento disparador es interno del sistema y no requiere acción del estudiante. |

</div>

#### 🔀 Flujo Alterno

| Código | Descripción |
|--------|-------------|
| FA-01 | No hay tareas activas al momento del evento → el sistema omite el recálculo y registra en log: `"FA-01: No active tasks found for student '{}'. Skipping prioritization."` |

---

### 6.2 R15 — Balance de Tiempo

Analiza la distribución de la carga académica semanal del estudiante, detectando días sobrecargados y días con poca actividad.

**Endpoint:**
`GET /planning/balance`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `studentId` | `String` | Obligatorio (Query Param) | Identificador del estudiante. |
| `weekStart` | `String` | Obligatorio (Query Param) | Fecha de inicio de semana (ISO 8601). |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `date` | `String` | Fecha del día analizado. |
| `occupancyPercentage` | `Double` | Porcentaje de ocupación del día (0-100). |
| `status` | `Enum` | Estado: `OVERLOADED` (>80%), `BALANCED`, `EMPTY` (<20%). |
| `totalMinutes` | `Integer` | Total de minutos de trabajo asignados. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
GET /planning/balance?studentId=STU-001&weekStart=2025-08-04
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "weekStart": "2025-08-04",
  "weekEnd": "2025-08-10",
  "days": [
    {
      "date": "2025-08-04",
      "occupancyPercentage": 85.0,
      "status": "OVERLOADED",
      "totalMinutes": 510
    },
    {
      "date": "2025-08-05",
      "occupancyPercentage": 50.0,
      "status": "BALANCED",
      "totalMinutes": 300
    },
    {
      "date": "2025-08-06",
      "occupancyPercentage": 10.0,
      "status": "EMPTY",
      "totalMinutes": 60
    }
  ]
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Fecha inválida | `"Invalid date format. Use ISO 8601"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![404](https://img.shields.io/badge/404-Not_Found-orange?style=flat) | Sin datos | `"No schedule found for student in given week"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Error analyzing weekly balance"` |

</div>

---

### 6.3 R16 — Distribución Automática

Genera un plan semanal optimizado que distribuye las tareas priorizadas respetando la disponibilidad horaria del estudiante.

**Endpoint:**
`POST /planning/distribution`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `studentId` | `String` | Obligatorio | Identificador del estudiante. |
| `weekStart` | `String` | Obligatorio | Fecha de inicio de la semana de planificación. |
| `availableHoursPerDay` | `Map<String, Integer>` | Obligatorio | Horas disponibles por día de la semana. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `planId` | `String` | Identificador único del plan generado. |
| `studentId` | `String` | Identificador del estudiante. |
| `weekStart` | `String` | Inicio de semana del plan. |
| `weekEnd` | `String` | Fin de semana del plan. |
| `dailyAssignments` | `List<Object>` | Lista de tareas distribuidas por día. |
| `optimizationScore` | `Double` | Puntaje de optimización del plan generado. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```json
POST /planning/distribution
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "studentId": "STU-001",
  "weekStart": "2025-08-04",
  "availableHoursPerDay": {
    "MONDAY": 3,
    "TUESDAY": 4,
    "WEDNESDAY": 2,
    "THURSDAY": 5,
    "FRIDAY": 3
  }
}
```

**Response `201 Created`:**
```json
{
  "planId": "PLAN-2025-0042",
  "studentId": "STU-001",
  "weekStart": "2025-08-04",
  "weekEnd": "2025-08-08",
  "optimizationScore": 0.87,
  "dailyAssignments": [
    {
      "date": "2025-08-04",
      "tasks": [
        {
          "taskId": "TASK-101",
          "taskName": "Parcial de Cálculo Diferencial",
          "allocatedMinutes": 120,
          "priorityScore": 0.92
        }
      ]
    }
  ]
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Datos inválidos | `"Available hours must be greater than 0"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![409](https://img.shields.io/badge/409-Conflict-orange?style=flat) | Plan ya existente | `"A plan already exists for this student and week"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error de generación | `"Error generating weekly distribution plan"` |

</div>

---

### 6.4 R17 — Rebalanceo Dinámico

Reorganiza el plan académico cuando el estudiante no cumple con las tareas asignadas, redistribuyendo la carga restante en los días disponibles.

#### Endpoint — Registrar Fallo

`POST /planning/rebalance/failure`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `planId` | `String` | Obligatorio | Identificador del plan a rebalancear. |
| `taskId` | `String` | Obligatorio | Tarea que no fue completada. |
| `failureDate` | `String` | Obligatorio | Fecha en que ocurrió el incumplimiento. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### Endpoint — Reorganizar Plan

`POST /planning/rebalance/reorganize`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `planId` | `String` | Obligatorio | Identificador del plan a reorganizar. |
| `remainingAvailableHours` | `Map<String, Integer>` | Obligatorio | Horas disponibles en los días restantes. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `planId` | `String` | ID del plan reorganizado. |
| `rebalancedAt` | `String` | Timestamp del rebalanceo. |
| `affectedTasks` | `Integer` | Número de tareas redistribuidas. |
| `newDailyAssignments` | `List<Object>` | Nueva distribución de tareas. |
| `feasible` | `Boolean` | Indica si el plan es alcanzable con el tiempo restante. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```json
POST /planning/rebalance/reorganize
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
{
  "planId": "PLAN-2025-0042",
  "remainingAvailableHours": {
    "THURSDAY": 5,
    "FRIDAY": 4,
    "SATURDAY": 6
  }
}
```

**Response `200 OK`:**
```json
{
  "planId": "PLAN-2025-0042",
  "rebalancedAt": "2025-08-06T14:30:00Z",
  "affectedTasks": 3,
  "feasible": true,
  "newDailyAssignments": [
    {
      "date": "2025-08-07",
      "tasks": [
        {
          "taskId": "TASK-101",
          "taskName": "Parcial de Cálculo Diferencial",
          "allocatedMinutes": 180,
          "priorityScore": 0.92
        }
      ]
    }
  ]
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Datos inválidos | `"Plan ID cannot be null or empty"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![404](https://img.shields.io/badge/404-Not_Found-orange?style=flat) | Plan no encontrado | `"Plan not found for given ID"` |
| ![422](https://img.shields.io/badge/422-Unprocessable-yellow?style=flat) | No rebalanceable | `"Not enough time to rebalance remaining tasks"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Error during plan reorganization"` |

</div>

---

### 6.6 AIB-22.2 — Recomendación de Tareas Críticas

Filters prioritized tasks to highlight up to 3 critical recommendations. A task is critical when
its priority is `HIGH` or `CRITICAL` and the deadline is within 48 hours.

**Endpoint:**
`POST /planning/prioritization/critical`

---

#### 📦 Input (Request)

<div align="center">

| Field | Type | Required | Description |
|---|---|:---:|---|
| `X-Student-Id` | `String` | Yes (Header) | Student identifier. |
| `orderedTasks` | `Array` | No | If empty, planning-service will compute priorities using AIB-22. |
| `forceRecalculate` | `boolean` | No | Query param. Forces recalculation before filtering. |
| `Authorization` | `String` | Yes (Header) | JWT Bearer token. |

</div>

---

#### 📦 Output (Response)

<div align="center">

| Field | Type | Description |
|---|---|---|
| `criticalRecommendations` | `Array` | Up to 3 tasks ordered by urgency (CRITICAL first). |
| `totalCritical` | `Integer` | Total number of critical tasks (unlimited). |
| `message` | `String` | User-facing message in English. |

</div>

---

#### ✅ Example

**Request:**
```http
POST /planning/prioritization/critical?forceRecalculate=false
X-Student-Id: STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "You have 2 critical tasks that require immediate attention",
  "data": {
    "criticalRecommendations": [
      {
        "taskId": "TASK-101",
        "title": "Calculus Exam",
        "subjectId": "SUB-01",
        "taskType": "EXAMEN",
        "deadline": "2026-05-15T10:00:00",
        "estimatedDurationMinutes": 120,
        "priorityScore": 92,
        "priorityLevel": "CRITICAL",
        "priority": "CRITICAL",
        "status": "IN_PROGRESS",
        "scheduledDate": "2026-05-14T08:00:00"
      }
    ],
    "totalCritical": 2,
    "message": "You have 2 critical tasks that require immediate attention"
  },
  "timestamp": "2026-05-14T10:00:00"
}
```

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | Una tarea es **crítica** cuando su `priorityLevel` es `HIGH` o `CRITICAL` y el deadline está dentro de las 48 horas. |
| RN-02 | Se retornan hasta **3** recomendaciones, ordenadas por urgencia (`CRITICAL` primero). |
| RN-03 | Si no hay tareas críticas, se retorna una lista vacía con mensaje informativo. |

</div>

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Unexpected server error"` |

</div>

#### 🔀 Flujo Alterno

| Código | Descripción |
|--------|-------------|
| FA-01 | Sin tareas críticas → `"No hay tareas críticas en este momento."` |

---

### 6.7 AIB-22.3 — Detección de Tareas de Alto Riesgo

Analiza las tareas activas del estudiante contra su disponibilidad horaria configurada e identifica aquellas con tiempo disponible insuficiente hasta el deadline. Las tareas se clasifican en dos niveles de riesgo: **HIGH** (menos del 70 % del tiempo estimado disponible) y **MEDIUM** (70–85 %). Las tareas con peso académico superior al 30 % se incluyen primero en el reporte.

**Endpoint:**
`GET /planning/risk/high-risk`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `X-Student-Id` | `String` | Obligatorio (Header) | Identificador del estudiante. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `highRiskTasks[].taskId` | `String` | Identificador único de la tarea. |
| `highRiskTasks[].title` | `String` | Título de la tarea. |
| `highRiskTasks[].riskLevel` | `String` | Nivel de riesgo: `HIGH` o `MEDIUM`. |
| `highRiskTasks[].availableMinutes` | `Integer` | Minutos disponibles hasta el deadline según la agenda del estudiante. |
| `highRiskTasks[].estimatedDurationMinutes` | `Integer` | Minutos estimados para completar la tarea. |
| `highRiskTasks[].academicWeight` | `Double` | Peso académico de la materia (0–1). |
| `riskSummary.totalAtRisk` | `Integer` | Total de tareas en riesgo. |
| `riskSummary.affectedLoadPercentage` | `Double` | Porcentaje de carga académica afectada. |
| `message` | `String` | Resumen del análisis de riesgo en español. |

</div>

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | Una tarea es **HIGH** si `availableMinutes / estimatedDurationMinutes < 0.70`. |
| RN-02 | Una tarea es **MEDIUM** si el cociente está en el rango `[0.70, 0.85)`. |
| RN-03 | Tareas con `academicWeight > 0.30` se ordenan primero en el resultado. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
GET /planning/risk/high-risk
X-Student-Id: STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "Se detectaron 2 tarea(s) en riesgo.",
  "data": {
    "highRiskTasks": [
      {
        "taskId": "TASK-101",
        "title": "Parcial de Cálculo",
        "riskLevel": "HIGH",
        "availableMinutes": 60,
        "estimatedDurationMinutes": 120,
        "academicWeight": 0.40
      }
    ],
    "riskSummary": {
      "totalAtRisk": 2,
      "affectedLoadPercentage": 35.0
    },
    "message": "Se detectaron 2 tarea(s) en riesgo."
  },
  "timestamp": "2026-05-16T10:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Unexpected server error"` |

</div>

#### 🔀 Flujo Alterno

| Código | Descripción |
|--------|-------------|
| FA-01 | Sin tareas en riesgo → `"No se encontraron tareas en riesgo para el estudiante."` |

---

### 6.8 AIB-22.4 — Ajuste Automático de Estimaciones

Registra el tiempo real empleado al completar una tarea y, cuando el estudiante acumula suficientes muestras del mismo tipo (mínimo 5), calcula un factor de corrección personalizado y lo aplica automáticamente a todas las tareas `TODO` pendientes del mismo tipo. El factor se acota al rango `[0.5, 2.0]` para evitar ajustes extremos, y el historial de las últimas 10 muestras se mantiene en memoria por `(studentId, taskType)`.

**Endpoint:**
`POST /planning/estimations/adjust`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `X-Student-Id` | `String` | Obligatorio (Header) | Identificador del estudiante. |
| `completedTaskId` | `String` | Obligatorio | UUID de la tarea recién completada. |
| `actualTime` | `Integer` | Obligatorio, > 0 | Tiempo real empleado, en minutos. |
| `estimatedDurationMinutes` | `Integer` | Obligatorio, > 0 | Duración estimada original de la tarea completada, en minutos. |
| `taskType` | `TaskType` | Obligatorio | `TAREA` / `EXAMEN` / `PROYECTO` / `LECTURA` / `OTRO`. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `adjustmentFactor` | `Double` | Factor de corrección calculado, acotado a `[0.5, 2.0]`. Valores > 1 indican que el estudiante suele tardar más de lo estimado. |
| `updatedEstimates[].id` | `String` | Identificador de la tarea ajustada. |
| `updatedEstimates[].title` | `String` | Título de la tarea. |
| `updatedEstimates[].taskType` | `String` | Tipo de tarea. |
| `updatedEstimates[].originalEstimatedMinutes` | `Integer` | Estimación original antes del ajuste. |
| `updatedEstimates[].adjustedEstimatedMinutes` | `Integer` | Nueva estimación tras aplicar el factor. |
| `message` | `String` | Mensaje en español describiendo el resultado del ajuste. |

</div>

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | Se requieren mínimo **5** tareas completadas del mismo `taskType` para aplicar el factor. |
| RN-02 | `factor = promedio(actualTime / estimatedDurationMinutes)` sobre las últimas **10** tareas del mismo tipo. |
| RN-03 | El factor se acota al rango **[0.5, 2.0]** para evitar ajustes extremos. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
POST /planning/estimations/adjust
X-Student-Id: STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "completedTaskId": "TASK-101",
  "actualTime": 95,
  "estimatedDurationMinutes": 60,
  "taskType": "TAREA"
}
```

**Response `200 OK`:**
```json
{
  "message": "Hemos ajustado las estimaciones de 3 tarea(s) de tipo TAREA.",
  "data": {
    "adjustmentFactor": 1.3,
    "updatedEstimates": [
      {
        "id": "TASK-205",
        "title": "Taller de Programación",
        "taskType": "TAREA",
        "originalEstimatedMinutes": 90,
        "adjustedEstimatedMinutes": 117
      }
    ],
    "message": "Hemos ajustado las estimaciones de 3 tarea(s) de tipo TAREA."
  },
  "timestamp": "2026-05-16T10:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | Cuerpo inválido | `"actualTime must be >= 1"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Unexpected server error"` |

</div>

#### 🔀 Flujos Alternos

| Código | Descripción |
|--------|-------------|
| FA-01 | Menos de 5 muestras del mismo tipo → `adjustmentFactor: 1.0`, sin cambios en estimaciones, mensaje: `"Aún no hay suficientes datos para ajustar estimaciones. Se necesitan al menos 5 tareas completadas del mismo tipo."` |
| FA-02 | `actualTime` no proporcionado o igual a 0 → no actualiza el factor, retorna sin cambios: `"No se registró tiempo real. El factor de ajuste no fue actualizado."` |

---

### 6.9 AIB-23 — Balanceador de Tiempo

Analiza la distribución de carga de trabajo del estudiante durante la semana, detecta días sobrecargados (>80 % de disponibilidad) o con tiempo libre (<20 % de disponibilidad), y emite **hasta 5 sugerencias** de movimiento de tareas para lograr un balance óptimo. El servicio **nunca aplica cambios automáticamente**: solo recomienda (RN-03).

**Endpoint:**
`GET /planning/balance`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `X-Student-Id` | `String` | Obligatorio (Header) | Identificador del estudiante. |
| `weekStartDate` | `LocalDate` | Opcional (Query param, ISO) | Lunes de la semana a analizar. Si se omite, se usa el lunes de la semana actual. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `studentId` | `String` | Identificador del estudiante. |
| `weeklyLoadAnalysis[].date` | `LocalDate` | Fecha del día analizado. |
| `weeklyLoadAnalysis[].availableMinutes` | `Integer` | Minutos disponibles ese día según el perfil del estudiante. |
| `weeklyLoadAnalysis[].assignedMinutes` | `Integer` | Minutos asignados a tareas ese día (suma de duraciones). |
| `weeklyLoadAnalysis[].occupancyPercentage` | `Double` | Porcentaje de ocupación: `(assignedMinutes / availableMinutes) × 100`. |
| `weeklyLoadAnalysis[].status` | `String` | Estado del día: `OVERLOADED` / `FREE` / `BALANCED`. |
| `overloadedDays` | `List<String>` | Etiquetas de días con carga > 80 % (e.g., `"lunes 2026-05-11"`). |
| `emptyDays` | `List<String>` | Etiquetas de días con carga < 20 % (e.g., `"miércoles 2026-05-13"`). |
| `balanceSuggestions[].taskId` | `String` | Identificador de la tarea sugerida para mover. |
| `balanceSuggestions[].taskTitle` | `String` | Título de la tarea. |
| `balanceSuggestions[].fromDay` | `LocalDate` | Día origen (sobrecargado) del cual se recomienda mover la tarea. |
| `balanceSuggestions[].toDay` | `LocalDate` | Día destino (con tiempo libre) al que se recomienda mover la tarea. |
| `balanceSuggestions[].reason` | `String` | Justificación de la sugerencia (máx 200 caracteres). |
| `message` | `String` | Mensaje en español describiendo el resultado global del análisis. |

</div>

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | Un día se considera **sobrecargado** si `assignedMinutes / availableMinutes > 80 %`. |
| RN-02 | Un día se considera **con tiempo libre** si `assignedMinutes / availableMinutes < 20 %`. |
| RN-03 | El servicio emite **máximo 5 sugerencias** y **nunca aplica cambios automáticamente**. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
GET /planning/balance?weekStartDate=2026-05-11
X-Student-Id: STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "Se detectaron días con sobrecarga, se sugiere redistribuir",
  "data": {
    "studentId": "STU-001",
    "weeklyLoadAnalysis": [
      {
        "date": "2026-05-11",
        "availableMinutes": 360,
        "assignedMinutes": 330,
        "occupancyPercentage": 91.67,
        "status": "OVERLOADED"
      },
      {
        "date": "2026-05-13",
        "availableMinutes": 360,
        "assignedMinutes": 60,
        "occupancyPercentage": 16.67,
        "status": "FREE"
      }
    ],
    "overloadedDays": ["lunes 2026-05-11"],
    "emptyDays": ["miércoles 2026-05-13"],
    "balanceSuggestions": [
      {
        "taskId": "TASK-205",
        "taskTitle": "Parcial de Cálculo",
        "fromDay": "2026-05-11",
        "toDay": "2026-05-13",
        "reason": "El día lunes está sobrecargado. El día miércoles tiene tiempo libre."
      }
    ],
    "message": "Se detectaron días con sobrecarga, se sugiere redistribuir"
  },
  "timestamp": "2026-05-16T10:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Unexpected server error"` |

</div>

#### 🔀 Flujos Alternos

| Código | Descripción |
|--------|-------------|
| FA-01 | No hay disponibilidad configurada → respuesta vacía, mensaje: `"Configura tu disponibilidad diaria para activar el balanceador."` |
| FA-02 | No hay tareas planificadas para la semana → respuesta vacía, mensaje: `"No hay tareas registradas para esta semana."` |

---

### 6.10 AIB-24 — Distribución Automática de Tareas

Distribuye automáticamente las tareas pendientes del estudiante en bloques de estudio diarios, respetando su disponibilidad horaria, la prioridad calculada por AIB-22 y el límite de **MAX_MINUTES_PER_DAY = 240 minutos** por día. Las tareas `CRITICAL` y `HIGH` se asignan antes que `MEDIUM` y `LOW` (algoritmo greedy). Aplica el factor de corrección de AIB-22.4 si está disponible. Nunca modifica los bloques de tiempo personal (integración con AIB-25 / AIB-27).

**Endpoint:**
`POST /planning/distribution`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `X-Student-Id` | `String` | Obligatorio (Header) | Identificador del estudiante. |
| `weekStartDate` | `LocalDate` | Opcional (Query param, ISO) | Lunes de la semana a distribuir. Si se omite, se usa el lunes de la semana actual. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `assignedBlocks[].taskId` | `String` | Identificador de la tarea asignada al bloque. |
| `assignedBlocks[].title` | `String` | Título de la tarea para visualización. |
| `assignedBlocks[].scheduledDate` | `LocalDateTime` | Fecha y hora de inicio del bloque de estudio (ISO 8601). |
| `assignedBlocks[].estimatedDurationMinutes` | `Integer` | Duración del bloque en minutos. Suma diaria ≤ `MAX_MINUTES_PER_DAY = 240`. |
| `assignedBlocks[].priority` | `String` | Prioridad de la tarea: `LOW` / `MEDIUM` / `HIGH` / `CRITICAL`. |
| `unassignedTasks[].id` | `String` | Identificador de las tareas que no pudieron asignarse por falta de disponibilidad. |
| `message` | `String` | Resultado de la distribución. |

</div>

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | Las tareas `CRITICAL` y `HIGH` se asignan antes que `MEDIUM` y `LOW`. |
| RN-02 | No se asignan tareas en bloques marcados como `PERSONAL`, `DESCANSO` o `SOCIAL` (AIB-25). |
| RN-03 | La carga diaria no puede superar `MAX_MINUTES_PER_DAY = 240 minutos` (AIB-27). |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
POST /planning/distribution?weekStartDate=2026-05-11
X-Student-Id: STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
{
  "message": "¡Plan de trabajo generado exitosamente!",
  "data": {
    "studentId": "STU-001",
    "assignedBlocks": [
      {
        "taskId": "TASK-101",
        "title": "Parcial de Cálculo",
        "scheduledDate": "2026-05-11T09:00:00",
        "estimatedDurationMinutes": 120,
        "priority": "HIGH"
      },
      {
        "taskId": "TASK-205",
        "title": "Taller de Programación",
        "scheduledDate": "2026-05-11T11:00:00",
        "estimatedDurationMinutes": 90,
        "priority": "MEDIUM"
      }
    ],
    "unassignedTasks": [],
    "message": "¡Plan de trabajo generado exitosamente!"
  },
  "timestamp": "2026-05-16T10:00:00"
}
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error interno | `"Unexpected server error"` |

</div>

#### 🔀 Flujos Alternos

| Código | Descripción |
|--------|-------------|
| FA-01 | Disponibilidad insuficiente → las tareas `CRITICAL` y `HIGH` se asignan primero, las restantes van a `unassignedTasks`. Mensaje: `"Hay tareas que no pudieron asignarse por falta de disponibilidad"`. |
| FA-02 | Sin tareas pendientes → `assignedBlocks` y `unassignedTasks` vacíos. Mensaje: `"No tienes tareas pendientes para distribuir."` |

---

### 6.11 AIB-25 — Protección del Tiempo Personal

Garantiza que ningún bloque de tiempo marcado como `PERSONAL`, `DESCANSO` o `SOCIAL` reciba asignaciones de tareas académicas. La protección es **incondicional** y se aplica automáticamente dentro de AIB-24 y AIB-26. Solo los bloques de tipo `ACADEMICO` son elegibles para asignación. No expone un endpoint REST propio — opera como capa de filtrado dentro de `DistributeTasksUseCaseImpl`.

---

#### 📦 Datos de Entrada (internos)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `timeBlocks[].blockType` | `BlockType` | Tipo semántico: `ACADEMICO` \| `PERSONAL` \| `DESCANSO` \| `SOCIAL` \| `CLASE` \| `OTRO` |
| `timeBlocks[].date` | `LocalDate` | Fecha del bloque. |
| `timeBlocks[].startTime` / `endTime` | `LocalTime` | Ventana horaria del bloque. |

</div>

---

#### 📦 Resultado del Filtrado

<div align="center">

| 🏷️ Campo | 📝 Descripción |
|---|---|
| `filteredBlocks` | Solo bloques `ACADEMICO` disponibles para asignación. |
| `unassignedTasks` | Tareas que no pudieron asignarse por falta de bloques académicos. |
| `message` | Mensaje específico según el FA detectado. |

</div>

---

#### 📋 Reglas de Negocio

<div align="center">

| RN | Descripción |
|----|-------------|
| RN-01 | Los bloques `PERSONAL`, `DESCANSO` y `SOCIAL` no pueden recibir tareas académicas bajo ninguna circunstancia. |
| RN-02 | El estudiante puede cambiar el tipo de un bloque desde su configuración de disponibilidad en cualquier momento. |
| RN-03 | Si todas las tareas quedan sin asignar por falta de bloques `ACADEMICO`, el sistema notifica pero no modifica la configuración personal. |

</div>

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |

</div>

#### 🔀 Flujos Alternos

| Código | Descripción |
|--------|-------------|
| FA-01 | Todos los bloques son `PERSONAL`, `DESCANSO` o `SOCIAL` → `assignedBlocks` vacío, todas las tareas en `unassignedTasks`. Mensaje: `"No hay bloques académicos disponibles. Revisa tu configuración de disponibilidad."` |
| FA-02 | Sin disponibilidad configurada → `assignedBlocks` vacío, tareas en `unassignedTasks`. Mensaje: `"Configura tu disponibilidad horaria para activar la distribución automática."` |

---

### 6.12 AIB-26 — Rebalanceo Dinámico de Tareas

> **Épica:** Rebalanceo automático ante fallos y reorganización manual del plan semanal.

#### 📌 Endpoints

**`POST /planning/rebalance/failure`** — Reporte de bloque fallido

**`POST /planning/rebalance/reorganize`** — Reorganización manual del plan

---

#### ➡️ Input — `POST /planning/rebalance/failure`

<div align="center">

| 📥 **Campo** | 📝 **Tipo** | ✅ **Validación** | 📌 **Descripción** |
|:-------------|:-----------:|:-----------------:|:-------------------|
| `studentId` | `String` | `@NotBlank` | Identificador del estudiante |
| `taskId` | `String` | `@NotBlank` | Identificador de la tarea fallida |
| `failedDate` | `LocalDate` | `@NotNull` | Fecha en que se perdió el bloque |
| `hoursMissed` | `double` | `@Positive` | Horas de estudio perdidas (> 0) |
| `reason` | `String` | `@Size(max=500)` | Motivo del fallo (opcional) |

</div>

#### ➡️ Input — `POST /planning/rebalance/reorganize`

<div align="center">

| 📥 **Header** | 📝 **Tipo** | ✅ **Validación** | 📌 **Descripción** |
|:--------------|:-----------:|:-----------------:|:-------------------|
| `X-Student-Id` | `String` | Requerido | Identificador del estudiante |

</div>

#### ⬅️ Output (ambos endpoints)

<div align="center">

| 📤 **Campo** | 📝 **Tipo** | 📌 **Descripción** |
|:-------------|:-----------:|:-------------------|
| `studentId` | `String` | Identificador del estudiante |
| `assignedBlocks` | `List<ScheduledBlockResponse>` | Bloques de estudio reorganizados |
| `unassignedTasks` | `List<PrioritizedTaskResponse>` | Tareas sin bloque disponible |
| `movedTasks` | `List<MovedTaskResponse>` | Tareas que cambiaron de fecha u hora |
| `criticalAlerts` | `List<PrioritizedTaskResponse>` | Tareas con deadline ≤ mañana marcadas como CRITICAL |
| `fullyAssigned` | `boolean` | `true` si todas las tareas tienen bloque asignado |
| `message` | `String` | Mensaje descriptivo del resultado |

</div>

#### 📏 Reglas de Negocio

| Código | Descripción |
|--------|-------------|
| RN-01 | Al recibir un reporte de fallo (`/failure`), el sistema notifica al servicio externo via `TaskProviderPort.reportTaskFailure()` antes de redistribuir. |
| RN-02 | Toda tarea con `dueDate ≤ mañana` es marcada automáticamente como `CRITICAL` (`priorityScore = max(actual, 100.0)`). Las tareas críticas aparecen en `criticalAlerts`. |
| RN-03 | El campo `movedTasks` incluye toda tarea que cambió de fecha u hora respecto al plan anterior. El motivo indica si fue por fallo reportado o reorganización manual. |

#### ✅ Happy Path

```http
POST /planning/rebalance/failure
Content-Type: application/json
Authorization: Bearer <token>

{
  "studentId": "student-123",
  "taskId": "task-456",
  "failedDate": "2026-05-20",
  "hoursMissed": 2.0,
  "reason": "Tuve un imprevisto"
}
```

```json
{
  "success": true,
  "message": "Hay tareas críticas que requieren tu atención inmediata.",
  "data": {
    "studentId": "student-123",
    "assignedBlocks": [...],
    "unassignedTasks": [],
    "movedTasks": [
      {
        "taskId": "task-789",
        "taskTitle": "Álgebra Lineal",
        "originalDate": "2026-05-20",
        "originalStartTime": "10:00",
        "newDate": "2026-05-21",
        "newStartTime": "09:00",
        "reason": "Rebalanceo por tarea no completada el 2026-05-20"
      }
    ],
    "criticalAlerts": [{"id": "task-456", "title": "Examen Cálculo", ...}],
    "fullyAssigned": true,
    "message": "Hay tareas críticas que requieren tu atención inmediata."
  }
}
```

#### ⚠️ Errores HTTP

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-BadRequest-yellow?style=flat) | Campo obligatorio ausente o inválido (`@NotBlank`, `@Positive`) | `"studentId is required"` / `"hoursMissed must be greater than 0"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido o ausente | `"Invalid or missing JWT token"` |
| ![403](https://img.shields.io/badge/403-Forbidden-red?style=flat) | studentId ≠ autenticado | `"El studentId no coincide con el usuario autenticado"` |

</div>

#### 🔀 Flujos Alternos

| Código | Descripción |
|--------|-------------|
| FA-01 | Sin bloques académicos disponibles tras redistribución → `assignedBlocks` vacío, tareas en `unassignedTasks`. Mensaje: `"No hay bloques académicos disponibles. Revisa tu configuración de disponibilidad."` |
| FA-02 | Sin disponibilidad configurada → `assignedBlocks` vacío. Mensaje: `"Configura tu disponibilidad horaria para activar la distribución automática."` |

---

### 6.13 AIB-27 — Protección de Sobrecarga Académica

> **Épica:** Garantiza que ninguna distribución (AIB-24) ni rebalanceo (AIB-26) supere el límite de **`MAX_MINUTES_PER_DAY = 240 min`** por día. La protección tiene prioridad sobre la prioridad de las tareas: una tarea `CRITICAL` no puede violar el límite.

#### 📌 Aplicación

AIB-27 es una protección **interna** (sin endpoint propio) que se ejecuta dentro de `DistributeTasksUseCaseImpl` y, por extensión, dentro de `RebalanceTasksUseCaseImpl` (que llama al mismo caso de uso). El resultado se incluye en `DistributionPlanResponse`.

#### ⬅️ Nuevos campos en `DistributionPlanResponse`

<div align="center">

| 📤 **Campo** | 📝 **Tipo** | 📌 **Descripción** |
|:-------------|:-----------:|:-------------------|
| `overloadedDays` | `List<OverloadedDayResponse>` | Días donde el plan propuesto habría superado 240 min. Vacío si no hubo sobrecarga. |
| `overloadedDays[].date` | `LocalDate` | Fecha del día sobrecargado (ISO 8601) |
| `overloadedDays[].excessMinutes` | `Integer` | Minutos en exceso = suma propuesta − 240 |
| `message` | `String` | `"Tu plan fue ajustado para respetar tu límite diario de estudio."` si hubo ajustes; de lo contrario el mensaje habitual |

</div>

#### 📏 Reglas de Negocio

| Código | Descripción |
|--------|-------------|
| RN-01 | No se pueden asignar tareas si el total acumulado del día superaría `MAX_MINUTES_PER_DAY = 240`. |
| RN-02 | Si una tarea no cabe en ningún día sin superar el límite, se marca como no asignada (`unassignedTasks`). |
| RN-03 | La protección tiene prioridad sobre la prioridad de la tarea: una tarea `CRITICAL` tampoco puede superar el límite. |

#### 🔍 Cómo funciona internamente

```
1. Se ejecuta una simulación SIN cap (sobre clones de availableDays) para calcular los totales diarios propuestos.
2. Los días donde ese total > 240 se registran en overloadedDays con sus excessMinutes.
3. La distribución real corre CON el cap (lógica preexistente), respetando MAX_MINUTES_PER_DAY.
4. Si se detectaron días sobrecargados, el mensaje del plan se fija en "Tu plan fue ajustado...".
```

#### ✅ Happy Path

```http
POST /planning/distribute   (o rebalance)
X-Student-Id: student-123
```

```json
{
  "success": true,
  "message": "Tu plan fue ajustado para respetar tu límite diario de estudio.",
  "data": {
    "studentId": "student-123",
    "assignedBlocks": [...],
    "unassignedTasks": [],
    "overloadedDays": [
      { "date": "2026-05-20", "excessMinutes": 60 }
    ],
    "fullyAssigned": true,
    "message": "Tu plan fue ajustado para respetar tu límite diario de estudio."
  }
}
```

#### 🔀 Flujos Alternos

| Código | Descripción |
|--------|-------------|
| FA-01 | Sin días con capacidad suficiente: la tarea queda en `unassignedTasks`. Mensaje: `"No es posible redistribuir sin superar tu límite diario. Revisa tu disponibilidad."` |

---

## 7. 🔌 Conexiones con Servicios Externos

El microservicio se comunica con servicios externos a través de **Feign Clients** HTTP REST y **APIs de Inteligencia Artificial**:

<div align="center">

| 🌍 **Servicio Externo** | 🔗 **Tipo de Conexión** | ⚙️ **Operación** | 📋 **Propósito** |
|:------------------------|:------------------------|:----------------|:-----------------|
| **task-service** | Feign Client HTTP | `GET /tasks?studentId={}` | Obtener tareas pendientes del estudiante para priorizar |
| **user-service** | Feign Client HTTP | `GET /users/{id}/profile` | Obtener disponibilidad horaria y perfil académico |
| **Gemini API** | HTTP REST (Google AI) | `POST /generateContent` | Generación y optimización inteligente de planes académicos |
| **Groq API** | HTTP REST (Groq Cloud) | `POST /chat/completions` | Modelo LLM alternativo para análisis y recomendaciones |

</div>

### Configuración de Feign Clients

```java
@FeignClient(name = "task-service", url = "${feign.task-service.url}")
public interface TaskServiceClient {
    @GetMapping("/tasks")
    List<TaskDTO> getTasksByStudent(@RequestParam String studentId,
                                    @RequestHeader("Authorization") String token);
}

@FeignClient(name = "user-service", url = "${feign.profile-service.url}")
public interface UserServiceClient {
    @GetMapping("/users/{id}/profile")
    UserProfileDTO getUserProfile(@PathVariable String id,
                                   @RequestHeader("Authorization") String token);
}
```

> ⚠️ **Nota:** Todas las llamadas a servicios externos incluyen el token JWT en el header `Authorization` para mantener la seguridad de extremo a extremo.

---

## 8. ⚠️ Manejo de Errores

El microservicio implementa un **mecanismo centralizado de manejo de errores** mediante `@ControllerAdvice`, garantizando respuestas uniformes y seguras.

### Estructura de Error Estandarizada

```json
{
  "code": 404,
  "message": "No tasks found for student STU-001",
  "timestamp": "2025-08-06T14:30:00Z",
  "path": "/planning/prioritization"
}
```

### Beneficios del Manejo Centralizado

<div align="center">

| 🎯 **Beneficio** | 📋 **Descripción** |
|:-----------------|:-------------------|
| **🎯 Uniformidad** | Todas las respuestas de error tienen el mismo formato JSON |
| **🔧 Mantenibilidad** | Agregar nuevas excepciones no requiere modificar cada controlador |
| **🔒 Seguridad** | Oculta detalles internos del servidor |
| **📍 Trazabilidad** | Cada error incluye contexto (ruta, timestamp, descripción) |
| **🤝 Integración** | Facilita la comunicación con frontend y herramientas como Postman/Swagger |

</div>

---

## 9. 📋 Estrategia de Versionamiento y Branches

El equipo utiliza **GitFlow** como modelo de ramificación para el control de versiones.

### Ramas y propósito

#### `main`
- **Propósito:** Rama **estable** con la versión final (lista para producción).
- **Reglas:** Solo recibe merges desde `release/*` y `hotfix/*`. Cada merge crea un **tag** SemVer (`vX.Y.Z`). Rama **protegida**: PR obligatorio con 1–2 aprobaciones y checks de CI en verde.

#### `develop`
- **Propósito:** Integración continua de trabajo; base de nuevas funcionalidades.
- **Reglas:** Recibe merges desde `feature/*` y `release/*`. Rama **protegida** similar a `main`.

#### `feature/*`
- **Propósito:** Desarrollo de una funcionalidad específica.
- **Base:** `develop`.
- **Cierre:** Merge a `develop` mediante PR.

#### `release/*`
- **Propósito:** Congelar cambios para estabilizar antes del deploy.
- **Base:** `develop`.
- **Cierre:** Merge a `main` (crear **tag**) y merge de vuelta a `develop`.
- **Ejemplo:** `release/1.3.0`

#### `hotfix/*`
- **Propósito:** Corregir bugs **críticos** detectados en `main`.
- **Base:** `main`.
- **Cierre:** Merge a `main` (crear **tag PATCH**) y merge a `develop`.
- **Ejemplo:** `hotfix/fix-priority-calculation`

---

### 9.1 Convenciones para crear ramas

#### `feature/*`
**Formato:**
```
feature/[nombre-funcionalidad]-AIBERT_[codigo-jira]
```
**Ejemplos:**
- `feature/priority-algorithm-AIBERT-14`
- `feature/weekly-distribution-AIBERT-16`

**Reglas:**
- Usar **kebab-case**
- Máximo 50 caracteres
- Código de Jira obligatorio para trazabilidad

#### `release/*`
```
release/[version]
```
**Ejemplo:** `release/1.0.0`

#### `hotfix/*`
```
hotfix/[descripcion-breve-del-fix]
```
**Ejemplo:** `hotfix/corregir-calculo-score`

---

### 9.2 Convenciones para crear commits

**Formato:**
```
[codigo-jira] [tipo]: [descripción específica de la acción]
```

**Tipos de commit:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de errores
- `docs`: Cambios en documentación
- `test`: Adición o corrección de pruebas
- `refactor`: Refactorización de código

**Ejemplo:**
```
AIBERT-14 feat: implementar algoritmo de priorización con scoring ponderado
AIBERT-16 feat: generar distribución semanal respetando disponibilidad
AIBERT-14 fix: corregir cálculo de proximidad con fecha límite
```

---

## 10. 🧪 Evidencia de Pruebas Unitarias

El microservicio implementa una **estrategia integral de pruebas** con JUnit 5 y Mockito.

### Tipos de pruebas implementadas

<div align="center">

| 🧪 **Tipo de Prueba** | 📋 **Descripción** | 🛠️ **Herramientas** |
|:---------------------|:-------------------|:--------------------|
| **Pruebas Unitarias** | Validan el funcionamiento aislado de servicios, calculadoras de prioridad y estrategias | ![JUnit](https://img.shields.io/badge/JUnit_5-25A162?style=flat&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-C5D928?style=flat) |
| **Pruebas de Integración** | Verifican la interacción entre capas y el controlador REST | ![Spring Test](https://img.shields.io/badge/Spring_Test-6DB33F?style=flat&logo=spring&logoColor=white) |
| **Cobertura de Código** | Mide el porcentaje de código cubierto por las pruebas | ![JaCoCo](https://img.shields.io/badge/JaCoCo-D1322B?style=flat) |

</div>

### Cómo ejecutar las pruebas

#### 1️⃣ Ejecutar todas las pruebas

```bash
mvn clean test
```

#### 2️⃣ Generar reporte de cobertura con JaCoCo

```bash
mvn clean test jacoco:report
```

El reporte HTML se generará en:
```
target/site/jacoco/index.html
```

#### 3️⃣ Ejecutar una prueba específica

```bash
mvn test -Dtest=PlanningServiceTest
```

#### 4️⃣ Ejecutar pruebas desde IntelliJ IDEA

1. Click derecho sobre la carpeta `src/test/java`
2. Selecciona **"Run 'Tests in...'"**
3. Ver resultados en el panel inferior

---

### Ejemplo de prueba unitaria

```java
@ExtendWith(MockitoExtension.class)
class PriorityCalculatorTest {

    @InjectMocks
    private PriorityCalculator priorityCalculator;

    @Test
    @DisplayName("Should calculate HIGH priority for task due in 2 days with high academic weight")
    void calculatePriority_HighUrgency_ShouldReturnHighScore() {
        // Given
        TaskDTO task = TaskDTO.builder()
                .taskId("TASK-101")
                .taskName("Parcial Cálculo")
                .academicWeight(0.40)
                .dueDate(LocalDate.now().plusDays(2))
                .estimatedMinutes(180)
                .courseCredits(4)
                .currentGrade(3.0)
                .build();

        // When
        PriorityScore result = priorityCalculator.calculate(task);

        // Then
        assertThat(result.getScore()).isGreaterThan(0.85);
        assertThat(result.getLevel()).isEqualTo(PriorityLevel.HIGH);
    }
}
```

---

### Ejemplo de prueba de integración

```java
@WebMvcTest(PlanningController.class)
class PlanningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanningUseCases planningUseCases;

    @Test
    @DisplayName("Should return prioritized tasks list with 200 OK")
    void getPrioritization_ValidStudent_ShouldReturn200() throws Exception {
        // Given
        List<PrioritizedTaskResponse> mockResponse = List.of(
            new PrioritizedTaskResponse("TASK-101", "Parcial Cálculo", 0.92, "HIGH",
                                        "2025-08-10T23:59:00Z", 180)
        );

        when(planningUseCases.getPrioritization("STU-001")).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/planning/prioritization")
                        .param("studentId", "STU-001")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value("TASK-101"))
                .andExpect(jsonPath("$[0].priorityScore").value(0.92))
                .andExpect(jsonPath("$[0].priorityLevel").value("HIGH"));
    }
}
```

---

### Evidencias de ejecución

> 📌 *Inserta aquí las capturas de pantalla de las pruebas ejecutándose.*

**1. Consola mostrando pruebas ejecutadas exitosamente:**

<div align="center">
<img src="docs/images/console-tests.png" alt="Consola Pruebas" width="700"/>
</div>

**2. Vista del panel de pruebas en IntelliJ IDEA:**

<div align="center">
<img src="docs/images/intellij-tests.png" alt="IntelliJ Pruebas" width="700"/>
</div>

---

### Criterios de aceptación de pruebas

- ✅ **Cobertura mínima del 70%** en servicios y lógica de negocio (RNF-05)
- ✅ **Todas las pruebas en estado PASSED** (sin fallos)
- ✅ **Cero errores de compilación** en el código de pruebas
- ✅ **Pruebas de casos felices y casos de error** implementadas

---

## 11. 📈 Evidencia de Análisis de Cobertura

El análisis de cobertura se realiza con **JaCoCo** y se integra con **SonarQube** para el análisis estático de calidad.

### Reporte JaCoCo

> 📌 *Inserta aquí la captura del reporte JaCoCo generado.*

<div align="center">
<img src="docs/images/jacoco-report.png" alt="Reporte JaCoCo" width="700"/>
</div>

### Análisis SonarQube

> 📌 *Inserta aquí la captura del análisis de SonarQube.*

<div align="center">
<img src="docs/images/sonarqube-analysis.png" alt="Análisis SonarQube" width="700"/>
</div>

<div align="center">

| 📊 **Métrica** | 🎯 **Objetivo** | ✅ **Resultado** |
|:--------------|:---------------|:----------------|
| Cobertura de líneas | ≥ 70% | *(Pendiente)* |
| Cobertura de ramas | ≥ 65% | *(Pendiente)* |
| Code Smells | 0 Blocker | *(Pendiente)* |
| Vulnerabilidades | 0 Critical | *(Pendiente)* |
| Duplicación de código | < 5% | *(Pendiente)* |

</div>

---

## 12. 🗂️ Código Organizado por Carpetas

El microservicio sigue **Clean Architecture** con enfoque **Hexagonal (Ports & Adapters)**:

### Estructura general del proyecto (Scaffolding)

```
superOscholar-engineplanning-service/
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/aibert/planningengine/
│   │   │   │
│   │   │   ├── 📁 domain/                          # 🟢 CAPA DE DOMINIO
│   │   │   │   ├── 📁 model/                       # Entidades y objetos de valor
│   │   │   │   │   ├── PlanningEngine.java
│   │   │   │   │   ├── WeeklyPlan.java
│   │   │   │   │   ├── Task.java
│   │   │   │   │   └── PriorityScore.java
│   │   │   │   └── 📁 port/                        # Interfaces (Puertos)
│   │   │   │       ├── PlanningUseCases.java
│   │   │   │       └── PlanningRepositoryPort.java
│   │   │   │
│   │   │   ├── 📁 application/                     # 🔵 CAPA DE APLICACIÓN
│   │   │   │   ├── 📁 service/                     # Implementación de casos de uso
│   │   │   │   │   └── PlanningService.java
│   │   │   │   ├── 📁 mapper/                      # Mappers dominio ↔ DTO
│   │   │   │   │   └── PlanningMapper.java
│   │   │   │   └── 📁 dto/                         # Request y Response DTOs
│   │   │   │       ├── request/
│   │   │   │       └── response/
│   │   │   │
│   │   │   ├── 📁 entrypoints/                     # 🟡 CAPA DE ENTRADA
│   │   │   │   └── 📁 rest/
│   │   │   │       └── PlanningController.java      # Controlador REST
│   │   │   │
│   │   │   └── 📁 infrastructure/                  # 🟠 CAPA DE INFRAESTRUCTURA
│   │   │       ├── 📁 persistence/                 # Adaptadores de persistencia
│   │   │       │   ├── PlanningRepositoryAdapter.java
│   │   │       │   ├── JpaPlanningRepository.java
│   │   │       │   └── 📁 entity/
│   │   │       │       └── WeeklyPlanEntity.java
│   │   │       ├── 📁 client/                      # Feign Clients externos
│   │   │       │   ├── TaskServiceClient.java
│   │   │       │   └── UserServiceClient.java
│   │   │       ├── 📁 ai/                          # Clientes de IA (Gemini/Groq)
│   │   │       │   ├── GeminiClient.java
│   │   │       │   └── GroqClient.java
│   │   │       ├── 📁 security/                    # Configuración JWT
│   │   │       │   └── JwtAuthFilter.java
│   │   │       ├── 📁 config/                      # Configuraciones generales
│   │   │       │   ├── SwaggerConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       └── 📁 exception/                   # Manejo centralizado de errores
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── ApiError.java
│   │   │
│   │   └── 📁 resources/
│   │       └── application.yml                     # Configuración de la aplicación
│   │
│   └── 📁 test/                                    # 🧪 PRUEBAS
│       └── 📁 java/com/aibert/planningengine/
│           ├── 📁 domain/
│           ├── 📁 application/
│           └── 📁 entrypoints/
│
├── 📁 docs/
│   └── 📁 images/                                  # Imágenes de diagramas y evidencias
│
├── 📄 Dockerfile
├── 📄 docker-compose.yml
├── 📄 pom.xml
└── 📄 README.md
```

### Arquitectura Hexagonal Implementada

<div align="center">

| 🎨 **Capa** | 📋 **Responsabilidad** | 🔗 **Dependencias** |
|:-----------|:----------------------|:-------------------|
| **🟢 Domain** | Lógica de negocio pura, entidades y puertos (interfaces) | ❌ Ninguna (independiente) |
| **🔵 Application** | Casos de uso, servicios y DTOs | ✅ Solo `Domain` |
| **🟡 Entrypoints** | Controladores REST y documentación Swagger | ✅ `Application` + `Domain` |
| **🟠 Infrastructure** | Adaptadores de persistencia, clientes externos y configuración | ✅ `Domain` + `Application` |

</div>

**Flujo de dependencias:** `Entrypoints → Application → Domain ← Infrastructure`

### Principios de diseño aplicados

<div align="center">

| ✅ **Principio** | 📋 **Implementación** |
|:----------------|:---------------------|
| **Separación de responsabilidades** | Cada capa tiene un propósito único |
| **Inversión de dependencias** | Las capas externas dependen de interfaces del dominio |
| **Independencia del framework** | La lógica de negocio no depende de Spring o JPA |
| **Patrón Strategy** | Estrategias intercambiables de planificación |
| **Testabilidad** | Fácil mockear puertos y adaptadores en pruebas |

</div>

> ℹ️ Todo el código fuente está **documentado y comentado** para facilitar su comprensión, mantenimiento y extensión.

---

## 13. 🚀 Cómo Ejecutar el Proyecto

### 📋 Prerrequisitos

- **Java 21**
- **Maven 3.8+**
- **Docker** y **Docker Compose** (opcional)
- **PostgreSQL 15+** (si se ejecuta sin Docker)

### 🛠️ Opción 1: Ejecución Local (Maven)

```bash
# 1. Clonar el repositorio
git clone https://github.com/<org>/superOscholar-engineplanning-service.git
cd superOscholar-engineplanning-service

# 2. Configurar variables de entorno (ver sección Variables de Entorno)
cp .env.example .env

# 3. Ejecutar la aplicación
mvn spring-boot:run
```

📍 **URL Local:** `http://localhost:8004`
📚 **Swagger UI:** `http://localhost:8004/swagger-ui.html`

---

### 🐳 Opción 2: Ejecución con Docker Compose

```bash
# 1. Clonar el repositorio
git clone https://github.com/<org>/superOscholar-engineplanning-service.git
cd superOscholar-engineplanning-service

# 2. Levantar los contenedores (app + base de datos)
docker-compose up --build -d

# 3. Ver logs
docker-compose logs -f

# 4. Detener los contenedores
docker-compose down
```

📍 **URL Docker:** `http://localhost:8004`

---

### 🐳 Opción 3: Ejecutar solo con Docker

```bash
# 1. Construir la imagen
docker build -t planning-engine-service .

# 2. Ejecutar el contenedor
docker run -p 8004:8004 \
  -e DB_URL=jdbc:postgresql://host:5432/planningdb \
  -e DB_USERNAME=user \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=secret \
  planning-engine-service
```

---

## 14. ☁️ CI/CD y Despliegue en Azure

El proyecto implementa un **pipeline automatizado** con **GitHub Actions** para garantizar la calidad del código y el despliegue continuo en **Azure Cloud**.

---

### 14.1 Pipeline de Desarrollo (DEV)

Se ejecuta automáticamente en cada **Push** o **Pull Request** a la rama `develop`.

```yaml
# .github/workflows/cd_dev.yml
name: CI/CD — Development

on:
  push:
    branches: [ develop ]
  pull_request:
    branches: [ develop ]

jobs:
  build-and-test:
    name: 🧪 Build, Test & Quality
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: ☕ Set up Java 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: 🔨 Build + Test + Coverage
        run: mvn -B clean verify

      - name: 📊 SonarQube Analysis
        run: mvn sonar:sonar -Dsonar.projectKey=planning-engine

      - name: 🐳 Build Docker Image
        run: docker build -t planning-engine-dev .

      - name: 📤 Push to ACR (Dev)
        run: |
          docker tag planning-engine-dev $ACR_URL/planning-engine:dev
          docker push $ACR_URL/planning-engine:dev

      - name: 🚀 Deploy to Azure App Service (Dev)
        uses: azure/webapps-deploy@v2
        with:
          app-name: planning-engine-dev
          images: ${{ secrets.ACR_URL }}/planning-engine:dev
```

---

### 14.2 Pipeline de Producción (PROD)

Se ejecuta automáticamente en cada **Push** o **merge** a la rama `main`.

```yaml
# .github/workflows/cd_prod.yml
name: CI/CD — Production

on:
  push:
    branches: [ main ]

jobs:
  deploy-production:
    name: 🚀 Deploy to Production
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: ☕ Set up Java 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: 🔨 Build + Test + Coverage
        run: mvn -B clean verify

      - name: 🐳 Build Docker Image (Production)
        run: docker build -t planning-engine-prod .

      - name: 📤 Push to ACR (Production)
        run: |
          docker tag planning-engine-prod $ACR_URL/planning-engine:latest
          docker push $ACR_URL/planning-engine:latest

      - name: 🚀 Deploy to Azure App Service (Production)
        uses: azure/webapps-deploy@v2
        with:
          app-name: planning-engine-prod
          images: ${{ secrets.ACR_URL }}/planning-engine:latest

      - name: 🏷️ Create Release Tag
        run: |
          git tag v${{ github.run_number }}
          git push origin v${{ github.run_number }}
```

---

### 14.3 Evidencia del Despliegue

> 📌 *Inserta aquí las capturas de pantalla de los despliegues en Azure.*

<div align="center">
  <img src="docs/images/azure-dev-deploy.png" alt="Azure Dev Deploy" width="45%" />
  <img src="docs/images/azure-prod-deploy.png" alt="Azure Prod Deploy" width="45%" />
</div>

### Infraestructura Azure

<div align="center">

| Componente | Servicio Azure | Propósito |
|:-----------|:---------------|:----------|
| **Compute** | ![App Service](https://img.shields.io/badge/App_Service-0078D4?logo=microsoft-azure&logoColor=white) | Ejecución del contenedor Docker del microservicio |
| **Registry** | ![ACR](https://img.shields.io/badge/ACR-0078D4?logo=docker&logoColor=white) | Almacenamiento privado de imágenes Docker |
| **Database** | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Azure-4169E1?logo=postgresql&logoColor=white) | Persistencia de datos de planificación |
| **Monitoring** | ![Insights](https://img.shields.io/badge/App_Insights-5C2D91?logo=microsoft-azure&logoColor=white) | Logs, métricas y trazabilidad en tiempo real |

</div>

---

### 14.4 Link Swagger en Azure

<div align="center">

| 🌍 Ambiente | 🔗 URL Swagger | 📝 Estado |
|:-----------|:--------------|:---------|
| **🟢 Producción** | [planning-engine-prod.azurewebsites.net/swagger-ui/index.html](#) | ![Pendiente](https://img.shields.io/badge/Status-Pending-yellow?style=flat) |
| **🟠 Desarrollo** | [planning-engine-dev.azurewebsites.net/swagger-ui/index.html](#) | ![Pendiente](https://img.shields.io/badge/Status-Pending-yellow?style=flat) |

</div>

> 📌 *Reemplaza los links `#` por las URLs reales de Azure una vez desplegado el servicio.*

---

## 15. 🔐 Variables de Entorno

```bash
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/planningdb
DB_USERNAME=planninguser
DB_PASSWORD=planningpassword

# Seguridad
JWT_SECRET=your_jwt_secret_key_here

# Feign Clients — Servicios externos
FEIGN_TASK_SERVICE_URL=http://task-service:8001
FEIGN_PROFILE_SERVICE_URL=http://user-service:8002
FEIGN_ACADEMIC_SERVICE_URL=http://academic-service:8003

# Inteligencia Artificial
GEMINI_API_KEY=your_gemini_api_key_here
GROQ_API_KEY=your_groq_api_key_here

# Servidor
SERVER_PORT=8004
SPRING_PROFILES_ACTIVE=dev
```

### ⚙️ Propiedades del Motor de Priorización (application.yml)

```yaml
planning:
  priority:
    weight-proximity: 0.40            # Peso del factor proximidad al deadline
    weight-academic: 0.40             # Peso del factor peso académico de la materia
    weight-time: 0.20                 # Peso del factor tiempo estimado
    time-correction-factor: 1.0       # AIB-22.4 — Factor de corrección de duración (valor a definir)
```

> ⚠️ **Nunca subas el archivo `.env` al repositorio.** Usa `.env.example` como plantilla y agrega `.env` a tu `.gitignore`.

---

## 16. 📚 Referencias

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security + JWT](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [OpenFeign Client](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [SonarQube](https://docs.sonarqube.org/)
- [Docker Documentation](https://docs.docker.com/)
- [Azure App Service](https://docs.microsoft.com/en-us/azure/app-service/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Gemini API](https://ai.google.dev/docs)
- [Groq API](https://console.groq.com/docs)

---

<div align="center">

### 🏆 Módulo 4 — Motor de Planificación Inteligente

![Module](https://img.shields.io/badge/Module-4-blueviolet?style=for-the-badge)
![Project](https://img.shields.io/badge/Project-A.IBERT_ECI_Planner-orange?style=for-the-badge)
![Year](https://img.shields.io/badge/Year-2025-blue?style=for-the-badge)

> 💡 **A.IBERT — ECI Planner** es un sistema académico inteligente diseñado para optimizar
> el rendimiento estudiantil mediante planificación automatizada e inteligencia artificial.

**🎓 Escuela Colombiana de Ingeniería Julio Garavito**

</div>
```