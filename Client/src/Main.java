import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final int PORT = 5000;
    public static final int ESPAIS = 5;


    public static void main(String[] args)
    {
        try
        {
            System.out.print("IP del servidor: ");
            String ip = new Scanner(System.in).nextLine();
            Socket socket = new Socket(ip,PORT);
            System.out.println("Connexió establerta amb el servidor");
            int numConnexio = rebreNumeroConnexio(socket);
            System.out.println("Numero de connexio: "+numConnexio);

            System.out.print("Introdueix el teu nom: ");
            String nom = new Scanner(System.in).nextLine();
            System.out.println("Esperant que la resta de jugadors es connectin...");
            espaiarLinies();

            Jugador jugador = new Jugador(nom);
            enviarJugador(socket,jugador);
            System.out.println("Esperant el teu torn.......");

            String[] paraulaActual = new String[0]; //Variable que indicara quines lletres s'han trobat i les marcara en la seva posició de la paraula escollida pel servidor
            List<String> lletresUtilitzades = new ArrayList<>();
            boolean paraulaTrobada=false; // Variable booleana per comprobar si la paraula s'ha trobat
            do{
                paraulaTrobada = rebreParaulaTrobada(socket);
                if(!paraulaTrobada)
                {
                    espaiarLinies();
                    System.out.println(rebreMissatge(socket));
                    paraulaActual = rebreParaulaActual(socket,paraulaActual);
                    mostrarParaulaActual(paraulaActual);
                    lletresUtilitzades = rebreLletresUtilitzades(socket);
                    mostrarLletresUtilitzades(lletresUtilitzades);
                    System.out.print("Introduce una letra: ");
                    String lletra = new Scanner(System.in).nextLine();
                    enviarString(socket,lletra); //Enviem la lletra al servidor
                    paraulaActual = rebreParaulaActual(socket,paraulaActual);
                    mostrarParaulaActual(paraulaActual);
                    paraulaTrobada=rebreParaulaTrobada(socket);
                    if(!paraulaTrobada)
                    {
                        System.out.print("Introdueix quina paraula creus que es: ");
                        String paraula = new Scanner(System.in).nextLine();
                        enviarString(socket,paraula); // Enviem la posible paraula al servidor
                    }
                    paraulaTrobada = rebreParaulaTrobada(socket);
                }
                espaiarLinies();
                System.out.println("Esperant el teu torn.......");
            }while(!paraulaTrobada);

            System.out.println("LA PARTIDA HA FINALITAT");
            System.out.println(rebreMissatge(socket));

            socket.close();
            System.out.println("Connexió tancada amb el servidor");
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }
    public static int rebreNumeroConnexio(Socket socket)
    {
        int numConnexio = -1;
        try
        {
            InputStream is = socket.getInputStream();
            DataInputStream ois = new DataInputStream(is);
            numConnexio = (int) ois.readInt()+1;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return numConnexio;
    }

    public static void enviarJugador(Socket socket, Jugador jugador) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(jugador);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    /**
    * Funcio que podra enviar un string que serà una lletra o una paraula
    * */
    public static void enviarString(Socket socket, String cadena) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(cadena);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static String rebreMissatge(Socket socket) {
        String msg="";
        try {
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            msg = (String) ois.readObject();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return msg;
    }
    /**
    * Funció que rebrà l'estat de la paraula (amb les lletres trobades marcades)
    * */
    public static String[] rebreParaulaActual(Socket socket,String[]paraulaActual) {
        try {
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            paraulaActual = (String[]) ois.readObject();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return paraulaActual;
    }
    /**
    * Funció per agafar del servidor la variable booleana del servidor que ens indica si la paraula s'ha trobat
    * */
    public static boolean rebreParaulaTrobada(Socket socket) {
        boolean paraulaTrobada=false;
        try {
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            paraulaTrobada = (boolean) ois.readObject();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return paraulaTrobada;
    }
    /**
    * Funció que rebra del server una llista amb totes les lletres utilitzades per tots els jugadors
    * */
    public static List<String> rebreLletresUtilitzades(Socket socket) {
        List<String> lletresUtilitzades= new ArrayList<>();
        try {
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            lletresUtilitzades = (List<String>) ois.readObject();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return lletresUtilitzades;
    }
    public static void mostrarParaulaActual(String[]arr){
        for (int i =0;i< arr.length;i++){
            System.out.print(arr[i]);
        }
        System.out.println();
    }
    public static void mostrarLletresUtilitzades (List<String> lletresIncorrectes){
        System.out.print("Lletres utilitzades: ");
        for (String err:lletresIncorrectes) {
            System.out.print(err+" ");
        }
        System.out.println();

    }
    public static void espaiarLinies()
    {
        for(int i =0;i<ESPAIS;i++)
        {
            System.out.println();
        }
    }
}