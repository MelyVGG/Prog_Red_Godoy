package TP2;


public class Main {

    public static void main (String[] args) {

        archivoUtil.crearArchivo();

        int opcion = 0;

        do {
            System.out.println(consola.amarillo + "\n\t===== MENÚ INVENTARIO =====" + consola.reset);
            System.out.println("1 - Agregar producto");
            System.out.println("2 - Mostrar productos");
            System.out.println("3 - Eliminar producto");
            System.out.println("4 - Editar producto");
            System.out.println("5 - Salir");

            String input = consola.leerTexto("\nSeleccione una opción: ");

            if (numeroUtil.tipoNumero(input) != 1) {
                System.out.println(consola.rojo + "Opción inválida" + consola.reset);
                continue;
            }

            opcion = numeroUtil.convertirInt(input);

            switch (opcion) {
            case 1:
                agregarProducto();
                break;

            case 2:
                archivoUtil.mostrarProductos();
                break;

            case 3:
                eliminarProducto();
                break;

            case 4:
                editarProducto();
                break;

            case 5:
                System.out.println(consola.verde + "\nSaliendo del sistema..." + consola.reset);
                break;

            default:
                System.out.println(consola.rojo + "Opción incorrecta" + consola.reset);
        }

        } while (opcion != 5);
    }

    public static void agregarProducto() {

        String nombre = consola.leerTexto("\nIngrese nombre del producto: ");

        float compra = leerFloat("Ingrese precio de compra: ");
        float venta = leerFloat("Ingrese precio de venta: ");
        int stock = leerInt("Ingrese stock: ");

        archivoUtil.agregarProducto(nombre, compra, venta, stock);
    }

    public static void eliminarProducto() {
        archivoUtil.mostrarProductos();
        int indice = leerInt("\nIngrese número de producto a eliminar: ");
        archivoUtil.eliminarProducto(indice);
    }

    public static void editarProducto() {
        archivoUtil.mostrarProductos();

        int indice = leerInt("\nIngrese número de producto a editar: ");
        String nombre = consola.leerTexto("Nuevo nombre: ");
        float compra = leerFloat("Nuevo precio de compra: ");
        float venta = leerFloat("Nuevo precio de venta: ");
        int stock = leerInt("Nuevo stock: ");

        String linea = nombre + ";" + compra + ";" + venta + ";" + stock;
        archivoUtil.editarProducto(indice, linea);
    }

    public static float leerFloat(String msg) {
        while (true) {
            String texto = consola.leerTexto(msg);
            if (numeroUtil.tipoNumero(texto) == 2 || numeroUtil.tipoNumero(texto) == 1) {
                return numeroUtil.convertirFloat(texto);
            }
            System.out.println(consola.rojo + "Debe ser un número válido" + consola.reset);
        }
    }

    public static int leerInt(String msg) {
        while (true) {
            String texto = consola.leerTexto(msg);
            if (numeroUtil.tipoNumero(texto) == 1) {
                return numeroUtil.convertirInt(texto);
            }
            System.out.println(consola.rojo + "Debe ser un número entero" + consola.reset);
        }
    }
}