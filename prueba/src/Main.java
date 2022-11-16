import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String palabra = "hola";
        String[] palabraEndevinar = palabra.split("");
        String[] paraulaActual = new String[palabra.length()];
        List<String> lletresUtilitzades = new ArrayList<>();
        boolean paraulaTrobada=false;

        for (int i =0;i<paraulaActual.length;i++){
            paraulaActual[i]="_";
        }


        do{
            mostrarLletresUtilitzades(lletresUtilitzades);
            System.out.print("Introduce una letra: ");
            String lletra = new Scanner(System.in).nextLine();
            lletresUtilitzades.add(lletra);
            boolean lletraExisteix = comprobarExistenciaLletra(lletra,palabraEndevinar);
            if(lletraExisteix){
                paraulaActual = marcarLletra(lletra,paraulaActual,palabraEndevinar);
                mostrarParaulaActual(paraulaActual);
            }

            if(Arrays.equals(palabraEndevinar,paraulaActual)){
                paraulaTrobada=true;
            }
        }while(!paraulaTrobada);


    }

    public static void mostrarParaulaActual(String[]arr){
        for (int i =0;i< arr.length;i++){
            System.out.print(arr[i]);
        }
        System.out.println();
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

    public static void mostrarLletresUtilitzades (List<String> lletresIncorrectes){
        System.out.print("Lletres utilitzades: ");
        for (String err:lletresIncorrectes) {
            System.out.print(err+" ");
        }
        System.out.println();

    }
}