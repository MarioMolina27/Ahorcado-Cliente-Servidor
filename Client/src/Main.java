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
            //String ip = new Scanner(System.in).nextLine();
            String ip = "localhost";
            Socket socket = new Socket(ip,PORT);
            System.out.println("Connexió establerta amb el servidor");
            int numConnexio = rebreNumeroConnexio(socket);
            System.out.println("Numero de connexio: "+numConnexio);

            System.out.print("Introdueix el teu nom: ");
            String nom = new Scanner(System.in).nextLine();
            espaiarLinies();

            Jugador jugador = new Jugador(nom);
            enviarJugador(socket,jugador);

            String[] paraulaActual = new String[0];
            List<String> lletresUtilitzades = new ArrayList<>();
            boolean paraulaTrobada=false;
            do{
                paraulaTrobada = rebreParaulaTrobada(socket);
                if(!paraulaTrobada)
                {
                    System.out.println(rebreMissatge(socket));
                    paraulaActual = rebreParaulaActual(socket,paraulaActual);
                    mostrarParaulaActual(paraulaActual);
                    lletresUtilitzades = rebreLletresUtilitzades(socket);
                    mostrarLletresUtilitzades(lletresUtilitzades);
                    System.out.print("Introduce una letra: ");
                    String lletra = new Scanner(System.in).nextLine();
                    enviarLletra(socket,lletra);
                    paraulaActual = rebreParaulaActual(socket,paraulaActual);
                    mostrarParaulaActual(paraulaActual);
                    paraulaTrobada = rebreParaulaTrobada(socket);
                }
                espaiarLinies();
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
    public static void enviarLletra(Socket socket, String lletra) {
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(lletra);

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