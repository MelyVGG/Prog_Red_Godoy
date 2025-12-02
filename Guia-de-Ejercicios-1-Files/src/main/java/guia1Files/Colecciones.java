package guia1Files;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Colecciones {
    private final BufferedReader console;

    public Colecciones(BufferedReader console) {
        this.console = console;
    }

    public void menuColecciones() {
        while (true) {
            try {
                System.out.println("\n-- Menu Colecciones --");
                System.out.println("1 - 2.a Leer enteros hasta -99 y mostrar suma/media y > media");
                System.out.println("2 - 2.b Clase Colegio (nacionalidades)");
                System.out.println("3 - 2.c Lista dias de la semana y operaciones");
                System.out.println("4 - 2.d Conjunto jugadores (ej Barça)");
                System.out.println("5 - 2.e Reglas de bolas de dos colores (generar apuesta)");
                System.out.println("0 - Volver");
                System.out.print("Elegi: ");
                String opt = console.readLine();
                if (opt == null) return;
                switch (opt.trim()) {
                    case "1":
                        ejercicio2a();
                        break;
                    case "2":
                        ejercicio2b();
                        break;
                    case "3":
                        ejercicio2c();
                        break;
                    case "4":
                        ejercicio2d();
                        break;
                    case "5":
                        ejercicio2e();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // 2.a
    public void ejercicio2a() throws IOException {
        System.out.println("Ingresa enteros. Para terminar ingresa -99 (no se guarda).");
        List<Integer> lista = new ArrayList<>();
        while (true) {
            System.out.print("> ");
            String l = console.readLine();
            if (l == null) break;
            try {
                int v = Integer.parseInt(l.trim());
                if (v == -99) break;
                lista.add(v);
            } catch (NumberFormatException e) {
                System.out.println("No es entero. Intenta de nuevo.");
            }
        }
        if (lista.isEmpty()) {
            System.out.println("No se ingresaron valores.");
            return;
        }
        int suma = lista.stream().mapToInt(Integer::intValue).sum();
        double media = (double) suma / lista.size();

        System.out.println("Cantidad leida: " + lista.size());
        System.out.println("Suma: " + suma);
        System.out.println("Media: " + media);
        long mayores = lista.stream().filter(x -> x > media).count();
        System.out.println("Cantidad de valores mayores que la media: " + mayores);
        System.out.println("Valores leidos: " + lista);
    }

    // 2.b Colegio nacionalidades
    public void ejercicio2b() throws IOException {
        Map<String, Integer> map = new HashMap<>();
        while (true) {
            System.out.println("\n-- Colegio (nacionalidades) --");
            System.out.println("1 - Añadir alumno (se pide nacionalidad)");
            System.out.println("2 - Mostrar todas las nacionalidades y cantidad");
            System.out.println("3 - Mostrar una nacionalidad especifica");
            System.out.println("4 - Cantidad de nacionalidades distintas");
            System.out.println("5 - Borrar todos los datos");
            System.out.println("0 - Volver");
            System.out.print("Elegi: ");
            String opt = console.readLine();
            if (opt == null) return;
            switch (opt.trim()) {
                case "1":
                    System.out.print("Ingresar nacionalidad: ");
                    String n = console.readLine();
                    if (n == null || n.trim().isEmpty()) {
                        System.out.println("Valor invalido.");
                    } else {
                        map.put(n, map.getOrDefault(n, 0) + 1);
                        System.out.println("Agregado.");
                    }
                    break;
                case "2":
                    System.out.println("Nacionalidades y cantidad:");
                    map.forEach((k,v)-> System.out.println(k + " -> " + v));
                    break;
                case "3":
                    System.out.print("Ingresar nacionalidad a consultar: ");
                    String q = console.readLine();
                    if (q == null) break;
                    System.out.println(q + " -> " + map.getOrDefault(q, 0));
                    break;
                case "4":
                    System.out.println("Cantidad de nacionalidades distintas: " + map.size());
                    break;
                case "5":
                    map.clear();
                    System.out.println("Datos borrados.");
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    // 2.c Lista días semana y operaciones
    public void ejercicio2c() {
        List<String> dias = new ArrayList<>(List.of("Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"));
        System.out.println("Lista original: " + dias);
        dias.add(3, "Juernes");
        List<String> listaDos = new ArrayList<>(dias);
        dias.addAll(listaDos);
        System.out.println("Despues de modificaciones: " + dias);
        try {
            System.out.println("Posicion 3: " + dias.get(2));
            System.out.println("Posicion 4: " + dias.get(3));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No existen esas posiciones.");
        }
        System.out.println("Primer elemento: " + dias.get(0));
        System.out.println("Ultimo elemento: " + dias.get(dias.size()-1));
        boolean removed = dias.remove("Juernes");
        System.out.println("Se intento eliminar 'Juernes'. Resultado: " + removed);
        System.out.println("Mostrando uno a uno con Iterator:");
        Iterator<String> it = dias.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        boolean existeLunes = dias.stream().anyMatch(s -> s.equalsIgnoreCase("Lunes"));
        System.out.println("¿Existe 'Lunes'? " + existeLunes);
        dias.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("Lista ordenada: " + dias);
    }

    // 2.d Conjunto jugadores
    public void ejercicio2d() {
        Set<String> jugadores = new HashSet<>(List.of("Jordi Alba","Pique","Busquets","Iniesta","Messi"));
        System.out.println("Jugadores iniciales:");
        for (String j : jugadores) System.out.println(j);
        System.out.println("¿Existe Neymar JR? " + jugadores.contains("Neymar JR"));
        Set<String> jugadores2 = new HashSet<>(List.of("Pique","Busquets"));
        boolean todos = jugadores.containsAll(jugadores2);
        System.out.println("¿Todos los de jugadores2 estan en jugadores? " + todos);
        Set<String> union = new HashSet<>(jugadores);
        union.addAll(jugadores2);
        System.out.println("Union de conjuntos: " + union);
        boolean agregado = jugadores.add("Pique");
        System.out.println("Intento agregar 'Pique' a 'jugadores'. Se agregó? " + agregado);
        System.out.println("Estado final de 'jugadores': " + jugadores);
    }

    // 2.e Reglas de bolas de dos colores: generar combinación aleatoria
    public void ejercicio2e() {
        Random rnd = new Random();
        Set<Integer> rojas = new HashSet<>();
        while (rojas.size() < 6) {
            int num = rnd.nextInt(33) + 1; // 1..33
            rojas.add(num);
        }
        int azul = rnd.nextInt(16) + 1; // 1..16
        System.out.println("Bolas rojas (6): " + rojas);
        System.out.println("Bola azul (1): " + azul);
    }
}
