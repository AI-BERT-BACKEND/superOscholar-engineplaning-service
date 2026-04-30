# README — superOscholar-engineplanning-service

```markdown
# superOscholar-engineplanning-service

> Microservicio del Motor de Planificación Inteligente de A.IBERT — ECI Planner.
> Analiza, prioriza y distribuye automáticamente las tareas académicas del estudiante
> optimizando su tiempo disponible para mejorar el rendimiento y evitar el estrés.

---

## Tabla de Contenido

- [Equipo](#equipo)
- [Descripción del Módulo](#descripción-del-módulo)
- [Microservicios del Módulo 4](#microservicios-del-módulo-4)
- [Requerimientos Funcionales](#requerimientos-funcionales)
- [Requerimientos No Funcionales](#requerimientos-no-funcionales)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Endpoints REST](#endpoints-rest)
- [Diagramas](#diagramas)
- [Gestión del Proyecto](#gestión-del-proyecto)
- [Pruebas y Calidad](#pruebas-y-calidad)
- [Demo](#demo)
- [Variables de Entorno](#variables-de-entorno)
- [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
- [Referencias](#referencias)

---

## Equipo

**Módulo 4 — Motor de Planificación Inteligente**
**Proyecto:** A.IBERT — ECI Planner
**Institución:** Escuela Colombiana de Ingeniería Julio Garavito

| Integrante                      |
|---------------------------------|
| Juan Esteban Sánchez García     |
| Juan Carlos Bohórquez Monroy    |
| Jeyder Nicolay Leon Lancheros   |

---

## Descripción del Módulo

El **Módulo 4 — Motor de Planificación Inteligente** es el núcleo inteligente de
A.IBERT. No es solo una lista de tareas: es un sistema que **analiza, prioriza y
optimiza** cómo el estudiante usa su tiempo académico.

Los estudiantes universitarios no fallan por falta de capacidad, sino por mala
gestión del tiempo. Este módulo ataca directamente ese problema:

- Subestiman el tiempo real que requieren las tareas
- Priorizan mal (trabajan en lo urgente, no en lo importante)
- Sobrecargan días específicos y dejan otros vacíos
- Toman decisiones reactivas, no estratégicas

**El resultado sin este módulo:** estrés acumulado, bajo rendimiento,
pérdida de materias y burnout estudiantil.

---

## Microservicios del Módulo 4

El Módulo 4 está compuesto por **dos microservicios independientes**:

| # | Microservicio | Puerto | Responsabilidad | Requerimientos |
|---|---------------|--------|-----------------|----------------|
| 1 | `planning-engine-service` | :8004 | Motor de cálculo, priorización, balance y distribución de tareas | R14, R15, R16, R17 |
| 2 | `recommendation-service` | :8005 | Sistema de recomendaciones con IA (Gemini + Groq) | R18, R19 |

> 📌 **Este repositorio** corresponde al microservicio `planning-engine-service`.
> El `recommendation-service` tiene su propio repositorio independiente.

### Comunicación entre microservicios

```
planning-engine-service ←── (Feign Client) ──── recommendation-service
        │                                                  │
        │                                          Gemini 2.5 Flash API
        │                                          Groq llama-3.1-8b
        │
        ├── task-service      (consume tareas del estudiante)
        └── user-service      (consume perfil y disponibilidad)
