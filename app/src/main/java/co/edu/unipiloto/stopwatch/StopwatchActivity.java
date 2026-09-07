package co.edu.unipiloto.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

/**
 * Laboratorio: ciclo de vida de una actividad.
 *
 * Cronometro con el concepto de VUELTA (lap): permite cronometrar cinco vueltas,
 * informa el cumplimiento de cada una, calcula el tiempo de cada vuelta, continua
 * cronometrando la siguiente y al final reporta los tiempos parciales alcanzados.
 */
public class StopwatchActivity extends Activity {

    private static final String TAG = "StopwatchActivity";

    /** Numero de vueltas que se deben cronometrar. */
    private static final int TOTAL_VUELTAS = 5;

    /** Numero de segundos mostrados en el cronometro. */
    private int seconds = 0;
    /** Esta corriendo el cronometro? */
    private boolean running;
    /** Estaba corriendo antes de que la actividad dejara de ser visible? */
    private boolean wasRunning;

    /** Vueltas ya registradas. */
    private int lapCount = 0;
    /** Segundo del cronometro en el que termino la ultima vuelta. */
    private int lastLapSeconds = 0;
    /** Duracion en segundos de cada vuelta. */
    private int[] lapTimes = new int[TOTAL_VUELTAS];

    private TextView timeView;
    private TextView lapTimeView;
    private TextView lapStatusView;
    private TextView lapsView;
    private Button startButton;
    private Button stopButton;
    private Button lapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        Log.d(TAG, "onCreate");

        timeView = (TextView) findViewById(R.id.time_view);
        lapTimeView = (TextView) findViewById(R.id.lap_time_view);
        lapStatusView = (TextView) findViewById(R.id.lap_status_view);
        lapsView = (TextView) findViewById(R.id.laps_view);
        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        lapButton = (Button) findViewById(R.id.lap_button);

        // Se recupera el estado guardado antes de que la actividad fuera destruida.
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
            lapCount = savedInstanceState.getInt("lapCount");
            lastLapSeconds = savedInstanceState.getInt("lastLapSeconds");
            int[] savedLaps = savedInstanceState.getIntArray("lapTimes");
            if (savedLaps != null && savedLaps.length == TOTAL_VUELTAS) {
                lapTimes = savedLaps;
            }
        }

        mostrarVueltas();
        runTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
        savedInstanceState.putInt("lapCount", lapCount);
        savedInstanceState.putInt("lastLapSeconds", lastLapSeconds);
        savedInstanceState.putIntArray("lapTimes", lapTimes);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        if (wasRunning) {
            running = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // El cronometro se detiene mientras la actividad no sea visible.
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /** Inicia el cronometro cuando se pulsa el boton Iniciar. */
    public void onClickStart(View view) {
        if (lapCount < TOTAL_VUELTAS) {
            running = true;
        }
    }

    /** Detiene el cronometro cuando se pulsa el boton Detener. */
    public void onClickStop(View view) {
        running = false;
    }

    /**
     * Registra el cumplimiento de una vuelta: calcula su tiempo, la informa y
     * el cronometro continua con la siguiente hasta completar las cinco vueltas.
     */
    public void onClickLap(View view) {
        if (!running || lapCount >= TOTAL_VUELTAS) {
            return;
        }
        lapTimes[lapCount] = seconds - lastLapSeconds;
        lastLapSeconds = seconds;
        lapCount++;
        if (lapCount == TOTAL_VUELTAS) {
            // Al completar las cinco vueltas el cronometro se detiene y se reporta.
            running = false;
            wasRunning = false;
        }
        mostrarVueltas();
    }

    /** Reinicia el cronometro y las vueltas cuando se pulsa el boton Reiniciar. */
    public void onClickReset(View view) {
        running = false;
        wasRunning = false;
        seconds = 0;
        lapCount = 0;
        lastLapSeconds = 0;
        lapTimes = new int[TOTAL_VUELTAS];
        mostrarVueltas();
    }

    /** Actualiza cada segundo el cronometro usando un Handler. */
    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                timeView.setText(formatearTiempo(seconds));
                lapTimeView.setText(formatearTiempo(seconds - lastLapSeconds));

                if (lapCount < TOTAL_VUELTAS) {
                    lapStatusView.setText(getString(R.string.lap_status,
                            lapCount + 1, TOTAL_VUELTAS));
                } else {
                    lapStatusView.setText(getString(R.string.lap_finished, TOTAL_VUELTAS));
                }

                startButton.setEnabled(!running && lapCount < TOTAL_VUELTAS);
                stopButton.setEnabled(running);
                lapButton.setEnabled(running && lapCount < TOTAL_VUELTAS);

                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    /** Construye el reporte con los tiempos parciales de cada vuelta. */
    private void mostrarVueltas() {
        if (lapCount == 0) {
            lapsView.setText(R.string.no_laps);
            return;
        }
        StringBuilder reporte = new StringBuilder();
        int acumulado = 0;
        for (int i = 0; i < lapCount; i++) {
            acumulado += lapTimes[i];
            reporte.append(getString(R.string.lap_row, i + 1,
                    formatearTiempo(lapTimes[i]), formatearTiempo(acumulado)));
            reporte.append("\n");
        }
        if (lapCount == TOTAL_VUELTAS) {
            reporte.append("\n");
            reporte.append(getString(R.string.report_total, formatearTiempo(acumulado)));
        }
        lapsView.setText(reporte.toString());
    }

    /** Da formato h:mm:ss a una cantidad de segundos. */
    private String formatearTiempo(int totalSegundos) {
        int hours = totalSegundos / 3600;
        int minutes = (totalSegundos % 3600) / 60;
        int secs = totalSegundos % 60;
        return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
    }
}
