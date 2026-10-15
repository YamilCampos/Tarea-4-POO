package consola;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import juegos.conecta4.Conecta4;
import juegos.memoria.MemoriaNumerica;

/**
 * Clase principal: menú, lanzamiento de juegos y estadísticas.
 * Solo conoce la interfaz {@link Jugable}; nunca una clase concreta de juego
 * (salvo al registrarlos en {@link #registrarJuegos()}).
 */
public class ConsolaVideojuegos {

    private static final int OPCION_LANZAR = 1;
    private static final int OPCION_ESTADISTICAS = 2;
    private static final int OPCION_SALIR = 3;

    // Un Top 3 independiente por juego, en orden de registro.
    private final Map<Jugable, Top3> ranking = new LinkedHashMap<>();

    public ConsolaVideojuegos() {
        registrarJuegos();
        sembrarEscenarioDemo();
    }

    public static void main(String[] args) {
        new ConsolaVideojuegos().ejecutar();
    }

    /** Único lugar donde se agregan juegos nuevos a la consola. */
    private void registrarJuegos() {
        ranking.put(new Conecta4(), new Top3());
        ranking.put(new MemoriaNumerica(), new Top3());
        Top3 topBattleship = new Top3();
        ranking.put(new BattleshipAdapter(topBattleship::clasifica), topBattleship);
    }

    /** Si un juego no tiene partidas, se carga un podio ficticio de prueba. */
    private void sembrarEscenarioDemo() {
        for (Map.Entry<Jugable, Top3> e : ranking.entrySet()) {
            Top3 top = e.getValue();
            if (!top.estaVacio()) {
                continue;
            }
            String juego = e.getKey().getNombreJuego();
            int base = switch (juego) {
                case "Conecta 4" -> 90;
                case "Memoria Numérica" -> 60;
                default -> 450; // Battleship
            };
            int paso = base / 5;
            top.registrar("Demo-Ana", base);
            top.registrar("Demo-Luis", base - paso);
            top.registrar("Demo-Sofía", base - 2 * paso);
        }
    }

    public void ejecutar() {
        try {
            int opcion;
            do {
                mostrarMenu();
                opcion = Entrada.leerEntero("Elige una opción: ", 1, 3);
                switch (opcion) {
                    case OPCION_LANZAR -> lanzarJuego();
                    case OPCION_ESTADISTICAS -> verEstadisticas();
                    default -> System.out.println("\n¡Hasta pronto!");
                }
            } while (opcion != OPCION_SALIR);
        } catch (EntradaCerradaException e) {
            System.out.println("\nEntrada cerrada. Saliendo de la consola.");
        }
    }

    private void mostrarMenu() {
        System.out.println("\n==============================");
        System.out.println("   CONSOLA DE VIDEOJUEGOS");
        System.out.println("==============================");
        System.out.println("1. Lanzar Juego");
        System.out.println("2. Ver Estadísticas");
        System.out.println("3. Salir");
    }

    private void lanzarJuego() {
        List<Jugable> juegos = new ArrayList<>(ranking.keySet());
        System.out.println("\n--- Juegos disponibles ---");
        for (int i = 0; i < juegos.size(); i++) {
            System.out.println((i + 1) + ". " + juegos.get(i).getNombreJuego());
        }
        System.out.println("0. Volver");

        int eleccion = Entrada.leerEntero("Elige un juego: ", 0, juegos.size());
        if (eleccion == 0) {
            return;
        }

        Jugable juego = juegos.get(eleccion - 1);      // polimorfismo
        int puntaje = juego.start();
        System.out.println("\nPuntaje obtenido en " + juego.getNombreJuego() + ": " + puntaje);
        procesarPuntaje(juego, puntaje);
    }

    private void procesarPuntaje(Jugable juego, int puntaje) {
        Top3 top = ranking.get(juego);
        if (!top.clasifica(puntaje)) {
            System.out.println("No alcanzaste el Top 3 de " + juego.getNombreJuego() + ".");
            return;
        }
        String nombre = juego.getNombreGanador();      // Battleship ya lo pidió
        if (nombre == null || nombre.isBlank()) {
            nombre = Entrada.leerLinea("🏆 ¡Entraste al Top 3! Ingresa tu nombre: ").trim();
        }
        if (nombre.isEmpty()) {
            nombre = "Anónimo";
        }
        int posicion = top.registrar(nombre, puntaje);
        System.out.println("¡" + nombre + " quedó en el puesto #" + posicion + "!");
    }

    private void verEstadisticas() {
        System.out.println("\n===== TOP 3 POR JUEGO =====");
        for (Map.Entry<Jugable, Top3> e : ranking.entrySet()) {
            System.out.println("\n" + e.getKey().getNombreJuego() + ":");
            e.getValue().imprimir();
        }
    }
}
