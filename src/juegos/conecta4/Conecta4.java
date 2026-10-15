package juegos.conecta4;

import consola.Entrada;
import consola.Jugable;

/**
 * Conecta 4 para 2 jugadores por turnos.
 *
 * <p>Puntaje: el mínimo posible para ganar son 4 movimientos propios (100 pts);
 * cada movimiento extra del ganador resta 5 (mínimo 15). Un empate vale 10.</p>
 */
public class Conecta4 implements Jugable {

    private static final char[] FICHAS = {'X', 'O'};
    private static final int PUNTAJE_MAXIMO = 100;
    private static final int PENALIZACION_POR_MOVIMIENTO = 5;
    private static final int PUNTAJE_MINIMO_VICTORIA = 15;
    private static final int PUNTAJE_EMPATE = 10;
    private static final int MOVIMIENTOS_MINIMOS = 4;

    @Override
    public String getNombreJuego() {
        return "Conecta 4";
    }

    @Override
    public int start() {
        TableroConecta4 tablero = new TableroConecta4(); // estado nuevo por partida
        int[] movimientos = new int[2];
        int turno = 0;

        System.out.println("\n=== CONECTA 4 ===");
        System.out.println("Jugador 1 = X | Jugador 2 = O. Une 4 fichas en línea para ganar.");

        while (true) {
            tablero.imprimir();
            int columna = Entrada.leerEntero(
                    "Jugador " + (turno + 1) + " (" + FICHAS[turno] + "), elige columna (1-7): ",
                    1, TableroConecta4.COLUMNAS) - 1;

            int fila = tablero.soltarFicha(columna, FICHAS[turno]);
            if (fila < 0) {
                System.out.println("Esa columna está llena. Elige otra.");
                continue; // no cambia el turno
            }
            movimientos[turno]++;

            if (tablero.formaLinea(fila, columna, FICHAS[turno])) {
                tablero.imprimir();
                int puntaje = calcularPuntaje(movimientos[turno]);
                System.out.println("¡Gana el Jugador " + (turno + 1) + " en " + movimientos[turno]
                        + " movimientos!");
                return puntaje;
            }
            if (tablero.estaLleno()) {
                tablero.imprimir();
                System.out.println("¡Empate! El tablero está lleno.");
                return PUNTAJE_EMPATE;
            }
            turno = 1 - turno;
        }
    }

    /** Eficiencia: menos movimientos del ganador = más puntos. */
    static int calcularPuntaje(int movimientosGanador) {
        int puntaje = PUNTAJE_MAXIMO
                - (movimientosGanador - MOVIMIENTOS_MINIMOS) * PENALIZACION_POR_MOVIMIENTO;
        return Math.max(PUNTAJE_MINIMO_VICTORIA, puntaje);
    }
}
