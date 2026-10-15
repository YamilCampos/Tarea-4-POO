package consola;

/**
 * Contrato que debe cumplir cualquier juego de la consola.
 *
 * <p>La consola trabaja ÚNICAMENTE contra esta interfaz (polimorfismo): no
 * sabe si detrás hay Conecta 4, Memoria o un juego externo adaptado.</p>
 */
public interface Jugable {

    /**
     * Ejecuta una partida completa de 2 jugadores.
     *
     * @return puntaje final de la partida (mayor = mejor; 0 = sin puntaje)
     */
    int start();

    /** Nombre mostrado en los menús y estadísticas. */
    String getNombreJuego();

    /**
     * Nombre del ganador si el propio juego ya lo preguntó (caso Battleship).
     * Si devuelve {@code null}, la consola se lo pedirá al jugador.
     */
    default String getNombreGanador() {
        return null;
    }
}
