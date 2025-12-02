package guia1Files;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ClaseEjerciciosIO {
    private final BufferedReader console;

    public ClaseEjerciciosIO(BufferedReader console) {
        this.console = console;
    }

    // 1.a Crear un archivo de texto que guarde solo el último dato que el usuario escribe por consola.
    public void ejercicio1a() {
        try {
            System.out.println("EJ 1.a - Escribi lineas. Para finalizar ingresa 'FIN' en una linea nueva.");
            String ultima = null;
            while (true) {
                System.out.print("> ");
                String linea = console.readLine();
                if (linea == null) break;
                if ("FIN".equalsIgnoreCase(linea.trim())) break;
                if (!linea.trim().isEmpty()) ultima = linea;
            }
            if (ultima == null) {
                System.out.println("No se ingreso ningun dato valido.");
                return;
            }
            System.out.print("Ruta de archivo (se creara/reescribira): ");
            String ruta = console.readLine();
            try (PrintWriter pw = new PrintWriter(new FileWriter(ruta,false))) {
                pw.println(ultima);
            }
            System.out.println("Se guardo el ultimo dato: " + ultima);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.b Crear archivo que guarde TODOS los valores NUMERICOS que ingrese el usuario por consola, cada uno en un renglón.
    public void ejercicio1b() {
        try {
            System.out.println("EJ 1.b - Ingresa valores (para terminar escribi 'FIN'). Solo se guardan valores numericos.");
            List<String> numeros = new ArrayList<>();
            while (true) {
                System.out.print("> ");
                String linea = console.readLine();
                if (linea == null) break;
                if ("FIN".equalsIgnoreCase(linea.trim())) break;
                linea = linea.trim();
                try {
                    Double.parseDouble(linea);
                    numeros.add(linea);
                } catch (NumberFormatException ex) {
                    System.out.println("No es un numero. Se ignora.");
                }
            }
            if (numeros.isEmpty()) {
                System.out.println("No se ingresaron numeros.");
                return;
            }
            System.out.print("Ruta de archivo (se creara/reescribira): ");
            String ruta = console.readLine();
            try (PrintWriter pw = new PrintWriter(new FileWriter(ruta,false))) {
                for (String n : numeros) pw.println(n);
            }
            System.out.println("Se guardaron " + numeros.size() + " números en " + ruta);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.c Crear archivo numeros.txt (fuera de la carpeta del proyecto) que guarde los números pares desde 0 al 1000.
    public void ejercicio1c() {
        try {
            System.out.print("Ruta completa donde crear 'numeros.txt' (ej: C:/io/numeros.txt): ");
            String ruta = console.readLine();
            try (PrintWriter pw = new PrintWriter(new FileWriter(ruta,false))) {
                for (int i = 0; i <= 1000; i += 2) pw.println(i);
            }
            System.out.println("Archivo creado con pares 0..1000 en: " + ruta);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.d Utilizar numeros.txt y leer los valores para mostrarlos por la consola.
    public void ejercicio1d() {
        try {
            System.out.print("Ruta de 'numeros.txt' a leer: ");
            String ruta = console.readLine();
            try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
                String linea;
                System.out.println("Contenido de " + ruta + ":");
                while ((linea = br.readLine()) != null) {
                    System.out.println(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.e Borrar todos los renglones que contengan números múltiplos de 3 en numeros.txt
    public void ejercicio1e() {
        try {
            System.out.print("Ruta de 'numeros.txt' a editar: ");
            String ruta = console.readLine();
            Path origen = Path.of(ruta);
            Path temp = Files.createTempFile("numeros_temp", ".tmp");
            try (BufferedReader br = Files.newBufferedReader(origen);
                 PrintWriter pw = new PrintWriter(Files.newBufferedWriter(temp))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String t = linea.trim();
                    if (t.isEmpty()) continue;
                    try {
                        long val = Long.parseLong(t);
                        if (val % 3 == 0) {
                        } else {
                            pw.println(val);
                        }
                    } catch (NumberFormatException e) {
                        pw.println(linea);
                    }
                }
            }
            Files.move(temp, origen, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Se eliminaron los multiplos de 3 (si existian) en: " + ruta);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.f Tomar numeros.txt (con impares eliminados) y colocar en primos.dat todos los números que sean primos.
    public void ejercicio1f() {
        try {
            System.out.print("Ruta de 'numeros.txt' a leer (debe contener numeros): ");
            String rutaNumeros = console.readLine();
            System.out.print("Ruta de salida para 'primos.dat' (se creará): ");
            String rutaPrimos = console.readLine();

            List<Long> primos = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(rutaNumeros))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;
                    try {
                        long val = Long.parseLong(linea);
                        if (esPrimo(val)) primos.add(val);
                    } catch (NumberFormatException ignored) {}
                }
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(rutaPrimos,false))) {
                for (Long p : primos) pw.println(p);
            }
            System.out.println("Se encontraron " + primos.size() + " numeros primos y se guardaron en: " + rutaPrimos);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean esPrimo(long n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        long r = (long)Math.sqrt(n);
        for (long i = 3; i <= r; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // 1.g caracteres.dat, cargar 10 palabras que contienen 'ñ' mediante consola. Mostrar "Fichero original:" luego reemplazar 'ñ' -> 'nie-nio' y mostrar "Fichero arreglado:".
    public void ejercicio1g() {
        try {
            System.out.println("EJ 1.g - Ingresa 10 palabras que contengan la letra 'ñ'.");
            List<String> palabras = new ArrayList<>();
            while (palabras.size() < 10) {
                System.out.print("Palabra " + (palabras.size() + 1) + ": ");
                String linea = console.readLine();
                if (linea == null) break;
                linea = linea.trim();
                if (linea.isEmpty()) {
                    System.out.println("No puede ser vacia.");
                    continue;
                }
                if (!linea.toLowerCase().contains("ñ")) {
                    System.out.println("La palabra no contiene 'ñ'. Ingresá otra.");
                    continue;
                }
                palabras.add(linea);
            }
            System.out.print("Ruta de salida para 'caracteres.dat' (se creara): ");
            String ruta = console.readLine();
            try (PrintWriter pw = new PrintWriter(new FileWriter(ruta,false))) {
                for (String p : palabras) pw.println(p);
            }
            System.out.println("Fichero original:");
            palabras.forEach(System.out::println);

            List<String> arregladas = new ArrayList<>();
            for (String p : palabras) arregladas.add(p.replace("ñ", "nie-nio").replace("Ñ","Nie-Nio"));

            try (PrintWriter pw = new PrintWriter(new FileWriter(ruta,false))) {
                for (String p : arregladas) pw.println(p);
            }

            System.out.println("Fichero arreglado:");
            arregladas.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.h Crear un archivo HTML con un lorem (a mano), con un algoritmo abrir el archivo y borrar toda la leyenda de 'lorem' (interpretación: borrar ocurrencias de la palabra 'lorem' y párrafos que la contengan).
    public void ejercicio1h() {
        try {
            System.out.print("Ruta del archivo HTML a editar: ");
            String ruta = console.readLine();
            Path origen = Path.of(ruta);
            Path temp = Files.createTempFile("html_temp", ".tmp");
            try (BufferedReader br = Files.newBufferedReader(origen);
                 PrintWriter pw = new PrintWriter(Files.newBufferedWriter(temp))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.toLowerCase().contains("lorem")) {
                        continue;
                    } else {
                        pw.println(linea);
                    }
                }
            }
            Files.move(temp, origen, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Se eliminaron las lineas que contenian 'lorem' en: " + ruta);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1.i Registro de clima por fecha: permitir cargar, mostrar y borrar registro por fecha
    public void ejercicio1i() {
        try {
            System.out.print("Ruta del archivo de clima (se creara si no existe): ");
            String ruta = console.readLine();
            File fichero = new File(ruta);
            if (!fichero.exists()) {
                fichero.getParentFile().mkdirs();
                fichero.createNewFile();
            }

            while (true) {
                System.out.println("\n-- Submenu Clima --");
                System.out.println("1 - Agregar registro");
                System.out.println("2 - Mostrar todos los registros");
                System.out.println("3 - Borrar registro por fecha");
                System.out.println("0 - Volver al menu principal");
                System.out.print("Elegi: ");
                String opt = console.readLine();
                if (opt == null) break;
                switch (opt.trim()) {
                    case "1":
                        agregarRegistroClima(ruta);
                        break;
                    case "2":
                        mostrarRegistrosClima(ruta);
                        break;
                    case "3":
                        borrarRegistroClima(ruta);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Opcion invalida.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void agregarRegistroClima(String ruta) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta, true))) {
            System.out.println("Ingresar datos de clima. Fecha formato DD-MM-AAAA");
            System.out.print("Fecha: ");
            String fecha = console.readLine();
            if (fecha == null || fecha.trim().isEmpty()) {
                System.out.println("Fecha invalida.");
                return;
            }
            System.out.print("Temperatura (ej 23.5): ");
            String temp = console.readLine();
            System.out.print("Humedad (ej 75): ");
            String humedad = console.readLine();
            System.out.print("Descripcion (ej: soleado): ");
            String desc = console.readLine();
            pw.println(fecha.trim() + "|" + temp + "|" + humedad + "|" + desc);
            System.out.println("Registro agregado.");
        } catch (IOException e) {
            System.out.println("Error guardando: " + e.getMessage());
        }
    }

    private void mostrarRegistrosClima(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            System.out.println("Registros:");
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split("\\|");
                String fecha = partes.length > 0 ? partes[0] : "";
                String temp = partes.length > 1 ? partes[1] : "";
                String hum = partes.length > 2 ? partes[2] : "";
                String desc = partes.length > 3 ? partes[3] : "";
                System.out.println("Fecha: " + fecha + " | Temp: " + temp + " | Hum: " + hum + " | Desc: " + desc);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo registros: " + e.getMessage());
        }
    }

    private void borrarRegistroClima(String ruta) {
        try {
            System.out.print("Ingresa la fecha a borrar (DD-MM-AAAA): ");
            String fecha = console.readLine();
            if (fecha == null || fecha.trim().isEmpty()) {
                System.out.println("Fecha invalida.");
                return;
            }
            Path origen = Path.of(ruta);
            Path temp = Files.createTempFile("clima_temp", ".tmp");
            try (BufferedReader br = Files.newBufferedReader(origen);
                 PrintWriter pw = new PrintWriter(Files.newBufferedWriter(temp))) {
                String linea;
                boolean borrado = false;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] partes = linea.split("\\|");
                    String f = partes.length > 0 ? partes[0] : "";
                    if (f.equals(fecha.trim())) {
                        borrado = true;
                    } else {
                        pw.println(linea);
                    }
                }
                if (borrado) System.out.println("Registro con fecha " + fecha + " eliminado.");
                else System.out.println("No se encontro registro con fecha " + fecha);
            }
            Files.move(temp, origen, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
