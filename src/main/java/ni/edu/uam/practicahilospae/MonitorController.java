package ni.edu.uam.practicahilospae;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class MonitorController {

    // Controles para Clientes
    @FXML private Label lblEstadoClientes;
    @FXML private Label lblPorcentajeClientes;
    @FXML private ProgressBar barraClientes;
    @FXML private Button btnIniciarClientes;
    @FXML private Button btnPausarClientes;
    @FXML private Button btnReanudarClientes;
    @FXML private Button btnReiniciarClientes;

    // Controles para Productos
    @FXML private Label lblEstadoProductos;
    @FXML private Label lblPorcentajeProductos;
    @FXML private ProgressBar barraProductos;
    @FXML private Button btnIniciarProductos;
    @FXML private Button btnPausarProductos;
    @FXML private Button btnReanudarProductos;
    @FXML private Button btnReiniciarProductos;

    private Thread hiloClientes;
    private Thread hiloProductos;

    // Variables de control de pausa y progreso para Clientes
    private final Object lockClientes = new Object();
    private volatile boolean enPausaClientes = false;
    private int avanceClientes = 1;

    // Variables de control de pausa y progreso para Productos
    private final Object lockProductos = new Object();
    private volatile boolean enPausaProductos = false;
    private int avanceProductos = 1;

    // ==========================================
    // LÓGICA DE CLIENTES
    // ==========================================
    @FXML
    private void iniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            return;
        }

        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0%");
        lblEstadoClientes.setText("Procesando clientes...");

        btnIniciarClientes.setDisable(true);
        btnPausarClientes.setDisable(false);
        btnReanudarClientes.setDisable(true);
        btnReiniciarClientes.setDisable(true);

        enPausaClientes = false;
        avanceClientes = 1;

        Runnable tareaClientes = () -> {
            try {
                for (int i = avanceClientes; i <= 100; i++) {
                    avanceClientes = i;

                    synchronized (lockClientes) {
                        while (enPausaClientes) {
                            lockClientes.wait();
                        }
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(80); // Velocidad simulación clientes

                    final int avance = i;
                    Platform.runLater(() -> {
                        barraClientes.setProgress(avance / 100.0);
                        lblPorcentajeClientes.setText(avance + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Clientes finalizados");
                    btnPausarClientes.setDisable(true);
                    btnReanudarClientes.setDisable(true);
                    btnReiniciarClientes.setDisable(false);
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    if (!enPausaClientes) {
                        lblEstadoClientes.setText("Clientes detenidos");
                    }
                });
            }
        };

        hiloClientes = new Thread(tareaClientes);
        hiloClientes.setName("Hilo-Clientes");
        hiloClientes.setDaemon(true);
        hiloClientes.start();
    }

    @FXML
    private void pausarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            enPausaClientes = true;
            btnPausarClientes.setDisable(true);
            btnReanudarClientes.setDisable(false);
            btnReiniciarClientes.setDisable(false);
            lblEstadoClientes.setText("Clientes pausados en " + avanceClientes + "%");
        }
    }

    @FXML
    private void reanudarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive() && enPausaClientes) {
            synchronized (lockClientes) {
                enPausaClientes = false;
                lockClientes.notifyAll();
            }
            btnPausarClientes.setDisable(false);
            btnReanudarClientes.setDisable(true);
            btnReiniciarClientes.setDisable(true);
            lblEstadoClientes.setText("Reanudando clientes...");
        }
    }

    @FXML
    private void reiniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            hiloClientes.interrupt();
        }
        enPausaClientes = false;
        avanceClientes = 1;
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0%");
        lblEstadoClientes.setText("Esperando inicio");

        btnIniciarClientes.setDisable(false);
        btnPausarClientes.setDisable(true);
        btnReanudarClientes.setDisable(true);
        btnReiniciarClientes.setDisable(true);
    }

    // ==========================================
    // LÓGICA DE PRODUCTOS
    // ==========================================
    @FXML
    private void iniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            return;
        }

        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0%");
        lblEstadoProductos.setText("Procesando productos...");

        btnIniciarProductos.setDisable(true);
        btnPausarProductos.setDisable(false);
        btnReanudarProductos.setDisable(true);
        btnReiniciarProductos.setDisable(true);

        enPausaProductos = false;
        avanceProductos = 1;

        Runnable tareaProductos = () -> {
            try {
                for (int i = avanceProductos; i <= 50; i++) {
                    avanceProductos = i;

                    synchronized (lockProductos) {
                        while (enPausaProductos) {
                            lockProductos.wait();
                        }
                    }

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(120); // Velocidad simulación productos (distinta para ver concurrencia)

                    final int avance = i;
                    Platform.runLater(() -> {
                        barraProductos.setProgress(avance / 50.0);
                        int porcentaje = (int) ((avance / 50.0) * 100);
                        lblPorcentajeProductos.setText(porcentaje + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoProductos.setText("Productos finalizados");
                    btnPausarProductos.setDisable(true);
                    btnReanudarProductos.setDisable(true);
                    btnReiniciarProductos.setDisable(false);
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    if (!enPausaProductos) {
                        lblEstadoProductos.setText("Productos detenidos");
                    }
                });
            }
        };

        hiloProductos = new Thread(tareaProductos);
        hiloProductos.setName("Hilo-Productos");
        hiloProductos.setDaemon(true);
        hiloProductos.start();
    }

    @FXML
    private void pausarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            enPausaProductos = true;
            btnPausarProductos.setDisable(true);
            btnReanudarProductos.setDisable(false);
            btnReiniciarProductos.setDisable(false);
            int porcentaje = (int) ((avanceProductos / 50.0) * 100);
            lblEstadoProductos.setText("Productos pausados en " + porcentaje + "%");
        }
    }

    @FXML
    private void reanudarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive() && enPausaProductos) {
            synchronized (lockProductos) {
                enPausaProductos = false;
                lockProductos.notifyAll();
            }
            btnPausarProductos.setDisable(false);
            btnReanudarProductos.setDisable(true);
            btnReiniciarProductos.setDisable(true);
            lblEstadoProductos.setText("Reanudando productos...");
        }
    }

    @FXML
    private void reiniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            hiloProductos.interrupt();
        }
        enPausaProductos = false;
        avanceProductos = 1;
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0%");
        lblEstadoProductos.setText("Esperando inicio");

        btnIniciarProductos.setDisable(false);
        btnPausarProductos.setDisable(true);
        btnReanudarProductos.setDisable(true);
        btnReiniciarProductos.setDisable(true);
    }

    // Cierre seguro de hilos al cerrar la ventana principal
    public void detenerAlCerrar() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            synchronized (lockClientes) {
                enPausaClientes = false;
                lockClientes.notifyAll();
            }
            hiloClientes.interrupt();
        }
        if (hiloProductos != null && hiloProductos.isAlive()) {
            synchronized (lockProductos) {
                enPausaProductos = false;
                lockProductos.notifyAll();
            }
            hiloProductos.interrupt();
        }
    }
}