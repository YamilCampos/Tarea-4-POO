package main;

/**
 * Jugador mínimo requerido por {@code battleship.BattleshipGame}.
 * Esta clase NO pertenece al juego del compañero: es parte de la capa de
 * compatibilidad de la consola para satisfacer sus dependencias.
 */
public class Jugador {
    private String nickname;

    public Jugador(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