```

---

## Requerimientos Funcionales

### R14 — Motor de Priorización

| Campo | Detalle |
|-------|---------|
| **Código** | R14 |
| **Nombre** | Motor de priorización |
| **Actor** | Estudiante |
| **Precondición** | Sesión activa y al menos una tarea registrada |

**Descripción:** El sistema calcula automáticamente la prioridad de cada tarea
tomando como base el peso académico de la materia, la proximidad de la fecha
de entrega y el tiempo estimado de ejecución.

**Datos de Entrada:**

| Campo | Tipo | Regla | Obligatorio |
|-------|------|-------|-------------|
| taskId | String | Tarea existente del estudiante | Sí |
| academicWeight | Float | Entre 0.0 y 5.0 (nota final de la materia) | Sí |
| deadline | Date | Debe ser fecha futura | Sí |
| estimatedTime | Float | Mayor a 0, máximo 72 horas | Sí |

**Datos de Salida:**

| Campo | Tipo | Regla |
|-------|------|-------|
| priorityScore | Float | Entre 0.0 y 100.0 |
| priorityLevel | String | ALTA ≥70 / MEDIA 40-69 / BAJA <40 |
| orderedTasks | Array | Tareas activas ordenadas por score desc |
| message | String | Confirmación o aviso de sin tareas |

**Reglas de Negocio:**

| ID | Descripción |
|----|-------------|
| RN-01 | La prioridad se recalcula automáticamente al registrar, modificar o completar una tarea |
| RN-02 | Una tarea con deadline en menos de 24 horas escala automáticamente a prioridad ALTA |
| RN-03 | El peso académico corresponde al porcentaje de la materia en la carga total del semestre |

**Flujos Alternos:**

| ID | Escenario | Respuesta del sistema |
|----|-----------|----------------------|
| FA-01 | Sin tareas activas | "No hay tareas activas para priorizar" |
| FA-02 | Tareas sin deadline | Prioridad BAJA por defecto |

---

### R15 — Balanceador de Tiempo (Diferencial)

| Campo | Detalle |
|-------|---------|
| **Código** | R15 |
| **Nombre** | Balanceador de tiempo |
| **Actor** | Estudiante |
| **Precondición** | Sesión activa, tareas registradas y disponibilidad horaria configurada |

**Descripción:** Analiza la distribución del tiempo en la semana, detecta días
con sobrecarga y días vacíos, y sugiere una redistribución más equilibrada.

**Datos de Entrada:**

| Campo | Tipo | Regla | Obligatorio |
|-------|------|-------|-------------|
| weekStartDate | Date | Lunes como inicio de semana | Sí |
| studentId | String | Estudiante registrado con sesión activa | Sí |
| dailyAvailability | Object | 7 días con horas disponibles (0-24) | Sí |

**Datos de Salida:**

| Campo | Tipo | Regla |
|-------|------|-------|
| weeklyLoadAnalysis | Object | Horas asignadas, disponibles y % ocupación por día |
| overloadedDays | Array | Días donde la carga supera el 80% de disponibilidad |
| emptyDays | Array | Días donde la carga es menor al 20% |
| balanceSuggestion | Object | Qué tareas mover y hacia qué días |
| message | String | Estado del balance semanal |

**Reglas de Negocio:**

| ID | Descripción |
|----|-------------|
| RN-01 | Día sobrecargado: horas de tareas > 80% de disponibilidad declarada |
| RN-02 | Día vacío: horas de tareas < 20% de disponibilidad declarada |
| RN-03 | El estudiante tiene la decisión final sobre aceptar o rechazar sugerencias |

**Flujos Alternos:**

| ID | Escenario | Respuesta del sistema |
|----|-----------|----------------------|
| FA-01 | Sin disponibilidad configurada | "Configura tu disponibilidad diaria para activar el balanceador" |
| FA-02 | Sin tareas en la semana | "No hay tareas registradas para esta semana" |

---

### R16 — Distribución Automática de Tareas

| Campo | Detalle |
|-------|---------|
| **Código** | R16 |
| **Nombre** | Distribución automática de tareas |
| **Actor** | Estudiante |
| **Precondición** | Sesión activa, tareas pendientes, disponibilidad configurada y R14 calculado |

**Descripción:** Asigna automáticamente las tareas del estudiante con base en
su disponibilidad horaria, la prioridad de cada tarea y la carga acumulada
semanal, generando un plan de trabajo diario optimizado.

**Datos de Entrada:**

| Campo | Tipo | Regla | Obligatorio |
|-------|------|-------|-------------|
| studentId | String | Estudiante registrado con sesión activa | Sí |
| weekStartDate | Date | Por defecto lunes de la semana actual | Sí |
| pendingTasks | Array | Solo tareas en estado PENDIENTE o EN PROGRESO | Sí |
| dailyAvailability | Object | 7 días con horas disponibles | Sí |

**Datos de Salida:**

| Campo | Tipo | Regla |
|-------|------|-------|
| weeklyPlan | Object | Plan semanal respetando disponibilidad |
| assignedBlocks | Array | taskId, día asignado, hora inicio, duración |
| unassignedTasks | Array | Vacío si todas fueron asignadas |
| message | String | Confirmación o aviso de tareas sin asignar |

**Reglas de Negocio:**

| ID | Descripción |
|----|-------------|
| RN-01 | Las tareas ALTA siempre se asignan antes que MEDIA y BAJA |
| RN-02 | No se asignan tareas en bloques marcados como no disponibles |
| RN-03 | Si no hay disponibilidad suficiente, se asignan las de mayor prioridad y se notifican las restantes |

**Flujos Alternos:**

| ID | Escenario | Respuesta del sistema |
|----|-----------|----------------------|
| FA-01 | Sin disponibilidad suficiente | Asigna las de mayor prioridad. "Hay tareas que no pudieron asignarse" |
| FA-02 | Sin tareas pendientes | "No tienes tareas pendientes para distribuir" |

---

### R17 — Rebalanceo Dinámico de Tareas

| Campo | Detalle |
|-------|---------|
| **Código** | R17 |
| **Nombre** | Rebalanceo dinámico de tareas |
| **Actor** | Estudiante |
| **Precondición** | Sesión activa, plan generado y al menos una tarea no completada |

**Descripción:** Cuando el estudiante no completa una tarea en el tiempo
asignado, el sistema reorganiza automáticamente el plan de trabajo sin
sobrecargar la semana.

**Datos de Entrada:**

| Campo | Tipo | Regla | Obligatorio |
|-------|------|-------|-------------|
| taskId | String | Tarea con bloque de trabajo asignado | Sí |
| studentId | String | Estudiante registrado con sesión activa | Sí |
| remainingTime | Float | Horas disponibles distribuidas en 7 días | Sí |

**Datos de Salida:**

| Campo | Tipo | Regla |
|-------|------|-------|
| updatedPlan | Object | Plan reorganizado respetando disponibilidad restante |
| movedTasks | Array | taskId, bloque original y nuevo bloque |
| criticalTasks | Array | Tareas con deadline < 24h sin tiempo disponible |
| message | String | Confirmación o alerta de tareas críticas |

**Reglas de Negocio:**

| ID | Descripción |
|----|-------------|
| RN-01 | El sistema nunca elimina tareas del plan, solo las reorganiza |
| RN-02 | Tareas con deadline < 24h se marcan como CRÍTICAS con notificación inmediata |
| RN-03 | El rebalanceo respeta los bloques de tiempo personal y descanso configurados |

**Flujos Alternos:**

| ID | Escenario | Respuesta del sistema |
|----|-----------|----------------------|
| FA-01 | Sin tiempo disponible para reorganizar | "No hay tiempo disponible. Te recomendamos revisar tus prioridades" |
| FA-02 | Todas las tareas en estado crítico | Lista de tareas críticas con alerta |

---

## Requerimientos No Funcionales

| ID | Requerimiento | Métrica |
|----|---------------|---------|
| RNF-01 | Seguridad | Todos los endpoints protegidos con JWT |
| RNF-02 | Rendimiento | Respuesta < 2s. Motor de priorización < 1s |
| RNF-03 | Disponibilidad | Mínimo 95% en ambiente de producción |
| RNF-04 | Escalabilidad | Cada microservicio escala independientemente |
| RNF-05 | Mantenibilidad | Cobertura mínima 70% JaCoCo, calificativo B SonarQube |
| RNF-06 | Usabilidad | API REST con respuestas claras y orientadas al usuario |

---

## Arquitectura

### Tipo de Arquitectura

- **Microservicios** — cada módulo es independiente y desplegable por separado
- **Clean Architecture** — separación estricta en capas: domain, application, infrastructure, entrypoints
- **API REST** — comunicación estándar entre servicios y con el frontend
- **Hexagonal (Ports & Adapters)** — el dominio no depende de frameworks externos

### Capas de Clean Architecture

```
┌─────────────────────────────────────────────────┐
│  entrypoints/     → Controllers REST + JWT      │
│  ─────────────────────────────────────────────  │
│  application/     → Casos de uso + DTOs         │
│  ─────────────────────────────────────────────  │
│  domain/          → Modelos + Puertos (sin FW)  │
│  ─────────────────────────────────────────────  │
│  infrastructure/  → JPA + Feign + IA Adapters   │
└─────────────────────────────────────────────────┘
```

### Dependencias entre capas

```
entrypoints → application → domain ← infrastructure
```

### Algoritmo de Priorización (R14)

```
score = (factorNota × 0.35)
      + (factorProximidad × 0.35)
      + (factorPeso × 0.20)
      + (factorCreditos × 0.10)

