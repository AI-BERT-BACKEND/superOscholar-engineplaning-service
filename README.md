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
 └── user-service        → Obtiene el perfil y disponibilidad del estudiante
```

<div align="center">

| 🌍 **Microservicio** | ⚙️ **Operación** | 📋 **Propósito** |
|:---------------------|:----------------|:-----------------|
| **task-service** | GET tareas del estudiante | Obtener lista de tareas pendientes para planificar |
| **user-service** | GET perfil del estudiante | Obtener disponibilidad horaria y carga académica |

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

El motor calcula un **score de prioridad** para cada tarea académica con base en cuatro factores ponderados:

```
score =
  (nota_actual    × 0.35) +   ← Peso académico del estudiante
  (proximidad     × 0.35) +   ← Cercanía a la fecha límite
  (peso_tarea     × 0.20) +   ← Peso de la tarea en la materia
  (creditos       × 0.10)     ← Créditos de la asignatura
```

| Factor | Peso | Descripción |
|--------|------|-------------|
| `nota_actual` | 35% | Nota actual del estudiante en la materia |
| `proximidad` | 35% | Qué tan cerca está la fecha de entrega |
| `peso_tarea` | 20% | Porcentaje que representa la tarea en la nota final |
| `creditos` | 10% | Créditos de la asignatura |

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

### 6.1 R14 — Priorización de Tareas

Calcula automáticamente la prioridad de las tareas académicas del estudiante aplicando el algoritmo de scoring ponderado.

**Endpoint:**
`GET /planning/prioritization`

---

#### 📦 Información de Entrada (Request)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | ⚠️ Restricciones | 📝 Descripción |
|---|---|:---:|---|
| `studentId` | `String` | Obligatorio (Query Param) | Identificador del estudiante. |
| `Authorization` | `String` | Obligatorio (Header) | Token JWT Bearer. |

</div>

---

#### 📦 Información de Salida (Response)

<div align="center">

| 🏷️ Campo | 🗃️ Tipo | 📝 Descripción |
|---|---|---|
| `taskId` | `String` | Identificador único de la tarea. |
| `taskName` | `String` | Nombre de la tarea académica. |
| `priorityScore` | `Double` | Puntuación calculada (0.0 - 1.0). |
| `priorityLevel` | `Enum` | Nivel: `HIGH`, `MEDIUM`, `LOW`. |
| `dueDate` | `String` | Fecha límite de la tarea (ISO 8601). |
| `estimatedTime` | `Integer` | Tiempo estimado en minutos. |

</div>

---

#### ✅ Happy Path (Ejemplo de Uso Exitoso)

**Request:**
```http
GET /planning/prioritization?studentId=STU-001
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`:**
```json
[
  {
    "taskId": "TASK-101",
    "taskName": "Parcial de Cálculo Diferencial",
    "priorityScore": 0.92,
    "priorityLevel": "HIGH",
    "dueDate": "2025-08-10T23:59:00Z",
    "estimatedTime": 180
  },
  {
    "taskId": "TASK-102",
    "taskName": "Taller de Programación",
    "priorityScore": 0.65,
    "priorityLevel": "MEDIUM",
    "dueDate": "2025-08-14T23:59:00Z",
    "estimatedTime": 120
  }
]
```

---

#### 📊 Tipos de Errores Manejados

<div align="center">

| 🔢 **Código HTTP** | ⚠️ **Escenario** | 💬 **Mensaje de Error** |
|:------------------:|:----------------|:------------------------|
| ![400](https://img.shields.io/badge/400-Bad_Request-red?style=flat) | studentId vacío | `"Student ID cannot be null or empty"` |
| ![401](https://img.shields.io/badge/401-Unauthorized-orange?style=flat) | Token inválido o ausente | `"Invalid or missing JWT token"` |
| ![404](https://img.shields.io/badge/404-Not_Found-orange?style=flat) | Estudiante no encontrado | `"No tasks found for student"` |
| ![500](https://img.shields.io/badge/500-Internal_Error-critical?style=flat) | Error en algoritmo | `"Error calculating priority score"` |

</div>

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

# Inteligencia Artificial
GEMINI_API_KEY=your_gemini_api_key_here
GROQ_API_KEY=your_groq_api_key_here

# Servidor
SERVER_PORT=8004
SPRING_PROFILES_ACTIVE=dev
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