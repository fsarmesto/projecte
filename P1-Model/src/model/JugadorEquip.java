package model;

/**
 *
 * @author Ferran Sanchis Armesto
 */
public final class JugadorEquip {
    private Jugador jugador;
    private boolean esTitular;

    public JugadorEquip(Jugador jugador, boolean esTitular) {
        this.jugador = jugador;
        this.esTitular = esTitular;
    }

    public JugadorEquip() {
    }

    public void setEsTitular(boolean esTitular) {
        this.esTitular = esTitular;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
    
    protected Jugador getJugador() {
        return jugador;
    }
}