factorNota:       basado en riesgo académico de la materia (0.3 - 1.0)
factorProximidad: basado en días restantes al deadline (0.1 - 1.0)
factorPeso:       peso de la tarea en la nota / 100
factorCreditos:   créditos materia / total créditos semestre
```

### Formato estándar de respuesta JSON

```json
// Respuesta exitosa
{
  "success": true,
  "data": { ... },
  "message": "ok"
}

// Respuesta de error
{
  "success": false,
  "error": "Mensaje de error",
  "code": 400
}
```

---

## Stack Tecnológico

| Área | Tecnología |
|------|------------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2 |
| Build | Maven |
| Arquitectura | Clean Architecture + Microservicios |
| API | REST + OpenAPI/Swagger |
| Seguridad | Spring Security + JWT (JJWT 0.12.3) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | PostgreSQL |
| Migraciones | Flyway |
| Comunicación MS | OpenFeign |
| IA Principal | Google Gemini 2.5 Flash API |
| IA Fallback | Groq API (llama-3.1-8b-instant) |
| Resiliencia | Resilience4j (Circuit Breaker) |
| Mappers | MapStruct |
| Utilidades | Lombok |
| Testing | JUnit 5 + Mockito |
| Cobertura | JaCoCo |
| Calidad | SonarQube |
| Contenedores | Docker + Docker Compose |

---

## Estructura del Proyecto

```
superOscholar-engineplanning-service/
│
├── com.aibert.dosw/
│   │
│   ├── config/
│   │   ├── AiProviderConfig.java         → Selecciona Gemini o Groq
│   │   ├── FeignConfig.java              → Interceptor JWT para Feign
│   │   ├── JwtAuthenticationFilter.java  → Filtro de seguridad
│   │   ├── JwtTokenProvider.java         → Validación de tokens JWT
│   │   ├── PersistenceConfig.java        → Configuración JPA/PostgreSQL
│   │   ├── RestTemplateConfig.java       → Timeouts para llamadas a IA
│   │   └── SecurityConfig.java           → Spring Security stateless
│   │
│   ├── domain/
│   │   ├── exceptions/
│   │   │   ├── NoAvailabilityException.java
│   │   │   ├── OverloadException.java
│   │   │   ├── PlanningDomainException.java
│   │   │   └── TaskNotPlannableException.java
│   │   │
│   │   ├── model/
│   │   │   ├── balance/
│   │   │   │   ├── BalanceStatus.java         → enum: OVERLOADED, BALANCED, FREE
│   │   │   │   ├── DifferentialBalance.java   → Balance diferencial por día
│   │   │   │   └── WorkloadBalance.java       → Balance semanal completo
│   │   │   ├── context/
│   │   │   │   ├── NoteRiskCalculator.java    → Calcula nota necesaria corte 3
│   │   │   │   ├── PlanningContext.java       → Contexto completo del estudiante
│   │   │   │   ├── RebalanceResult.java       → Resultado del rebalanceo
│   │   │   │   ├── TaskReassignment.java      → Reasignación de tarea
│   │   │   │   └── UserProfile.java           → Perfil básico del usuario
│   │   │   ├── recommendation/
│   │   │   │   ├── Recommendation.java
│   │   │   │   ├── RecommendationType.java    → enum: STUDY_TODAY, RESCHEDULE, ALERT, REST
│   │   │   │   └── StudySuggestion.java
│   │   │   ├── schedule/
│   │   │   │   ├── AvailabilityBlock.java
│   │   │   │   ├── DailySchedule.java
│   │   │   │   ├── TimeSlot.java
│   │   │   │   └── WeeklyDistribution.java
│   │   │   └── task/
│   │   │       ├── PlanningTask.java          → Entidad principal de dominio
│   │   │       ├── TaskPriority.java          → enum: CRITICAL, HIGH, MEDIUM, LOW
│   │   │       └── TaskStatus.java            → enum: PENDING, SCHEDULED, IN_PROGRESS, OVERLOADED, COMPLETED
│   │   │
│   │   ├── ports/
│   │   │   ├── in/
│   │   │   │   ├── BalanceWorkloadUseCase.java       → R15
│   │   │   │   ├── DistributeTasksUseCase.java       → R16
│   │   │   │   ├── GetRecommendationsUseCase.java    → R18
│   │   │   │   ├── GetStudySuggestionsUseCase.java   → R19
│   │   │   │   ├── PrioritizeTasksUseCase.java       → R14
│   │   │   │   └── RebalanceTasksUseCase.java        → R17
│   │   │   └── out/
│   │   │       ├── AiProviderPort.java
│   │   │       ├── PlanningTaskRepositoryPort.java
│   │   │       ├── RecommendationRepositoryPort.java
│   │   │       ├── ScheduleRepositoryPort.java
│   │   │       └── UserProfileClientPort.java
│   │   │
│   │   └── valueobjects/
│   │       ├── AcademicWeight.java   → Peso académico (inmutable, validado)
│   │       ├── PriorityScore.java    → Score calculado (inmutable)
│   │       ├── TaskId.java           → ID de tarea (type-safe)
│   │       └── UserId.java           → ID de usuario (type-safe)
│   │
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── BalanceRequest.java
│   │   │   │   ├── DistributeTasksRequest.java
│   │   │   │   ├── PrioritizeRequest.java
│   │   │   │   ├── RebalanceRequest.java
│   │   │   │   └── RecommendationRequest.java
│   │   │   └── response/
│   │   │       ├── DayBalanceResponse.java
│   │   │       ├── DistributionPlanResponse.java
│   │   │       ├── PrioritizedTaskResponse.java
│   │   │       ├── RebalanceResultResponse.java
│   │   │       ├── RecommendationResponse.java
│   │   │       ├── StudySuggestionResponse.java
│   │   │       └── WorkloadBalanceResponse.java
│   │   ├── mapper/
│   │   │   ├── PlanningTaskMapper.java      → Domain ↔ DTO (MapStruct)
│   │   │   ├── RecommendationMapper.java
│   │   │   └── ScheduleMapper.java
│   │   ├── service/
│   │   │   ├── AiPlanningOrchestrator.java       → Orquesta lógica Java + IA
│   │   │   └── PlanningApplicationService.java   → Punto de entrada desde controllers
│   │   └── usecase/
│   │       ├── BalanceWorkloadUseCaseImpl.java
│   │       ├── DistributeTasksUseCaseImpl.java
│   │       ├── GetRecommendationsUseCaseImpl.java
│   │       ├── GetStudySuggestionsUseCaseImpl.java
│   │       ├── PrioritizeTasksUseCaseImpl.java
│   │       └── RebalanceTasksUseCaseImpl.java
│   │
│   ├── entrypoints/
│   │   ├── advice/
│   │   │   └── PlanningExceptionHandler.java   → @RestControllerAdvice global
│   │   └── rest/
│   │       ├── controller/
│   │       │   ├── BalanceController.java          → GET /api/v1/planning/balance
│   │       │   ├── DistributionController.java     → POST /api/v1/planning/distribution
│   │       │   ├── PrioritizationController.java   → GET /api/v1/planning/prioritization
│   │       │   ├── RebalanceController.java        → POST /api/v1/planning/rebalance
│   │       │   └── RecommendationController.java   → GET /api/v1/planning/recommendations
│   │       ├── mapper/
│   │       │   ├── PlanningRequestMapper.java    → HTTP params → DTO
│   │       │   └── PlanningResponseMapper.java   → DTO → ApiResponse<T>
│   │       └── response/
│   │           └── ApiResponse.java              → Wrapper genérico de respuestas
│   │
│   └── infrastructure/
│       ├── adapters/
│       │   ├── adapter/
│       │   │   ├── PlanningTaskRepositoryAdapter.java
│       │   │   ├── RecommendationRepositoryAdapter.java
│       │   │   ├── ScheduleRepositoryAdapter.java
│       │   │   └── UserProfileClientAdapter.java
│       │   └── persistence/
│       │       ├── entity/
│       │       │   ├── PlanningTaskEntity.java
│       │       │   ├── RecommendationEntity.java
│       │       │   └── ScheduleEntity.java
│       │       ├── mapper/
│       │       │   ├── PlanningTaskEntityMapper.java
│       │       │   ├── RecommendationEntityMapper.java
│       │       │   └── ScheduleEntityMapper.java
│       │       └── repository/
│       │           ├── SpringDataPlanningTaskRepository.java
│       │           ├── SpringDataRecommendationRepository.java
│       │           └── SpringDataScheduleRepository.java
│       └── external/
│           ├── ai/
│           │   ├── gemini/
│           │   │   ├── GeminiAdapter.java           → Implementa AiProviderPort
│           │   │   ├── GeminiContent.java
│           │   │   ├── GeminiGenerationConfig.java
│           │   │   ├── GeminiPart.java
│           │   │   ├── GeminiRequest.java
│           │   │   └── GeminiResponse.java
│           │   └── groq/
│           │       ├── GroqAdapter.java             → Implementa AiProviderPort (fallback)
│           │       ├── GroqChoice.java
│           │       ├── GroqMessage.java
│           │       ├── GroqRequest.java
│           │       └── GroqResponse.java
│           └── feign/
│               ├── client/
│               │   ├── TaskServiceClient.java       → Consume task-service
│               │   └── UserServiceClient.java       → Consume user-service
│               └── dto/
│                   ├── ExternalAvailabilityDto.java
│                   ├── ExternalTaskDto.java
│                   └── ExternalUserProfileDto.java
│
└── resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    ├── db/migration/
    │   └── V1__create_planning_tables.sql
    └── prompts/
        └── planning_prompt_template.txt
