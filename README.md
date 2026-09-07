# Laboratorio: Ciclo de vida de una actividad — Cronómetro con vueltas

Asignatura: Desarrollo de aplicaciones para dispositivos convergentes
Universidad Piloto de Colombia — Juan Andrés Vanegas (juan-vanegas5)

## Objetivo

Comprender el ciclo de vida de las actividades Android extendiendo el laboratorio
del cronómetro para incluir el concepto de **vuelta (lap)** y cronometrar **cinco (5) vueltas**.

## Qué hace la app

- Cronómetro con los botones **Iniciar**, **Detener**, **Vuelta** y **Reiniciar**.
- El botón **Vuelta** informa el cumplimiento de cada vuelta: calcula el tiempo de esa
  vuelta (tiempo transcurrido desde la vuelta anterior) y el cronómetro continúa
  corriendo con la siguiente.
- Se cronometran cinco vueltas; al registrar la quinta el cronómetro se detiene solo.
- La pantalla muestra el tiempo total, el tiempo de la vuelta en curso, el indicador
  "Vuelta N de 5" y el **reporte de tiempos parciales** de cada vuelta con su acumulado
  y el tiempo total al finalizar.

## Ciclo de vida implementado

| Método | Qué hace en la app |
|---|---|
| `onCreate()` | Infla el layout, restaura el estado del `Bundle` y arranca `runTimer()` |
| `onStart()` | Reanuda el conteo si el cronómetro venía corriendo (`wasRunning`) |
| `onResume()`, `onPause()`, `onRestart()`, `onDestroy()` | Trazas en Logcat (tag `StopwatchActivity`) |
| `onStop()` | Guarda `wasRunning` y detiene el conteo mientras la actividad no es visible |
| `onSaveInstanceState()` | Guarda `seconds`, `running`, `wasRunning`, `lapCount`, `lastLapSeconds` y `lapTimes` |

Al girar el dispositivo la actividad se destruye y se recrea: el cronómetro y las
vueltas registradas se conservan gracias a `onSaveInstanceState()` / `onCreate()`.

## Estructura

```
app/src/main/java/co/edu/unipiloto/stopwatch/StopwatchActivity.java
app/src/main/res/layout/activity_stopwatch.xml
app/src/main/res/values/strings.xml
app/src/main/AndroidManifest.xml
```

## Cómo ejecutarlo

1. Android Studio → **File > Open** y seleccionar la carpeta `Stopwatch`.
2. Esperar el Gradle Sync (AGP 8.6.1, Gradle 8.9, compileSdk 34, minSdk 24, Java 17).
3. **Run** sobre un emulador o dispositivo.
4. Para ver el ciclo de vida: Logcat filtrando por el tag `StopwatchActivity`.

## Prueba sugerida

Iniciar → pulsar **Vuelta** cinco veces con pausas distintas → verificar que cada
vuelta reporta su tiempo, que el acumulado coincide con el cronómetro y que al
completar la quinta vuelta se detiene y muestra el tiempo total.

## Referencia

Griffiths, D. & Griffiths, D. (2017). *Head First Android Development*. O'Reilly Media.
