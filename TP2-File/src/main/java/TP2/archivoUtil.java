package TP2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class archivoUtil {

    private static final String ARCHIVO = "Inventario.dat";

    public static void crearArchivo() {
        try {
            File file = new File(ARCHIVO);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println(consola.verde + "Archivo Inventario.dat creado" + consola.reset);
            }
        } catch (IOException e) {
            System.out.println(consola.rojo + "Error al crear el archivo" + consola.reset);
        }
    }

    public static void agregarProducto(String nombre, float compra, float venta, int stock) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, true))) {

            String linea = nombre + ";" + compra + ";" + venta + ";" + stock;
            bw.write(linea);
            bw.newLine();

            System.out.println(consola.verde + "Producto agregado correctamente" + consola.reset);

        } catch (IOException e) {
            System.out.println(consola.rojo + "Error al escribir archivo" + consola.reset);
        }
    }

    public static List<String> leerProductos() {
        List<String> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                lista.add(linea);
            }

        } catch (IOException e) {
            System.out.println(consola.rojo + "Error leyendo archivo" + consola.reset);
        }

        return lista;
    }

    public static void mostrarProductos() {
        List<String> productos = leerProductos();

        System.out.println(consola.amarillo + "\n\t===== INVENTARIO =====" + consola.reset);

        if (productos.isEmpty()) {
            System.out.println(consola.rojo + "No hay productos cargados." + consola.reset);
            return;
        }

        int i = 1;
        for (String p : productos) {
            String[] datos = p.split(";");

            System.out.println(consola.azul + "\nProducto #" + i++ + consola.reset);
            System.out.println("\tNombre: " + datos[0]);
            System.out.println("\tPrecio Compra: $" + datos[1]);
            System.out.println("\tPrecio Venta:  $" + datos[2]);
            System.out.println("\tStock: " + datos[3]);
        }
    }

    public static void eliminarProducto(int indice) {
        List<String> productos = leerProductos();

        if (indice < 1 || indice > productos.size()) {
            System.out.println(consola.rojo + "Indice invalido" + consola.reset);
            return;
        }

        productos.remove(indice - 1);
        guardarLista(productos);

        System.out.println(consola.verde + "Producto eliminado correctamente" + consola.reset);
    }

    public static void editarProducto(int indice, String nuevaLinea) {
        List<String> productos = leerProductos();

        if (indice < 1 || indice > productos.size()) {
            System.out.println(consola.rojo + "Indice invalido" + consola.reset);
            return;
        }

        productos.set(indice - 1, nuevaLinea);
        guardarLista(productos);

        System.out.println(consola.verde + "Producto editado correctamente" + consola.reset);
    }

    private static void guardarLista(List<String> productos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {

            for (String p : productos) {
                bw.write(p);
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println(consola.rojo + "Error reescribiendo archivo" + consola.reset);
        }
    }
}