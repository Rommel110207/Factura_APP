package ni.edu.uam.practicahilospae;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {

    @FXML private Label lblEstado;
    @FXML private Label lblPorcentaje;
    @FXML private ProgressBar barraProgreso;
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private Button btnReanudar;
    @FXML private Button btnReiniciar;

    private Thread hiloProceso;

    // Objetos de sincronización para la pausa y reanudación
    private final Object pausaLock = new Object();
    private volatile boolean enPausa = false;
    private int avanceActual = 1; // Para recordar por dónde iba

    @FXML
    private void iniciarProceso() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Procesando registros...");

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReanudar.setDisable(true);
        btnReiniciar.setDisable(true);

        enPausa = false;
        avanceActual = 1;

        Runnable tarea = () -> {
            try {
                for (int i = avanceActual; i <= 100; i++) {
                    avanceActual = i; // Guardamos el progreso actual

                    // Gestión de la pausa utilizando el monitor
                    synchronized (pausaLock) {
                        while (enPausa) {
                            pausaLock.wait();
                        }
                    }

                    // Verificación de interrupción cooperativa
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(100); // Simula el tiempo de procesamiento

                    final int avance = i;

                    // Actualización segura de la interfaz gráfica
                    Platform.runLater(() -> {
                        barraProgreso.setProgress(avance / 100.0);
                        lblPorcentaje.setText(avance + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstado.setText("Proceso finalizado");
                    habilitarBotonesAlFinalizar();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                Platform.runLater(() -> {
                    if (!enPausa) {
                        lblEstado.setText("Proceso detenido");
                        habilitarBotonesAlFinalizar();
                    }
                });
            }
        };

        hiloProceso = new Thread(tarea);
        hiloProceso.setName("Hilo-Procesamiento");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void detenerProceso() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            enPausa = true; // Activamos la bandera de pausa

            btnDetener.setDisable(true);
            btnReanudar.setDisable(false);

            lblEstado.setText("Proceso pausado en " + avanceActual + "%");
        }
    }

    @FXML
    private void reanudarProceso() {
        if (hiloProceso != null && hiloProceso.isAlive() && enPausa) {
            synchronized (pausaLock) {
                enPausa = false;
                pausaLock.notifyAll(); // Despierta al hilo para que continúe
            }

            btnDetener.setDisable(false);
            btnReanudar.setDisable(true);

            lblEstado.setText("Reanudando proceso...");
        }
    }

    @FXML
    private void reiniciarProceso() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            hiloProceso.interrupt();
        }

        enPausa = false;
        avanceActual = 1;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");

        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReanudar.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    private void habilitarBotonesAlFinalizar() {
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReanudar.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    public void detenerAlCerrar() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            synchronized (pausaLock) {
                enPausa = false;
                pausaLock.notifyAll();
            }
            hiloProceso.interrupt();
        }
    }
}