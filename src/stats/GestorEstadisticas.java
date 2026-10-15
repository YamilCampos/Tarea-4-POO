package stats;

import battleship.EstadisticaBattleship;
import java.util.function.BiPredicate;

/**
 * Punto de contacto entre {@code BattleshipGame} y la consola.
 *
 * <p>El juego original NO devuelve ningún valor (su método es {@code void}) y
 * al terminar consulta/registra sus resultados a través de esta clase. La
 * consola aprovecha ese hecho: aquí se CAPTURA el resultado de la partida
 * (duración, barcos vivos, nickname) para que {@code BattleshipAdapter} lo
 * traduzca a un {@code int}. Así el código original queda intacto.</p>
 */
public final class GestorEstadisticas {

    /** Decide si (duración, barcosVivos) merece entrar al Top 3 de la consola. */
    private static BiPredicate<Long, Integer> evaluador = (d, v) -> true;

    private static boolean partidaTerminada;
    private static long ultimaDuracion;
    private static int ultimosBarcosVivos;
    private static String ultimoNickname;

    private GestorEstadisticas() { }

    /** Prepara el gestor antes de una nueva partida. */
    public static void reiniciar() {
        partidaTerminada = false;
        ultimaDuracion = 0;
        ultimosBarcosVivos = 0;
        ultimoNickname = null;
    }

    public static void setEvaluador(BiPredicate<Long, Integer> nuevoEvaluador) {
        evaluador = (nuevoEvaluador != null) ? nuevoEvaluador : (d, v) -> true;
    }

    /** Invocado por BattleshipGame al ganar alguien: captura el resultado. */
    public static boolean esRecordBattleship(long duracion, int barcosVivos) {
        partidaTerminada = true;
        ultimaDuracion = duracion;
        ultimosBarcosVivos = barcosVivos;
        return evaluador.test(duracion, barcosVivos);
    }

    /** Invocado por BattleshipGame cuando hay récord: captura el nickname. */
    public static void registrarStatBattleship(EstadisticaBattleship stat) {
        ultimoNickname = stat.getNickname();
    }

    public static boolean isPartidaTerminada() { return partidaTerminada; }
    public static long getUltimaDuracion() { return ultimaDuracion; }
    public static int getUltimosBarcosVivos() { return ultimosBarcosVivos; }
    public static String getUltimoNickname() { return ultimoNickname; }
}
