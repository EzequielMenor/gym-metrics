# 🏋️ Analizador de Métricas de Gimnasio (Hevy)

Un analizador de métricas de gimnasio programado en Java. Este proyecto nace para solucionar una limitación de la app Hevy: la versión gratuita solo permite visualizar gráficos de los últimos 3 meses. Esta herramienta utiliza la exportación de datos CSV de Hevy para analizar y visualizar el historial completo de entrenamientos.

---

## 🎯 Objetivo y Contexto del Proyecto

Este repositorio es un proyecto académico para el ciclo de 2º de DAM (Desarrollo de Aplicaciones Multiplataforma).

El objetivo es construir una aplicación completa partiendo de una base sólida y escalable, dividida en dos fases:

* **Fase 1 (Actual):** Una aplicación de consola en Java que lee el CSV de Hevy y calcula métricas básicas. El foco principal es diseñar una **arquitectura limpia** (separada por capas) que facilite la migración futura.
* **Fase 2 (Futura):** Evolucionar la aplicación a un proyecto final, reemplazando el lector de CSV por una base de datos (MySQL/PostgreSQL) y añadiendo una interfaz gráfica de escritorio con **JavaFX**.

---

## ✨ Características (Fase 1 - MVP)

* **Lectura de CSV:** Parsea el archivo `workout_data.csv` exportado por Hevy.
* **Mapeo de Datos:** Convierte cada fila del CSV en POJOs (Plain Old Java Objects) usando la librería `OpenCSV`.
* **Arquitectura Escalable:** Estructurado en capas (`model`, `repository`, `service`, `ui`) usando interfaces para una fácil migración a una base de datos (Patrón Repositorio).
* **Cálculo de Métricas (en desarrollo):** Lógica de negocio para calcular estadísticas clave (ej. encontrar el peso máximo para un ejercicio específico).

---

## 🛠️ Stack Tecnológico (Fase 1)

* **Lenguaje:** Java 17+
* **Gestor de Proyecto:** Gradle
* **Librerías Clave:**
    * `com.opencsv:opencsv`: Para el parseo de archivos CSV a Java Beans.

---

## 🚀 Roadmap (Plan de Escalado - Fase 2)

La arquitectura de este proyecto está diseñada para escalar a las siguientes características:

* **[ ] Persistencia:** Reemplazar el `CsvEntrenamientoRepositorio` por una implementación de `JdbcRepositorio` (JDBC) o `JpaRepositorio` (Spring Data JPA) para conectar a una base de datos (MySQL, PostgreSQL o H2).
* **[ ] Interfaz de Usuario:** Añadir una interfaz de escritorio con **JavaFX** para visualizar los datos, filtrar por ejercicio y mostrar gráficos de progreso.
* **[ ] (Opcional) Backend & Móvil:** Migrar la lógica de negocio a un backend con **Spring Boot** (Kotlin) y consumir los datos desde un cliente móvil **Flutter** (Dart).

---

## 🏁 Cómo Empezar

1.  **Clonar el repositorio:**
    ```bash
    git clone [URL_DE_TU_REPOSITORIO_AQUÍ]
    ```

2.  **Añadir tus datos:**
    * Descarga tu archivo de entrenamientos desde la app de Hevy (normalmente `workout_data.csv`).
    * Copia el archivo `workout_data.csv` en la carpeta `src/main/resources` del proyecto.

3.  **Ejecutar el proyecto:**
    * Abre el proyecto con IntelliJ IDEA.
    * Ejecuta la clase `Main.java` (ubicada en `src/main/java/com/ezequiel/ui/Main.java`).

---

## 📁 Estructura del Proyecto

El proyecto sigue una arquitectura limpia para facilitar la separación de responsabilidades:

```
src/main/java/com/ezequiel/
├── model/          # POJOs (ej. Entrenamiento.java)
├── repository/     # Interfaces (EntrenamientoRepositorio) y sus implementaciones (CsvEntrenamientoRepositorio)
├── service/        # Lógica de negocio (ej. CalculadoraMetricas)
└── ui/             # Punto de entrada (Main.java)
```