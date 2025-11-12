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

#### 1.5.2. Prueba de Integración Final (Fase 1)

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

### 1.6. Fase 1.5: Migración a Interfaz Gráfica (JavaFX)

Una vez validado el "motor" (lógica de negocio y acceso a datos) en la consola, el siguiente paso es conectar este motor a una interfaz de usuario gráfica (GUI) con JavaFX, demostrando la flexibilidad de la arquitectura de 3 capas.

#### 1.6.1. Configuración de JavaFX en Gradle

Para añadir JavaFX a un proyecto Gradle existente, se realizan dos modificaciones clave en el archivo `build.gradle.kts`:

1.  Se añaden los plugins `application` y `org.openjfx.javafxplugin`.
2.  Se configuran los módulos de JavaFX (`controls`, `fxml`) y se especifica la `mainClass` de la aplicación.

```kotlin
// Fichero: build.gradle.kts (extracto)

plugins {
    id("java")
    id("application") // <-- Añadido
    id("org.openjfx.javafxplugin") version "0.1.0" // <-- Añadido
}

// ...

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("com.ezequiel.ui.MainFX") // <-- Le dice a Gradle cómo ejecutar la app
}
```
Esto permite ejecutar la aplicación gráfica de forma robusta usando la tarea gradle run.

#### 1.6.2. Creación de la Vista (FXML) y el Lanzador (Application)

El patrón de JavaFX separa la "Vista" (lo que se ve) del "Lanzador" (el código que la arranca).

1. Vista (MainView.fxml): Se crea el archivo FXML en src/main/resources/. Se define la "cara" de la aplicación y se asigna un fx:id a los componentes que necesitarán ser controlados (como el Label de resultado).
```XML
<AnchorPane xmlns:fx="[http://javafx.com/fxml](http://javafx.com/fxml)"
            fx:controller="com.ezequiel.ui.MainController">

    <Label fx:id="lblResultado"></Label>

</AnchorPane>
```
2. Lanzador (MainFX.java): Se crea una nueva clase en la capa ui que hereda de javafx.application.Application. Su única misión es cargar el FXML y mostrar la ventana (el Stage).
```java
// Fichero: src/main/java/com.ezequiel/ui/MainFX.java
public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Gym Metrics");
        primaryStage.setScene(new javafx.scene.Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Lanza la aplicación JavaFX
    }
}
```
#### 1.6.3. El Controlador (El "Cerebro" de la UI)

El MainController.java es el "puente" entre la Vista (FXML) y nuestro "motor" (la capa logic).

1. **Inyección (@FXML)**: Se usa la anotación @FXML para "inyectar" el Label del FXML en una variable Java.

2. **Inicialización (```initialize()```)**: Se usa el método initialize(), que JavaFX llama automáticamente después de la inyección. Dentro de este método, se reutiliza exactamente la misma lógica de negocio que se probó en la consola (Fase 1).

3. **Resultado**: En lugar de System.out.println, el resultado del servicio se asigna al Label usando .setText().

```java
// Fichero: src/main/java/com/ezequiel/ui/MainController.java
package com.ezequiel.ui;

// ... (imports de logic, repository, model y javafx)

public class MainController {

    @FXML // 1. Inyecta el Label desde el FXML
    private Label lblResultado;

    @FXML // 2. JavaFX llama a este método automáticamente
    public void initialize() {
        // --- SE REUTILIZA EL MISMO MOTOR ---
        EntrenamientoRepositorio repo = new CsvEntrenamientoRepositorio();
        List<Entrenamiento> entrenamientos = repo.obtenerTodos();

        if (!entrenamientos.isEmpty()) {
            CalcularMetricasService service = new CalcularMetricasService();
            String ejercicioBuscado = "Press de Banca (Barra)";
            double maxPeso = service.encontrarPesoMaximo(entrenamientos, ejercicioBuscado);

            // 3. El resultado se muestra en la GUI, no en la consola
            lblResultado.setText("Peso máximo de " + ejercicioBuscado + ": " + maxPeso + " kg");
        } else {
            lblResultado.setText("No se encontraron entrenamientos.");
        }
    }
}
```
#### 1.6.4. Conclusión de la Fase 1.5

La aplicación ahora arranca una interfaz gráfica que carga los datos del CSV y muestra la métrica calculada. Esto valida la arquitectura de 3 capas y demuestra que la lógica de negocio (logic) y el acceso a datos (repository) son completamente independientes de la capa de presentación (ui), permitiendo cambiar de consola a GUI sin modificar el "motor".

## 2. Fase 2: Migración a Base de Datos (PostgreSQL)

El objetivo de la Fase 2 es reemplazar la fuente de datos efímera (el archivo CSV) por una base de datos persistente y robusta (PostgreSQL), validando la arquitectura de 3 capas.

### 2.1. Entorno de Base de Datos (Docker y DBeaver)

Se utiliza un stack profesional para la base de datos:

* **Docker:** Se configura un `docker-compose.yml` para levantar un contenedor de `postgres:latest`.
* **DBeaver:** Se utiliza como cliente de BBDD para conectarse, crear la base de datos `gym_metrics_db` y ejecutar scripts SQL.

