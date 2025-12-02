package guia1Files;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        ClaseEjerciciosIO ejercicios = new ClaseEjerciciosIO(console);
        Colecciones colecciones = new Colecciones(console);

        while (true) {
            try {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1  - Ejercicio 1.a (guardar ultimo dato en archivo)");
                System.out.println("2  - Ejercicio 1.b (guardar todos los valores numericos ingresados)");
                System.out.println("3  - Ejercicio 1.c (crear numeros.txt con pares 0-1000)");
                System.out.println("4  - Ejercicio 1.d (leer numeros.txt y mostrar)");
                System.out.println("5  - Ejercicio 1.e (borrar renglones multiplos de 3 de numeros.txt)");
                System.out.println("6  - Ejercicio 1.f (extraer primos a primos.dat)");
                System.out.println("7  - Ejercicio 1.g (caracteres.dat con 'ñ' y reemplazo)");
                System.out.println("8  - Ejercicio 1.h (editar HTML: borrar leyenda 'lorem')");
                System.out.println("9  - Ejercicio 1.i (registro de clima: agregar/mostrar/borrar)");
                System.out.println("10 - Ejercicios de colecciones (2.a - 2.e)");
                System.out.println("0  - Salir");
                System.out.print("Elegi una opcion: ");

                String line = console.readLine();
                if (line == null || line.trim().isEmpty()) {
                    System.out.println("Opcion vacia. Ingresa un numero.");
                    continue;
                }
                int opcion;
                try {
                    opcion = Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Formato invalido. Ingresa un numero.");
                    continue;
                }

                switch (opcion) {
                    case 0:
                        System.out.println("Saliendo. ¡Chau!");
                        return;
                    case 1:
                        ejercicios.ejercicio1a();
                        break;
                    case 2:
                        ejercicios.ejercicio1b();
                        break;
                    case 3:
                        ejercicios.ejercicio1c();
                        break;
                    case 4:
                        ejercicios.ejercicio1d();
                        break;
                    case 5:
                        ejercicios.ejercicio1e();
                        break;
                    case 6:
                        ejercicios.ejercicio1f();
                        break;
                    case 7:
                        ejercicios.ejercicio1g();
                        break;
                    case 8:
                        ejercicios.ejercicio1h();
                        break;
                    case 9:
                        ejercicios.ejercicio1i();
                        break;
                    case 10:
                        colecciones.menuColecciones();
                        break;
                    default:
                        System.out.println("Opcion no reconocida.");
                }

            } catch (Exception ex) {
                System.out.println("Ocurrio un error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
