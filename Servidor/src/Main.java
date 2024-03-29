import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.*;

public class Main {
    public static final int PORT = 5000;
    
    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.print("Introdueix la paraula a buscar pels jugadors: ");
            String palabra = new Scanner (System.in).nextLine().toLowerCase();
            System.out.println("Socket servidor obert esperant connexions....");
            List<Socket> socketsClients = new ArrayList<>();
            List<Jugador> jugadors = new ArrayList<>();

            boolean paraulaTrobada=false;
            String[] palabraEndevinar = palabra.split("");
            String[] paraulaActual = new String[palabra.length()];
            List<String> lletresUtilitzades = new ArrayList<>();

            for (int i =0;i<paraulaActual.length;i++){
                paraulaActual[i]="_";
            }

            System.out.print("Numero de clients que es connectaran: ");
            int numClients = new Scanner(System.in).nextInt();

            System.out.println("Esperant que tots els clients es connectin");
            for (int i = 0; i < numClients; i++) {
                socketsClients.add(server.accept());
                enviarNumeroConnexio(socketsClients.get(i),i);
                System.out.println("Client Connectat");
            }

            for (int i = 0; i < numClients; i++) {
                jugadors.add(rebreJugador(socketsClients.get(i)));
            }

            System.out.println("Tots els clients s'han connectat correctament");

            int i=0;
            do{
                if(numClients==i){
                    i=0;
                }
                String tornActual = "Torn del jugador: "+jugadors.get(i).getNomJugador().toUpperCase();
                System.out.println(tornActual);
                enviarParaulaTrobada(socketsClients.get(i),paraulaTrobada); //Envia l'estat de si la paraula s'ha trobat
                enviarMissatge(socketsClients.get(i),"Es el teu torn");
                enviarParaulaActual(socketsClients.get(i),paraulaActual);
                enviarLletresUtilitzades(socketsClients.get(i),lletresUtilitzades);
                String lletra = rebreString(socketsClients.get(i)); // Rep la lletra introduida per un usuari
                lletresUtilitzades.add(lletra);
                boolean lletraExisteix = comprobarExistenciaLletra(lletra,palabraEndevinar);
                if(lletraExisteix){
                    paraulaActual = marcarLletra(lletra,paraulaActual,palabraEndevinar);
                }

                enviarParaulaActual(socketsClients.get(i),paraulaActual);

                if(Arrays.equals(palabraEndevinar,paraulaActual)){
                    paraulaTrobada=true;
                }
                enviarParaulaTrobada(socketsClients.get(i),paraulaTrobada);
                if(!paraulaTrobada)
                {
                    String[] paraulaTemporal = rebreString(socketsClients.get(i)).split(""); // Rep la paraula introduida pel jugador
                    if(Arrays.equals(palabraEndevinar,paraulaTemporal)){
                        paraulaTrobada=true;
                    }
                }
                enviarParaulaTrobada(socketsClients.get(i),paraulaTrobada);
                i++;

            }while(!paraulaTrobada);

            int guanyador = i-1;
            System.out.println("LA PARTIDA HA FINALITZAT");
            System.out.println("EL GUANYADOR ES "+jugadors.get(guanyador).getNomJugador().toUpperCase());

            for(int j =0;j<numClients;j++)
            {
                if (j==guanyador)
                {
                    enviarMissatge(socketsClients.get(j),"ETS EL GUANYADOR");
                }
                else if(j!=guanyador)
                {
                    enviarParaulaTrobada(socketsClients.get(j),true);
                    enviarMissatge(socketsClients.get(j),"EL GUANYADOR ES "+jugadors.get(guanyador).getNomJugador().toUpperCase());
                }
            }

            server.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static Jugador rebreJugador(Socket socketClient) {
        Jugador jugador = new Jugador();
        try {
            InputStream is = socketClient.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            jugador = (Jugador) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return jugador;
    }
    /**
     * Funcio que rep un string (una lletra o una paraula)
     */

    public static String rebreString(Socket socketClient) {
        String cadena="";
        try {
            InputStream is = socketClient.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            cadena = (String) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return cadena;
    }


    public static void enviarNumeroConnexio(Socket socket, int numConnexio) {
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream oos = new DataOutputStream(os);
            oos.writeInt(numConnexio);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public static void enviarMissatge(Socket socket, String msg) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(msg);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    /**
     * Funció que envia l'estat de la paraula amb les lletres marcades (les que ha  sigut endevinades pels usuaris)
     */
    public static void enviarParaulaActual(Socket socket, String[] paraula) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(paraula);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    public static void enviarLletresUtilitzades(Socket socket, List<String> lletresUtilitzades) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(lletresUtilitzades);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    /**
     * Envia als usuaris una variable booleana que indica si la paraula s'ha trobat
     * */
    public static void enviarParaulaTrobada(Socket socket, boolean paraulaTrobada) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(paraulaTrobada);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static boolean comprobarExistenciaLletra(String lletra,String[]arr){
        boolean existeix = false;
        for (String cha:arr) {
            if(cha.equals(lletra)){
                existeix=true;
            }
        }
        return existeix;
    }

    public static String[] marcarLletra(String lletra,String[]paraulaActual,String[]paraulaEndevinar){
        for(int i =0;i<paraulaActual.length;i++){
            if(paraulaEndevinar[i].equals(lletra)){
                paraulaActual[i]=lletra;
            }
        }
        return paraulaActual;
    }


}