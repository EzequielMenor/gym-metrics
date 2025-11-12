# Memoria Técnica del Proyecto: Analizador Hevy

## 1. Fase 1: MVP de Consola y Arquitectura

### 1.1. Objetivos de la Fase 1

El objetivo principal es crear una aplicación de consola en Java que lea y analice los datos exportados por la app de gimnasio Hevy (`workout_data.csv`).

El requisito de arquitectura más importante es que el proyecto sea **escalable**. La forma de acceder a los datos (leer el CSV) debe poder reemplazarse en el futuro por una base de datos (Fase 2) sin tener que reescribir la lógica de negocio ni la interfaz de usuario.

### 1.2. Configuración del Entorno

* **IDE:** IntelliJ IDEA
* **Gestor de Proyecto:** Gradle
* **Dependencia Clave:** Se añade `OpenCSV` al archivo `build.gradle` para el parseo de archivos CSV a objetos Java.

```groovy
// Fichero: build.gradle
dependencies {
    // ...
    implementation 'com.opencsv:opencsv:5.12.0'
    // ...
}
```
### 1.3. Arquitectura: Separación de Responsabilidades

Para lograr la escalabilidad, el proyecto se divide en paquetes que separan las responsabilidades:

com.ezequiel.model: Contiene los POJOs (los moldes de datos).

com.ezequiel.repository: Contiene la lógica de acceso a datos (el "cómo" se leen).

com.ezequiel.service: Contendrá la lógica de negocio (los cálculos).

com.ezequiel.ui: Contiene la capa de presentación (la consola, y en el futuro, JavaFX).

### 1.4. Implementación de Capas

#### 1.4.1. Capa model (El "Qué")

Se crea el POJO Entrenamiento.java. Esta clase debe seguir el estándar JavaBean (constructor vacío y métodos setters) para que la librería OpenCSV pueda instanciarla y rellenarla automáticamente.

Se usan las anotaciones @CsvBindByName para mapear las columnas del CSV (en snake_case, ej. exercise_title) a los atributos de la clase Java (en camelCase, ej. exerciseTitle).

```Java
// Fichero: src/main/java/com/ezequiel/model/Entrenamiento.java
package com.ezequiel.model;

import com.opencsv.bean.CsvBindByName;

public class Entrenamiento {
    @CsvBindByName(column = "start_time")
    private String startTime;
    @CsvBindByName(column = "exercise_title")
    private String exerciseTitle;
    @CsvBindByName(column = "weight_kg")
    private double weightKg;
    @CsvBindByName(column = "reps")
    private int reps;
    // ... otros atributos ...

    // Constructor vacío requerido por CsvToBean
    public Entrenamiento() {
    }

    // ... Getters y Setters para todos los atributos ...
}
```

#### 1.4.2. Capa repository (El "Cómo")

Se aplica el Patrón Repositorio para abstraer el origen de los datos.

1. El Contrato (Interfaz): Se define la interfaz EntrenamientoRepositorio, que obliga a cualquier clase que la implemente a tener un método obtenerTodos().

```Java
// Fichero: src/main/java/com/ezequiel/repository/EntrenamientoRepositorio.java
package com.ezequiel.repository;
import com.ezequiel.model.Entrenamiento;
import java.util.List;

public interface EntrenamientoRepositorio {
    List<Entrenamiento> obtenerTodos();
}
```
2. La Implementación (Fase 1): Se crea la clase CsvEntrenamientoRepositorio que implementa la interfaz. Esta clase lee el archivo workout_data.csv (ubicado en src/main/resources) y usa CsvToBeanBuilder de OpenCSV para convertirlo en una List<Entrenamiento>.

```Java
// Fichero: src/main/java/com/ezequiel/repository/CsvEntrenamientoRepositorio.java
package com.ezequiel.repository;

import com.ezequiel.model.Entrenamiento;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

public class CsvEntrenamientoRepositorio implements EntrenamientoRepositorio {

    private final String CSV_FILE = "workout_data.csv";

    @Override
    public List<Entrenamiento> obtenerTodos() {
        try (Reader reader = new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream(CSV_FILE))) {
            
            return new CsvToBeanBuilder<Entrenamiento>(reader)
                    .withType(Entrenamiento.class)
                    .build()
                    .parse();

        } catch (Exception e) {
            System.err.println("Error grave al leer el archivo CSV: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
```

#### 1.4.3. Capa ui (Prueba de Humo)

