Issue #1: Mejora de Visualización (Doble Serie en Gráfico)

El objetivo es añadir la línea de 1RM Estimado al gráfico de progreso.

Keyword Descripción
Característica: Visualización del Progreso de Fuerza Dual
Escenario: Mostrar la evolución del peso máximo real y el 1RM estimado en un solo gráfico.
Dado El Dashboard se ha cargado correctamente desde la Base de Datos.
Y El usuario ha seleccionado un ejercicio (Press de Banca).
Cuando El método actualizarMetricas() se ejecuta.
Entonces El gráfico (LineChart) debe mostrar dos series de datos.
Y La primera serie debe representar el Peso Máximo por sesión (el dato real).
Y La segunda serie debe representar el 1RM Estimado (el dato teórico calculado).
Y Ambos ejes (eje X: Fecha; eje Y: Peso en kg) deben ser compartidos.

Issue #2: Usabilidad y Navegación (Búsqueda Rápida)

El objetivo es mejorar la usabilidad del ComboBox de ejercicios mediante un filtro de búsqueda.

Keyword Descripción
Característica: Búsqueda Rápida y Filtrado Dinámico de Ejercicios
Escenario: Filtrar la lista de ejercicios al teclear en un campo de texto.
Dado El Dashboard ha cargado la lista completa de ejercicios.
Y Existe un nuevo campo de texto (TextField) para la búsqueda.
Cuando El usuario introduce una parte del nombre del ejercicio (ej. "Press") en el campo de texto.
Entonces El ComboBox de Ejercicios debe actualizarse instantáneamente.
Y El ComboBox solo debe mostrar los ejercicios que contengan el texto introducido (ej. "Press de Banca", "Press de Piernas").
Y Al seleccionar un ejercicio filtrado, el gráfico debe actualizarse correctamente.

Issue #3: Expansión de la Funcionalidad (CRUD: Create)

El objetivo es implementar la funcionalidad para registrar actividades no basadas en peso (correr, bici).

Keyword Descripción
Característica: Registro de Actividades No Pesadas (CRUD: Create)
Escenario: Guardar un nuevo registro de actividad de cardio o distancia en la Base de Datos.
Dado El usuario está en la aplicación y el servidor de PostgreSQL está activo.
Y Existe un formulario (VBox o nueva ventana) con campos para Distancia, Duración y Fecha.
Cuando El usuario rellena los campos y pulsa el botón "Guardar Actividad".
Entonces El sistema debe llamar al método repo.guardar(entrenamiento) con los datos de distancia y duración.
Y La Base de Datos debe contener el nuevo registro en las columnas distance_km y duration_seconds (con weight_kg=0).
Y La lista de ejercicios del ComboBox debe actualizarse (si el nuevo ejercicio es único).
