# Práctica: Programación Concurrente con Hilos en JavaFX

Repositorio desarrollado para la asignatura de **Programación de Aplicaciones de Escritorio** de la **Universidad Americana (UAM)**.  
**Unidad III:** Programación concurrente.  
**Caso de estudio:** NicaData Solutions S.A.

---

## 📋 Descripción del Proyecto
Esta aplicación de escritorio simula un **Monitor de Procesamiento Empresarial** desarrollado en **JavaFX**. Su objetivo principal es demostrar el uso de la concurrencia mediante la creación de hilos (`Thread` y `Runnable`), garantizando que las tareas pesadas o prolongadas se ejecuten en segundo plano sin congelar la interfaz gráfica (*JavaFX Application Thread*)[cite: 1, 4, 7, 13].

### Características Principales:
* **Ejecución Concurrente:** Procesamiento simultáneo de dos flujos de trabajo independientes (Clientes y Productos) con tiempos y volúmenes distintos.
* **Actualización Segura de la UI:** Uso obligatorio de `Platform.runLater()` para reflejar los cambios de progreso en la interfaz sin violar las reglas de hilos de JavaFX[cite: 7, 12].
* **Control de Ciclo de Vida:** Botones totalmente funcionales para **Iniciar**, **Pausar**, **Reanudar** y **Reiniciar** de manera independiente por cada proceso[cite: 1].
* **Mecanismos de Sincronización:** Uso de monitores (`wait()` / `notifyAll()`) e interrupción cooperativa (`isInterrupted()`) para detener y reanudar tareas de forma limpia[cite: 1, 17].

---

## 🛠️ Tecnologías y Requisitos
* **Java SE** (JDK recomendado: versión 17 o superior)
* **JavaFX** (controles FXML y Scene Builder)
* **Maven** (Gestor de dependencias y empaquetado)
* **IntelliJ IDEA** (Entorno de desarrollo recomendado)

---

## 📂 Estructura del Proyecto
```text
PracticaHilosPAE/
|-- src/main/java/
|   |-- ni/edu/uam/practicahilospae/
|       |-- MainApp.java             # Clase principal que carga JavaFX y la vista
|       |-- MonitorController.java   # Lógica de hilos, concurrencia y pausa/reanudación
|       |-- Launcher.java            # Lanzador auxiliar para empaquetado
|-- src/main/resources/
|   |-- ni/edu/uam/practicahilospae/
|       |-- monitor-view.fxml        # Interfaz gráfica moderna del monitor empresarial
|-- pom.xml                          # Configuración de dependencias Maven (JavaFX)
