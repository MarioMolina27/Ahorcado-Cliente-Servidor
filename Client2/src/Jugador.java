import java.io.Serializable;

public class Jugador implements Serializable {
    public String nomJugador;


    public String getNomJugador() {
        return nomJugador;
    }


    public void setNomJugador(String nomJugador) {
        this.nomJugador = nomJugador;
    }
    public Jugador(){}
    public Jugador(String nomJugador){
        this.nomJugador = nomJugador;
    }
}

