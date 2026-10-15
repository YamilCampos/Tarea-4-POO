package main;

import java.util.Scanner;

/**
 * Utilidades de pantalla requeridas por {@code battleship.BattleshipGame}.
 * Simula la limpieza de consola (secuencia ANSI + saltos de línea) para que
 * funcione también en terminales que no interpretan ANSI.
 */
public class Pantalla {

    private Pantalla() { }

    /** Limpia (u oculta) la pantalla actual. */
    public static void limpiar() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            sb.append((char) 10);
        }
        sb.append("\033[H\033[2J");
        System.out.print(sb);
        System.out.flush();
    }

    /** Pausa hasta que el usuario presione ENTER. */
    public static void pausar(Scanner scanner) {
        System.out.print("\nPresiona ENTER para continuar...");
        System.out.flush();
        scanner.nextLine();
    }
}