### 2.2. Diseño del Esquema (CREATE TABLE)

Se diseña un script SQL para crear la tabla `entrenamientos`. El diseño mapea los campos del POJO `Entrenamiento.java` a tipos de datos SQL, usando `TIMESTAMP` para las fechas (permitiendo consultas de rango) y `SERIAL PRIMARY KEY` para un ID único.

```sql
-- Fichero: DBeaver (Script de Creación)
DROP TABLE IF EXISTS entrenamientos;

CREATE TABLE entrenamientos (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    exercise_title VARCHAR(255),
    set_type VARCHAR(50),
    weight_kg REAL,
    reps INTEGER,
    distance_km REAL,
    duration_seconds REAL,
    rpe REAL,
    description TEXT,
    exercise_notes TEXT
);
```

### 2.3. Implementación del Repositorio JDBC

Se añade el "driver" de PostgreSQL al ```build.gradle.kts``` ```(org.postgresql:postgresql:42.7.3).```

Siguiendo el Patrón Repositorio, se crea una **nueva implementación** de la interfaz ```EntrenamientoRepositorio``` llamada ```JdbcEntreneRepo.java```.

#### 2.3.1. Conexión y Método ```obtenerTodos()```

Se implementa ```obtenerTodos()``` usando JDBC (```java.sql.*```). Se utiliza un ```try-with-resources``` para gestionar la ```Connection```, ```Statement``` y ```ResultSet```, y se mapean las columnas de la BBDD de vuelta al POJO ```Entrenamiento```.
```java
// Fichero: repository/JdbcEntreneRepo.java (extracto)
@Override
public List<Entrenamiento> obtenerTodos() {
    List<Entrenamiento> entrenamientos = new ArrayList<>();
    String sql = "SELECT * FROM entrenamientos";
    try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
        
        while (rs.next()) {
            Entrenamiento ent = new Entrenamiento();
            // ... (mapeo de rs.getString(...) a ent.set...) ...
            ent.setExerciseTitle(rs.getString("exercise_title"));
            ent.setWeightKg(rs.getDouble("weight_kg"));
            // ...
            entrenamientos.add(ent);
        }
    } catch (SQLException e ){
        e.printStackTrace();
    }
    return entrenamientos;
}
```

### 2.3.2. Método `guardar()` y Parseo de Fechas

Se implementa `guardar(Entrenamiento e)` usando un `PreparedStatement` para insertar datos de forma segura (evitando Inyección SQL).

El mayor desafío fue parsear los `String` de fecha del CSV (ej. "21 ago 2025") al tipo `TIMESTAMP` de PostgreSQL. Se creó una función `convertToTimestamp` personalizada que usa `SimpleDateFormat` y `DateFormatSymbols` para manejar el formato "Spanglish" del CSV (meses en español, excepto "sep").

```java
// Fichero: repository/JdbcEntreneRepo.java (extracto)
@Override
public void guardar(Entrenamiento entrenamiento) {
    String sql = "INSERT INTO entrenamientos (start_time, end_time, set_type, weight_kg, reps, exercise_title) VALUES (?, ?, ?, ?, ?, ?)";
    
    try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        PreparedStatement ps = conn.prepareStatement(sql)) {
        
        // Se usa el 'helper' para convertir el String a un objeto Timestamp
        ps.setTimestamp(1, convertToTimestamp(entrenamiento.getStartTime()));
        ps.setTimestamp(2, convertToTimestamp(entrenamiento.getEndTime()));
        ps.setString(3, entrenamiento.getSetType());
        ps.setDouble(4, entrenamiento.getWeightKg());
        ps.setInt(5, entrenamiento.getReps());
        ps.setString(6, entrenamiento.getExerciseTitle());
        ps.executeUpdate();
        
    } catch (SQLException e ){
        e.printStackTrace();
    }
}
```
### 2.4. Migración de Datos ("Seeding")

Se crea un script de un solo uso, `MigracionDatos.java`. Este script utiliza **ambos** repositorios:

* Instancia `CsvEntrenamientoRepositorio` como `repoFuente`.
* Instancia `JdbcEntreneRepo` como `repoDestino`.
* Llama a `repoFuente.obtenerTodos()` para leer los 2106 registros del CSV a la memoria.
* Recorre la lista con un `for-each` y llama a `repoDestino.guardar(ent)` por cada registro, insertándolos en PostgreSQL.
* Antes de la ejecución final, se limpia la BBDD con `TRUNCATE TABLE entrenamientos;` para evitar duplicados.

### 2.5. Prueba Final ("El Cambiazo")

La prueba final valida la arquitectura. En la capa de UI (`MainController.java`), se cambia **una sola línea**:

```java
// Se comenta la línea de Fase 1: 
// EntrenamientoRepositorio repo = new CsvEntrenamientoRepositorio();

// Se activa la línea de Fase 2: 
EntrenamientoRepositorio repo = new JdbcEntreneRepo();
```
Al ejecutar la aplicación JavaFX (gradle run), esta se conecta a la BBDD (ya poblada) y muestra el cálculo de "Peso máximo" (ej. 47.5 kg) en la ventana, demostrando que la migración ha sido un éxito y la arquitectura de 3 capas funciona.


