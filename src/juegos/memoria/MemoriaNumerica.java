package juegos.memoria;

import consola.Entrada;
import consola.Jugable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Memoria Numérica / Secuencia para 2 jugadores.
 *
 * <p>Cada ronda se agrega un dígito a la secuencia compartida. Los jugadores se
 * alternan: al que le toca ve la secuencia unos segundos, se oculta con saltos
 * de línea y debe reescribirla. Quien falla pierde y la partida termina.</p>
 *
 * <p>Puntaje = rondas completadas con éxito x 10.</p>
 */
public class MemoriaNumerica implements Jugable {

    private static final int PUNTOS_POR_RONDA = 10;
    private static final int MS_BASE = 1500;
    private static final int MS_POR_DIGITO = 600;

    private final Random random = new Random();
    private final int msBase;
    private final int msPorDigito;

    public MemoriaNumerica() {
        this(MS_BASE, MS_POR_DIGITO);
    }

    /** Permite acortar los tiempos (útil para pruebas). */
    public MemoriaNumerica(int msBase, int msPorDigito) {
        this.msBase = msBase;
        this.msPorDigito = msPorDigito;
    }

    @Override
    public String getNombreJuego() {
        return "Memoria Numérica";
    }

    @Override
    public int start() {
        List<Integer> secuencia = new ArrayList<>();
        int turno = 0;
        int rondasCompletadas = 0;

        System.out.println("\n=== MEMORIA NUMÉRICA ===");
        System.out.println("Memoriza la secuencia y reescríbela. Cada ronda crece un dígito.");
        System.out.println("Los jugadores se alternan; quien falle pierde.");

        while (true) {
            secuencia.add(random.nextInt(10));
            int ronda = secuencia.size();

            Entrada.limpiarPantalla();
            System.out.println("=== RONDA " + ronda + " ===");
            Entrada.leerLinea("Turno del Jugador " + (turno + 1)
                    + ". Que el otro jugador no mire y presiona ENTER... ");

            mostrarYOcultar(secuencia);

            String respuesta = Entrada.leerLinea("Jugador " + (turno + 1)
                    + ", escribe la secuencia (con o sin espacios): ");

            if (coincide(respuesta, secuencia)) {
                rondasCompletadas++;
                System.out.println("✔ ¡Correcto! Rondas completadas: " + rondasCompletadas);
                turno = 1 - turno;
            } else {
                System.out.println("✘ Incorrecto. La secuencia era: " + formatear(secuencia));
                System.out.println("¡Gana el Jugador " + (2 - turno) + "! Ronda máxima superada: "
                        + rondasCompletadas);
                return rondasCompletadas * PUNTOS_POR_RONDA;
            }
        }
    }

    private void mostrarYOcultar(List<Integer> secuencia) {
        System.out.println("\nMemoriza:\n\n    " + formatear(secuencia) + "\n");
        pausa(msBase + (long) msPorDigito * secuencia.size());
        Entrada.limpiarPantalla(); // saltos de línea que ocultan la secuencia
    }

    /** Compara solo los dígitos escritos contra la secuencia. */
    static boolean coincide(String respuesta, List<Integer> secuencia) {
        String solo = respuesta.replaceAll("[^0-9]", "");
        StringBuilder esperado = new StringBuilder();
        for (int d : secuencia) {
            esperado.append(d);
        }
        return solo.equals(esperado.toString());
    }

    private static String formatear(List<Integer> secuencia) {
        StringBuilder sb = new StringBuilder();
        for (int d : secuencia) {
            sb.append(d).append(' ');
        }
        return sb.toString().trim();
    }

    private static void pausa(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
