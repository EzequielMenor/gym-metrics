# 🏋️ Analizador de Métricas de Gimnasio (Hevy)

Un analizador de métricas de gimnasio programado en Java. Este proyecto nace para solucionar una limitación de la app Hevy: la versión gratuita solo permite visualizar gráficos de los últimos 3 meses. Esta herramienta utiliza la exportación de datos CSV de Hevy para analizar y visualizar el historial completo de entrenamientos.

---

## 🎯 Objetivo y Contexto del Proyecto

Este repositorio es un proyecto académico para el ciclo de 2º de DAM (Desarrollo de Aplicaciones Multiplataforma).

El objetivo es construir una aplicación completa partiendo de una **arquitectura de 3 capas** escalable, migrando de una prueba de concepto de consola (Fase 1) a una aplicación de escritorio completa (Fase 2).

---

## ✨ Características (Fase 2 - Núcleo Funcional)

* **Persistencia Robusta:** Migración completa de la fuente de datos desde el CSV a **PostgreSQL** (mediante JDBC). La base de datos es ahora la fuente única de verdad.
* **Lanzador Gráfico:** Interfaz de usuario inicial con **JavaFX** y Scene Builder.
* **Arquitectura Validada:** Estructurado en capas (`model`, `repository`, `service`, `ui`), demostrando que la lógica de negocio es 100% independiente de la capa de acceso a datos (Patrón Repositorio).
* **Migración de Datos:** Script de un solo uso (`MigracionDatos.java`) para importar y sanear más de 2100 registros del CSV a PostgreSQL.
* **Cálculo Funcional:** Lógica de negocio (`CalculadoraMetricasService`) probada que calcula métricas clave (ej. peso máximo por ejercicio).

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 17+
* **Gestor de Proyecto:** Gradle (`build.gradle.kts`)
* **Base de Datos:** PostgreSQL (Contenedor Docker)
* **Acceso a Datos:** JDBC (`org.postgresql:postgresql`)
* **Interfaz:** JavaFX (Módulos `controls` y `fxml`)
* **Librerías Clave:** `com.opencsv:opencsv` (Usada solo para el script inicial de migración)

---

## 🚀 Roadmap (Evolución a Fase 2)

La arquitectura de este proyecto está diseñada para escalar a las siguientes características:

* [x] **Persistencia:** Migración completa de CSV a PostgreSQL usando JDBC.
* [x] **Interfaz de Usuario:** Lanzador JavaFX y Conexión al controlador inicial completados.
* [x] **Interactividad:** Implementar un **`ComboBox`** para seleccionar ejercicios dinámicamente y actualizar la métrica.
* [x] **Visualización:** Añadir Gráficos (`LineChart`) de JavaFX para mostrar el progreso histórico del ejercicio seleccionado.
* [x] **CRUD (Escritura):** Añadir formulario para registrar actividades nuevas (ej. correr/bici), completando la funcionalidad de `GUARDAR` en el repositorio.
* [ ] **(Opcional) Backend & Móvil:** Migrar la lógica de negocio a un backend con Spring Boot.

---

## 🏁 Cómo Empezar

Para arrancar esta aplicación necesitas **Docker** (para PostgreSQL).

1.  **Clonar y Configurar Docker:**
    ```bash
    git clone https://github.com/EzequielMenor/gym-metrics.git
    cd gym-metrics
    docker-compose up -d  # Levanta el servidor PostgreSQL
    ```

2.  **Preparar la Base de Datos (PostgreSQL):**
    * Conéctate con DBeaver (Host: `localhost`, User: `root`, Pass: `test`).
    * Crea la BBDD: `gym_metrics_db`.
    * Ejecuta el script `CREATE TABLE` (disponible en la documentación).

3.  **Migrar Datos:**
    * Asegúrate de que tu `workout_data.csv` más reciente está en `src/main/resources`.
    * Ejecuta la clase `MigracionDatos.main()` en IntelliJ **una sola vez**.

4.  **Ejecutar la Aplicación Gráfica:**
    * En la pestaña Gradle de IntelliJ, ejecuta `Tasks` > `application` > `run`.
    * La ventana de JavaFX se abrirá, leyendo los datos directamente desde PostgreSQL.

---

## 📁 Estructura del Proyecto

El proyecto sigue una arquitectura limpia para facilitar la separación de responsabilidades:
```
src/main/java/com/ezequiel/
├── model/ # POJOs (Datos puros)
├── logic/ # Lógica de Negocio/Cálculos (CalculadoraMetricasService)
├── repository/ # Contratos (Interface) e Implementaciones (JdbcEntreneRepo)
└── ui/ # Capa de Presentación (MainFX, MainController)
```
