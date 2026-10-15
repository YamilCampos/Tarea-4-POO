package juegos.conecta4;

/**
 * Tablero de Conecta 4 (6 filas x 7 columnas).
 * Encapsula la matriz y las reglas de colocación y de línea ganadora.
 */
public class TableroConecta4 {

    public static final int FILAS = 6;
    public static final int COLUMNAS = 7;
    public static final char VACIO = '.';
    private static final int PARA_GANAR = 4;

    // Direcciones (dFila, dColumna): horizontal, vertical y las dos diagonales.
    private static final int[][] DIRECCIONES = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

    private final char[][] celdas = new char[FILAS][COLUMNAS];

    public TableroConecta4() {
        for (char[] fila : celdas) {
            java.util.Arrays.fill(fila, VACIO);
        }
    }

    public boolean columnaLlena(int columna) {
        return celdas[0][columna] != VACIO;
    }

    /**
     * Deja caer una ficha por la columna indicada (0-6).
     *
     * @return fila donde quedó la ficha, o -1 si la columna está llena
     */
    public int soltarFicha(int columna, char ficha) {
        for (int fila = FILAS - 1; fila >= 0; fila--) {
            if (celdas[fila][columna] == VACIO) {
                celdas[fila][columna] = ficha;
                return fila;
            }
        }
        return -1;
    }

    public boolean estaLleno() {
        for (int c = 0; c < COLUMNAS; c++) {
            if (!columnaLlena(c)) {
                return false;
            }
        }
        return true;
    }

    /** ¿La ficha recién colocada en (fila, columna) forma una línea de 4? */
    public boolean formaLinea(int fila, int columna, char ficha) {
        for (int[] d : DIRECCIONES) {
            int total = 1 + contar(fila, columna, d[0], d[1], ficha)
                          + contar(fila, columna, -d[0], -d[1], ficha);
            if (total >= PARA_GANAR) {
                return true;
            }
        }
        return false;
    }

    // Cuenta fichas consecutivas iguales avanzando en una dirección.
    private int contar(int fila, int columna, int df, int dc, char ficha) {
        int n = 0;
        int f = fila + df;
        int c = columna + dc;
        while (f >= 0 && f < FILAS && c >= 0 && c < COLUMNAS && celdas[f][c] == ficha) {
            n++;
            f += df;
            c += dc;
        }
        return n;
    }

    public void imprimir() {
        System.out.println();
        System.out.println("  1 2 3 4 5 6 7");
        for (int f = 0; f < FILAS; f++) {
            System.out.print("| ");
            for (int c = 0; c < COLUMNAS; c++) {
                System.out.print(celdas[f][c] + " ");
            }
            System.out.println("|");
        }
        System.out.println("+---------------+");
    }
}