```

---

## Endpoints REST

| Método | Endpoint | Descripción | Req |
|--------|----------|-------------|-----|
| GET | `/api/v1/planning/prioritization/{userId}` | Priorizar tareas del usuario | R14 |
| GET | `/api/v1/planning/balance/{userId}/weekly` | Balance semanal de carga | R15 |
| GET | `/api/v1/planning/balance/{userId}/daily` | Balance del día actual | R15 |
| POST | `/api/v1/planning/distribution/{userId}/auto` | Distribución automática | R16 |
| GET | `/api/v1/planning/distribution/{userId}/current-plan` | Plan actual | R16 |
| POST | `/api/v1/planning/rebalance/{userId}` | Rebalanceo dinámico | R17 |
| GET | `/api/v1/planning/recommendations/{userId}` | Recomendaciones | R18 |
| GET | `/api/v1/planning/recommendations/{userId}/today` | Sugerencias del día | R19 |
| PATCH | `/api/v1/planning/recommendations/{userId}/{id}/read` | Marcar como leída | R18 |

> 🔐 Todos los endpoints requieren header: `Authorization: Bearer {JWT}`

---

## Diagramas

### Contexto
- [Diagrama de contexto](docs/diagramas/contexto.png)

### Casos de Uso
- [Diagrama de casos de uso](docs/diagramas/casos-uso.png)

### Diagrama de Clases
- [Diagrama de clases del dominio](docs/diagramas/clases.png)

### Componentes General
- [Diagrama de componentes general](docs/diagramas/componentes-general.png)

### Componentes Específico
- [Diagrama de componentes específico](docs/diagramas/componentes-especifico.png)

### Entidad Relación
- [ER Diagram — planning_tasks, daily_schedules, recommendations](docs/diagramas/er.png)

### Secuencia
- [01 — Priorización de tareas](docs/secuencia/priorizacion.md)
- [02 — Balance semanal](docs/secuencia/balance.md)
- [03 — Distribución automática](docs/secuencia/distribucion.md)
- [04 — Rebalanceo dinámico](docs/secuencia/rebalanceo.md)

---

## Gestión del Proyecto

### Metodología

- **Scrum** con sprints semanales
- Dailies de 15 minutos con: ¿qué hice?, ¿qué haré?, ¿qué bloqueos tengo?
- Gestión de tareas en **JIRA**
- Estimación con **Planning Poker** (puntos Fibonacci)
- Canal de comunicación: **Slack**

### Épicas en JIRA

| Épica | Descripción |
|-------|-------------|
| EPIC-BE | Backend — microservicios, controladores, servicios, repositorios |
| EPIC-FE | Frontend — componentes React y navegación |
| EPIC-ARCH | Arquitectura — diagramas de clases y componentes |
| EPIC-NT | Módulo — refinamiento de artefactos y análisis de requerimientos |

---

### Sprint 1 — 24 abril al 1 mayo de 2026

---

#### Objetivo del Sprint 1

Construir la base del sistema A.IBERT estableciendo la estructura de cada
microservicio. Para el Módulo 4 específicamente: estructura Maven lista y
modelo de disponibilidad del estudiante definido.

#### Criterio de Éxito — Módulo 4

- Estructura Maven del proyecto configurada y compilando
- Modelos de dominio (entidades JPA) definidos
- Modelo de disponibilidad del estudiante implementado
- Scaffolding de Clean Architecture establecido

#### Actividades del Sprint 1

| Actividad | Descripción | Estado |
|-----------|-------------|--------|
| 5.1 Deuda técnica | Revisión de requerimientos y artefactos con feedback | 🚧 |
| 5.2 Backlog JIRA | Creación de épicas, historias y subtareas | 🚧 |
| 5.3 Set de pruebas | Definición de casos happy path, error y condicionales | 🚧 |
| 5.4 Sprint Backlog | Construcción, estimación y asignación de tareas | 🚧 |
| 5.5 Diagramas | Clases, componentes general y específico | 🚧 |
| 5.6 Implementación | Estructura Maven, flujo controlador-servicio, pruebas unitarias | 🚧 |

#### Distribución de Tareas Sprint 1 — Módulo 4

| Tarea | Responsable | Puntos | Estado |
|-------|-------------|--------|--------|
| Estructura Maven + pom.xml | Por asignar | — | 🚧 |
| Modelos de dominio (task, balance, schedule) | Por asignar | — | 🚧 |
| Entidades JPA y migración Flyway | Por asignar | — | 🚧 |
| Puertos de entrada y salida (interfaces) | Por asignar | — | 🚧 |
| Configuración JWT + Security | Por asignar | — | 🚧 |
| Diagrama de clases del dominio | Por asignar | — | 🚧 |
| Diagrama de componentes | Por asignar | — | 🚧 |

#### Entregables Sprint 1

| Entregable | Formato | Estado |
|------------|---------|--------|
| Link repositorio Backend | GitHub URL | 🚧 |
| Documento Análisis de Requerimientos | .pdf | ✅ |
| Presentación Sprint Review + Retro | Canva / PowerPoint | 🚧 |

---

> 📌 **Nota:** Esta sección se actualizará al finalizar cada sprint con métricas,
> retrospectiva y entregables completados. Los sprints siguientes se agregarán
> debajo con el mismo separador.

---

## Pruebas y Calidad

### Estrategia de Pruebas

- **Unitarias** — JUnit 5 + Mockito por cada caso de uso y clase de dominio
- **Integración** — Pruebas de controllers con @WebMvcTest
- **Cobertura mínima** — 70% medido con JaCoCo (RNF-05)

### Clases de Prueba

| Clase | Tipo | Cubre |
|-------|------|-------|
| PlanningTaskTest | Unitaria | Dominio — R14 |
| PriorityScoreTest | Unitaria | Value Object — R14 |
| DifferentialBalanceTest | Unitaria | Dominio — R15 |
| NoteRiskCalculatorTest | Unitaria | Dominio — R14 |
| PrioritizeTasksUseCaseImplTest | Unitaria | Caso de uso — R14 |
| BalanceWorkloadUseCaseImplTest | Unitaria | Caso de uso — R15 |
| DistributeTasksUseCaseImplTest | Unitaria | Caso de uso — R16 |
| RebalanceTasksUseCaseImplTest | Unitaria | Caso de uso — R17 |
| GetRecommendationsUseCaseImplTest | Unitaria | Caso de uso — R18 |
| GetStudySuggestionsUseCaseImplTest | Unitaria | Caso de uso — R19 |
| PlanningApplicationServiceTest | Unitaria | Service |
| GeminiAdapterTest | Unitaria | Infraestructura |
| GroqAdapterTest | Unitaria | Infraestructura |
| PrioritizationControllerTest | Integración | Entrypoint — R14 |
| RecommendationControllerTest | Integración | Entrypoint — R18/R19 |

### Reporte de Cobertura

> *Pendiente de actualizar al completar implementación*

Reporte generado con **JaCoCo** y analizado con **SonarQube**

| Métrica | Cubierto | Total | Cobertura |
|---------|----------|-------|-----------|
| Líneas | — | — | — |
| Ramas | — | — | — |
| Métodos | — | — | — |

### Calidad de Código

> *Pendiente de actualizar al completar implementación*

- Bugs críticos: —
- Code Smells: —
- Deuda técnica: —
- Quality Gate: —

---

## Demo

### Video Demo

- [Demo Módulo 4 — Motor de Planificación](link-demo)

### Capturas de Pantalla

> *Se agregarán al completar el frontend*

---

## Variables de Entorno

```bash
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/planning_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT (mismo secret que auth-service)
JWT_SECRET=tu-secret-aqui

