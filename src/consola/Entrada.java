package consola;

import java.util.NoSuchElementException;
import java.util.Scanner;
import main.Pantalla;

/**
 * Lectura centralizada de teclado. Existe UN solo {@link Scanner} compartido
 * por toda la aplicación (incluido Battleship), porque varios Scanner sobre
 * System.in perderían datos en su buffer interno.
 */
public final class Entrada {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Entrada() { }

    public static Scanner scanner() {
        return SCANNER;
    }

    /** Muestra un mensaje y devuelve la línea escrita. */
    public static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        System.out.flush();
        try {
            return SCANNER.nextLine();
        } catch (NoSuchElementException e) {
            throw new EntradaCerradaException();
        }
    }

    /** Pide un entero en [min, max], repitiendo hasta que sea válido. */
    public static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            String texto = leerLinea(mensaje).trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
            } catch (NumberFormatException ignorada) {
                // se informa abajo
            }
            System.out.println("Entrada inválida. Ingresa un número entre " + min + " y " + max + ".");
        }
    }

    public static void pausar() {
        leerLinea("\nPresiona ENTER para continuar...");
    }

    public static void limpiarPantalla() {
        Pantalla.limpiar();
    }
}
