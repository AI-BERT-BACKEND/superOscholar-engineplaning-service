# superOscholar-engineplanning-service

> Microservicio del Motor de Planificación Inteligente de A.IBERT — ECI Planner.
> Analiza, prioriza y distribuye automáticamente las tareas académicas del estudiante optimizando su tiempo disponible para mejorar el rendimiento y evitar el estrés.

---

## Tabla de Contenido

* [Equipo](#equipo)
* [Descripción del Módulo](#descripción-del-módulo)
* [Microservicios del Módulo 4](#microservicios-del-módulo-4)
* [Requerimientos Funcionales](#requerimientos-funcionales)
* [Requerimientos No Funcionales](#requerimientos-no-funcionales)
* [Arquitectura](#arquitectura)
* [Stack Tecnológico](#stack-tecnológico)
* [Estructura del Proyecto](#estructura-del-proyecto)
* [Endpoints REST](#endpoints-rest)
* [Diagramas](#diagramas)
* [Gestión del Proyecto](#gestión-del-proyecto)
* [Pruebas y Calidad](#pruebas-y-calidad)
* [Demo](#demo)
* [Variables de Entorno](#variables-de-entorno)
* [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
* [Referencias](#referencias)

---

## Equipo

**Módulo 4 — Motor de Planificación Inteligente**
**Proyecto:** A.IBERT — ECI Planner
**Institución:** Escuela Colombiana de Ingeniería Julio Garavito

| Integrante                    |
| ----------------------------- |
| Juan Esteban Sánchez García   |
| Juan Carlos Bohórquez Monroy  |
| Jeyder Nicolay Leon Lancheros |
| Nicolás Guillermo Ibañez León |

---

## Descripción del Módulo

El **Motor de Planificación Inteligente** es el núcleo de decisión del sistema A.IBERT.

No se limita a listar tareas, sino que:

* Analiza el contexto académico del estudiante
* Calcula prioridades automáticamente
* Distribuye la carga de trabajo
* Optimiza el uso del tiempo

Problemas que resuelve:

* Mala priorización de tareas
* Sobrecarga en días específicos
* Falta de planificación estratégica
* Estrés académico acumulado

---

## Microservicios del Módulo 4

| Microservicio           | Puerto | Responsabilidad        |
| ----------------------- | ------ | ---------------------- |
| planning-engine-service | 8004   | Motor de planificación |

### Comunicación

```
planning-engine-service
 ├── task-service
 └── user-service
```

---

## Requerimientos Funcionales

### R14 — Priorización

Calcula la prioridad de tareas con base en:

* Peso académico
* Fecha límite
* Tiempo estimado

### R15 — Balance de tiempo

Detecta:

* Días sobrecargados (>80%)
* Días vacíos (<20%)

### R16 — Distribución automática

Genera un plan semanal optimizado respetando disponibilidad.

### R17 — Rebalanceo dinámico

Reorganiza tareas cuando el estudiante no cumple el plan.

---

## Requerimientos No Funcionales

| ID     | Descripción                     |
| ------ | ------------------------------- |
| RNF-01 | Seguridad con JWT               |
| RNF-02 | Respuesta < 2s                  |
| RNF-03 | Disponibilidad 95%              |
| RNF-04 | Escalabilidad por microservicio |
| RNF-05 | Cobertura mínima 70%            |

---

## Arquitectura

### Enfoque

* Microservicios
* Clean Architecture
* API REST
* Hexagonal (Ports & Adapters)

### Capas

```
entrypoints → application → domain ← infrastructure
```

### Algoritmo de priorización

```
score =
  (nota × 0.35) +
  (proximidad × 0.35) +
  (peso × 0.20) +
  (creditos × 0.10)
```

---

## Stack Tecnológico

| Área         | Tecnología       |
| ------------ | ---------------- |
| Lenguaje     | Java 21          |
| Framework    | Spring Boot      |
| Seguridad    | JWT              |
| Persistencia | JPA + PostgreSQL |
| Comunicación | HTTP REST        |
| Testing      | JUnit + Mockito  |
| Contenedores | Docker           |

---

## Estructura del Proyecto

```
src/
 ├── domain/
 ├── application/
 ├── entrypoints/
 └── infrastructure/
```

---

## Endpoints REST

| Método | Endpoint                  |
| ------ | ------------------------- |
| GET    | /planning/prioritization  |
| GET    | /planning/balance         |
| POST   | /planning/distribution    |
| POST   | /planning/rebalance/failure |
| POST   | /planning/rebalance/reorganize |

---

## Diagramas

* Contexto
* Casos de uso
* Clases
* Componentes
* Secuencia

---

## Gestión del Proyecto

* Scrum
* Sprints semanales
* JIRA
* Planning Poker
* Slack

---

## Pruebas y Calidad

* Unitarias (JUnit, Mockito)
* Integración (@WebMvcTest)
* Cobertura con JaCoCo

---

## Demo

Pendiente de implementación.

---

## Variables de Entorno

```
DB_URL=
DB_USERNAME=
DB_PASSWORD=

JWT_SECRET=
FEIGN_TASK_SERVICE_URL=
FEIGN_PROFILE_SERVICE_URL=

GEMINI_API_KEY=
GROQ_API_KEY=
```

---

## Cómo ejecutar el proyecto

```
git clone <repo>
docker-compose up
mvn spring-boot:run
```

---

## Referencias

* Spring Boot
* Spring Security
* JPA

