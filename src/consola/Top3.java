package consola;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Podio con las 3 mejores puntuaciones de UN juego.
 * Cada juego tiene su propia instancia, por lo que son independientes.
 */
public class Top3 {

    public static final int CAPACIDAD = 3;

    /** Entrada inmutable del podio. */
    public static final class Registro {
        private final String nombre;
        private final int puntaje;

        public Registro(String nombre, int puntaje) {
            this.nombre = nombre;
            this.puntaje = puntaje;
        }

        public String getNombre() { return nombre; }
        public int getPuntaje() { return puntaje; }
    }

    // Siempre ordenada de mayor a menor puntaje.
    private final List<Registro> posiciones = new ArrayList<>();

    /**
     * ¿Este puntaje entra al podio?
     * Sí, si es positivo y (hay lugar libre o supera estrictamente al último).
     * En empate con un registro existente gana el más antiguo.
     */
    public boolean clasifica(int puntaje) {
        if (puntaje <= 0) {
            return false;
        }
        if (posiciones.size() < CAPACIDAD) {
            return true;
        }
        return puntaje > posiciones.get(posiciones.size() - 1).getPuntaje();
    }

    /**
     * Inserta el registro en su posición, desplazando a los demás y
     * descartando al último si hay más de 3.
     *
     * @return posición obtenida (1 a 3) o -1 si no clasificó
     */
    public int registrar(String nombre, int puntaje) {
        if (!clasifica(puntaje)) {
            return -1;
        }
        int indice = 0;
        while (indice < posiciones.size() && posiciones.get(indice).getPuntaje() >= puntaje) {
            indice++;
        }
        posiciones.add(indice, new Registro(nombre, puntaje));
        if (posiciones.size() > CAPACIDAD) {
            posiciones.remove(posiciones.size() - 1); // sale el último lugar
        }
        return indice + 1;
    }

    public boolean estaVacio() {
        return posiciones.isEmpty();
    }

    public List<Registro> getPosiciones() {
        return Collections.unmodifiableList(posiciones);
    }

    public void imprimir() {
        if (posiciones.isEmpty()) {
            System.out.println("   (sin registros)");
            return;
        }
        for (int i = 0; i < posiciones.size(); i++) {
            Registro r = posiciones.get(i);
            System.out.printf("   %d. %-15s %5d pts%n", i + 1, r.getNombre(), r.getPuntaje());
        }
    }
}