Se crea una clase Main.java para verificar que las capas model y repository funcionan correctamente. Se instancia el repositorio, se obtienen los datos y se imprime el total de registros para confirmar la lectura.

```Java
// Fichero: src/main/java/com/ezequiel/ui/Main.java
package com.ezequiel.ui;

import com.ezequiel.model.Entrenamiento;
import com.ezequiel.repository.CsvEntrenamientoRepositorio;
import com.ezequiel.repository.EntrenamientoRepositorio;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Se programa contra la Interfaz, no la clase concreta
        EntrenamientoRepositorio entrenamientoRepositorio = new CsvEntrenamientoRepositorio();
        List<Entrenamiento> entrenamientos = entrenamientoRepositorio.obtenerTodos();

        System.out.println("Total de registros leídos: " + entrenamientos.size());

        if (!entrenamientos.isEmpty()){
            System.out.println("Primer Ejercicio: " + entrenamientos.get(0).getExerciseTitle());
        }
    }
}
```

### 1.5. Capa `logic` (El "Cerebro" o Lógica de Negocio)

La capa `logic` (o `service`) es responsable de orquestar la aplicación y contener los cálculos de negocio. Esta capa se sitúa entre la `ui` (que pide los cálculos) y el `repository` (que provee los datos).

#### 1.5.1. Implementación del Servicio

Se crea la clase `CalculadoraMetricasService` dentro del paquete `logic`. El primer método implementado es `encontrarPesoMaximo`, que demuestra el procesamiento de la lista de datos.

Para este cálculo, se utiliza la **API Stream (Java 8+)** en lugar de bucles `for` tradicionales. Este enfoque de programación funcional es más limpio, legible y menos propenso a errores.

El método `encontrarPesoMaximo` implementa la siguiente "cadena de montaje" (stream pipeline):

1.  **`.stream()`**: Convierte la `List<Entrenamiento>` en un "río" de datos.
2.  **`.filter()`**: Filtra el río, dejando pasar solo los entrenamientos cuyo `exerciseTitle` coincide (ignorando mayúsculas/minúsculas).
3.  **`.mapToDouble()`**: Transforma el río de `Entrenamiento` en un río de `double` (solo los valores de `weightKg`).
4.  **`.max()`**: Encuentra el valor máximo en el río de números.
5.  **`.orElse(0.0)`**: Devuelve el máximo encontrado, o `0.0` si el río estaba vacío (ej. el ejercicio no se encontró), evitando así una `NoSuchElementException`.

```java
// Fichero: src/main/java/com/ezequiel/logic/CalculadoraMetricasService.java
package com.ezequiel.logic;

import com.ezequiel.model.Entrenamiento;
import java.util.List;

// (El nombre de la clase puede ser CalculadoraMetricasService)
public class CalcularMetricasService {

    public double encontrarPesoMaximo(List<Entrenamiento> entrenamientoList, String nombreEjercicio) {
        
        return entrenamientoList.stream()
                // 1. Filtra por el nombre del ejercicio
                .filter(e -> e.getExerciseTitle().equalsIgnoreCase(nombreEjercicio))
                // 2. Transforma a un stream de solo los pesos
                .mapToDouble(Entrenamiento::getWeightKg)
                // 3. Encuentra el máximo
                .max()
                // 4. Devuelve 0.0 si no se encontró nada
                .orElse(0.0);
    }
}
```

1.5.2. Prueba de Integración Final (Fase 1)

Finalmente, se modifica la clase Main.java (capa ui) para usar el nuevo CalculadoraMetricasService. Esto comprueba que todas las capas están conectadas y funcionan juntas: ui -> logic -> repository.

```java 
// Fichero: src/main/java/com/ezequiel/ui/Main.java
public class Main {
    public static void main(String[] args) {
        // ... (código del repositorio) ...
        List<Entrenamiento> entrenamientos = entrenamientoRepositorio.obtenerTodos();
        System.out.println("Total de registros leídos: " + entrenamientos.size());

        if (!entrenamientos.isEmpty()) {
            // Se instancia la capa de lógica
            CalcularMetricasService servicio = new CalcularMetricasService();
            
            // Se pide el cálculo
            String ejercicioBuscado = "Press de Banca (Barra)";
            double maxPeso = servicio.encontrarPesoMaximo(entrenamientos, ejercicioBuscado);
            
            // Se imprime el resultado
            System.out.println("Peso máximo en " + ejercicioBuscado + ": " + maxPeso + " kg");
            
        } else {
            System.out.println("No se encontraron entrenamientos.");
        }
    }
}
```