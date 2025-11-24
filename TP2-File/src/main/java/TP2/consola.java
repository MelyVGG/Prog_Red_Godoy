package TP2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class consola {

    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static final String reset = "\033[0m";
    public static final String rojo = "\033[31m";
    public static final String verde = "\033[32m";
    public static final String amarillo = "\033[33m";
    public static final String azul = "\033[34m";
    public static final String cyan = "\033[36m";

    public static String leerTexto(String mensaje) {
        try {
            System.out.print(cyan + mensaje + reset);
            return br.readLine();
        } catch (IOException e) {
            System.out.println(rojo + "Error leyendo desde consola" + reset);
            return "";
        }
    }
}