# IA Principal
GEMINI_API_KEY=tu-api-key-de-aistudio.google.com

# IA Fallback
GROQ_API_KEY=tu-api-key-de-console.groq.com

# Servicios externos
SERVICES_TASK_SERVICE_URL=http://localhost:8003
SERVICES_USER_SERVICE_URL=http://localhost:8001
```

---

## Cómo ejecutar el proyecto

```bash
# 1. Clonar el repositorio
git clone https://github.com/[org]/superOscholar-engineplanning-service.git

# 2. Configurar variables de entorno en application-dev.yml
# (ver sección Variables de Entorno)

# 3. Levantar la base de datos con Docker
docker-compose up -d postgres

# 4. Ejecutar el microservicio
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 5. Acceder a la API
# http://localhost:8004/api/v1/planning/
```

---

## Referencias

- [Documentación oficial Spring Boot 3.2](https://docs.spring.io/spring-boot/docs/3.2.x/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security — JWT](https://docs.spring.io/spring-security/reference/)
- [OpenFeign — Spring Cloud](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Resilience4j](https://resilience4j.readme.io/docs/getting-started)
- [Google Gemini API](https://ai.google.dev/docs)
- [Groq API](https://console.groq.com/docs/openai)
- [MapStruct](https://mapstruct.org/documentation/stable/reference/html/)
- [JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
- [Flyway](https://flywaydb.org/documentation/)
- [Clean Architecture — Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-Clean-Architecture.html)
- [Análisis de Requerimientos Módulo 4 — DOSW Company](docs/Analisis_Requerimientos_M4.pdf)

---

*A.IBERT — ECI Planner | DOSW Company | Escuela Colombiana de Ingeniería Julio Garavito*
*Módulo 4 — Motor de Planificación Inteligente | Sprint #1 — 26 de abril de 2026*
```