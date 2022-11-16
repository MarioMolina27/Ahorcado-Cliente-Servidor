import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    public static final int PORT = 5000;
    
    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Socket servidor obert esperant connexions....");
            List<Socket> socketsClients = new ArrayList<>();
            List<Jugador> jugadors = new ArrayList<>();

            boolean paraulaTrobada=false;
            String palabra = "hola";
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
                System.out.println("Torn del jugador: "+jugadors.get(i).getNomJugador());
                enviarParaulaTrobada(socketsClients.get(i),paraulaTrobada);
                enviarMissatge(socketsClients.get(i),"Es el teu torn");
                enviarParaulaActual(socketsClients.get(i),paraulaActual);
                enviarLletresUtilitzades(socketsClients.get(i),lletresUtilitzades);
                String lletra = rebreLletra(socketsClients.get(i));
                lletresUtilitzades.add(lletra);
                boolean lletraExisteix = comprobarExistenciaLletra(lletra,palabraEndevinar);
                if(lletraExisteix){
                    paraulaActual = marcarLletra(lletra,paraulaActual,palabraEndevinar);
                }

                enviarParaulaActual(socketsClients.get(i),paraulaActual);

                String paraula = rebreParaula(socketsClients.get(i));
                String[] paraulaIntroduida = paraula.split("");
                String[] paraulaActualTemp = paraulaActual;
                paraulaActual=paraulaIntroduida;

                if(Arrays.equals(palabraEndevinar,paraulaActual)){
                    paraulaTrobada=true;
                }
                else
                {
                    paraulaActual=paraulaActualTemp;
                }
                enviarParaulaTrobada(socketsClients.get(i),paraulaTrobada);
                i++;

            }while(!paraulaTrobada);

            int guanyador = i-1;
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

            System.out.println("Partida finalitzada\n"+"Guanyador: "+jugadors.get(guanyador).getNomJugador().toUpperCase());
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
    public static String rebreLletra(Socket socketClient) {
        String lletra="";
        try {
            InputStream is = socketClient.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            lletra = (String) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return lletra;
    }
    public static String rebreParaula(Socket socketClient) {
        String paraula="";
        try {
            InputStream is = socketClient.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            paraula = (String) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return paraula;
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