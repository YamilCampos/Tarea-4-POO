package consola;

/** Se lanza cuando la entrada estándar se cierra (fin de archivo / Ctrl+D). */
public class EntradaCerradaException extends RuntimeException {
    public EntradaCerradaException() {
        super("La entrada estándar se cerró.");
    }
}
