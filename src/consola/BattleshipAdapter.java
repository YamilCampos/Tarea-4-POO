package consola;

import battleship.BattleshipGame;
import java.util.NoSuchElementException;
import java.util.function.IntPredicate;
import stats.GestorEstadisticas;

/**
 * PATRÓN ADAPTADOR.
 *
 * <ul>
 *   <li><b>Target</b>: {@link Jugable} (lo que la consola espera).</li>
 *   <li><b>Adaptee</b>: {@link BattleshipGame} (sin modificar; su método es
 *       {@code void iniciarPartida(Scanner)}).</li>
 *   <li><b>Adapter</b>: esta clase.</li>
 * </ul>
 *
 * Traduce {@code iniciarPartida(Scanner)} a {@code int start()}. Como el juego
 * no devuelve nada, el resultado (duración y barcos vivos) se obtiene del
 * puente {@link GestorEstadisticas} y se convierte en un puntaje entero.
 */
public class BattleshipAdapter implements Jugable {

    private static final int PUNTOS_POR_BARCO_VIVO = 100;
    private static final int SEGUNDOS_DE_BONO = 300;

    private final BattleshipGame juego = new BattleshipGame();
    private final IntPredicate clasificaEnTop3;
    private String nombreGanador;

    /** Variante sin ranking: el juego siempre ofrecerá registrar el nickname. */
    public BattleshipAdapter() {
        this(p -> true);
    }

    /** @param clasificaEnTop3 pregunta a la consola si un puntaje entra al Top 3 */
    public BattleshipAdapter(IntPredicate clasificaEnTop3) {
        this.clasificaEnTop3 = clasificaEnTop3;
    }

    @Override
    public String getNombreJuego() {
        return "Battleship";
    }

    @Override
    public int start() {
        nombreGanador = null;
        GestorEstadisticas.reiniciar();
        // Así el propio juego solo pide el nickname cuando de verdad clasifica.
        GestorEstadisticas.setEvaluador((duracion, vivos) ->
                clasificaEnTop3.test(calcularPuntaje(duracion, vivos)));

        try {
            juego.iniciarPartida(Entrada.scanner());
        } catch (RuntimeException e) { // p. ej. InputMismatchException en el juego original
            System.out.println("\n⚠️ Battleship se interrumpió por una entrada inválida ("
                    + e.getClass().getSimpleName() + "). Partida cancelada.");
            descartarLineaPendiente();
            return 0;
        }

        if (!GestorEstadisticas.isPartidaTerminada()) {
            return 0;
        }
        nombreGanador = GestorEstadisticas.getUltimoNickname();
        return calcularPuntaje(GestorEstadisticas.getUltimaDuracion(),
                GestorEstadisticas.getUltimosBarcosVivos());
    }

    @Override
    public String getNombreGanador() {
        return nombreGanador;
    }

    /** Más barcos vivos y menos tiempo = más puntos (misma idea que su compareTo). */
    static int calcularPuntaje(long duracionSegundos, int barcosVivos) {
        int bonoTiempo = (int) Math.max(0, SEGUNDOS_DE_BONO - duracionSegundos);
        return barcosVivos * PUNTOS_POR_BARCO_VIVO + bonoTiempo;
    }

    private void descartarLineaPendiente() {
        try {
            Entrada.scanner().nextLine();
        } catch (NoSuchElementException ignorada) {
            // sin más entrada
        }
    }
}
