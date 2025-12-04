package recuperatorioDic;

import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public class RecuperatorioDic {

    private static final String INVENTARIO_FILE = "inventario.csv";
    private static final String LOG_FILE = "errores.log";
    private static final Logger LOGGER = Logger.getLogger("RecuperatorioLogger");

    static {
        try {
            LOGGER.setUseParentHandlers(false);
            FileHandler fh = new FileHandler(LOG_FILE, true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            System.err.println("No se pudo inicializar el logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory(INVENTARIO_FILE);
        try {
            inventory.loadOrCreateWithSample();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar/crear el archivo de inventario", e);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;

        while (running) {
            try {
                System.out.println();
                System.out.println("--- MENU INVENTARIO ---");
                System.out.println("1) Mostrar productos");
                System.out.println("2) Agregar producto");
                System.out.println("3) Comprar/Vender producto");
                System.out.println("4) Buscar por marca");
                System.out.println("5) Salir (guardar y salir)");
                System.out.print("Elija una opción: ");
                String opt = reader.readLine();

                switch (opt) {
                    case "1" -> inventory.printAggregatedByName();
                    case "2" -> addProductFlow(reader, inventory);
                    case "3" -> buySellFlow(reader, inventory);
                    case "4" -> brandSearchFlow(reader, inventory);
                    case "5" -> {
                        inventory.saveToFile();
                        System.out.println("Cambios guardados. Saliendo...");
                        running = false;
                    }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error leyendo entrada de usuario", e);
                System.out.println("Ocurrió un error. Intente nuevamente.");
            }
        }
    }
	
    private static void addProductFlow(BufferedReader reader, Inventory inventory) {
        try {
            System.out.println("--- AGREGAR PRODUCTO ---");
            String name = promptNonEmpty(reader, "Nombre: ");
            String brand = promptNonEmpty(reader, "Marca: ");
            String category = promptNonEmpty(reader, "Categoría: ");
            int qty = promptInt(reader, "Cantidad en stock: ");
            double price = promptDouble(reader, "Precio: ");
            String date = promptDate(reader, "Fecha de alta (YYYY-MM-DD): ");

            Product p = new Product(name, brand, category, qty, price, date);
            inventory.addProduct(p);
            inventory.saveToFile();
            System.out.println("Producto agregado correctamente.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en agregar producto", e);
            System.out.println("No se pudo agregar el producto. Revise errores.log");
        }
    }

    private static void buySellFlow(BufferedReader reader, Inventory inventory) {
        try {
            System.out.println("--- COMPRAR / VENDER ---");
            String name = promptNonEmpty(reader, "Ingrese el nombre del producto: ");
            List<Product> matches = inventory.findByName(name);
            if (matches.isEmpty()) {
                System.out.println("No se encontraron productos con ese nombre.");
                return;
            }

            System.out.println("Productos encontrados:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.printf("%d) %s\n", i + 1, matches.get(i).toReadableString());
            }
            System.out.println("0) Operar sobre la suma total de todos los registros encontrados");
            int sel = promptIntInRange(reader, "Elija índice (0 para total): ", 0, matches.size());

            boolean isSelling = promptYesNo(reader, "¿Desea vender? (s/n) — si no, será una compra: ");
            int cantidad = promptInt(reader, "Cantidad a " + (isSelling ? "vender": "comprar") + ": ");

            boolean ok;
            if (sel == 0) {
                ok = inventory.adjustStockByNameAggregated(name, isSelling ? -cantidad : cantidad);
            } else {
                Product chosen = matches.get(sel - 1);
                ok = inventory.adjustStockByIndex(chosen, isSelling ? -cantidad : cantidad);
            }

            if (ok) {
                inventory.saveToFile();
                System.out.println("Operación realizada correctamente.");
            } else {
                System.out.println("No se pudo completar la operación (stock insuficiente o error). Revise errores.log si procede.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en flujo comprar/vender", e);
            System.out.println("Ocurrió un error. Revise errores.log");
        }
    }

    private static void brandSearchFlow(BufferedReader reader, Inventory inventory) {
        try {
            System.out.println("--- BUSCAR POR MARCA ---");
            String brand = promptNonEmpty(reader, "Marca a buscar: ");
            inventory.printByBrandAggregated(brand);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error en búsqueda por marca", e);
            System.out.println("Ocurrió un error. Revise errores.log");
        }
    }

    private static String promptNonEmpty(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = reader.readLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("No puede estar vacío.");
        }
    }

    private static int promptInt(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = reader.readLine();
            try {
                int v = Integer.parseInt(s.trim());
                if (v < 0) { System.out.println("Ingrese un número no negativo."); continue; }
                return v;
            } catch (Exception e) {
                System.out.println("Valor numérico inválido. Intente de nuevo.");
            }
        }
    }

    private static int promptIntInRange(BufferedReader reader, String prompt, int min, int max) throws IOException {
        while (true) {
            int v = promptInt(reader, prompt);
            if (v >= min && v <= max) return v;
            System.out.println("Valor fuera de rango. Intente nuevamente.");
        }
    }

    private static double promptDouble(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = reader.readLine();
            try {
                double d = Double.parseDouble(s.trim());
                if (d < 0) { System.out.println("Ingrese un número no negativo."); continue; }
                return d;
            } catch (Exception e) {
                System.out.println("Valor numérico inválido. Intente de nuevo.");
            }
        }
    }

    private static String promptDate(BufferedReader reader, String prompt) throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        while (true) {
            System.out.print(prompt);
            String s = reader.readLine();
            try {
                df.parse(s.trim());
                return s.trim();
            } catch (ParseException e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
        }
    }

    private static boolean promptYesNo(BufferedReader reader, String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String s = reader.readLine();
            if (s == null) continue;
            s = s.trim().toLowerCase();
            if (s.equals("s") || s.equals("si") || s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("Responda s/n.");
        }
    }

    static class Product {
        String name;
        String brand;
        String category;
        int quantity;
        double price;
        String date;

        Product(String name, String brand, String category, int quantity, double price, String date) {
            this.name = name;
            this.brand = brand;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
            this.date = date;
        }

        static Product fromCsvLine(String line) {
            String[] parts = line.split(";");
            if (parts.length < 6) return null;
            try {
                String name = parts[0].trim();
                String brand = parts[1].trim();
                String category = parts[2].trim();
                int qty = Integer.parseInt(parts[3].trim());
                double price = Double.parseDouble(parts[4].trim());
                String date = parts[5].trim();
                return new Product(name, brand, category, qty, price, date);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Linea CSV inválida: " + line, e);
                return null;
            }
        }

        String toCsvLine() {
            return String.join(";", name, brand, category, String.valueOf(quantity), String.valueOf(price), date);
        }

        String toReadableString() {
            return String.format("%s | %s | %s | qty=%d | price=%.2f | date=%s", name, brand, category, quantity, price, date);
        }
    }

    static class Inventory {
        private final List<Product> products = new ArrayList<>();
        private final Path filePath;

        Inventory(String filename) {
            this.filePath = Paths.get(filename);
        }

        void loadOrCreateWithSample() {
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                    List<String> sample = List.of(
                            "Laptop;HP;Electrónica;10;1500;2023-10-01",
                            "Smartphone;Samsung;Electrónica;15;1000;2023-10-02",
                            "Auriculares;Logitech;Audio;50;200;2023-10-03",
                            "Smartphone;Samsung;Electrónica;35;1300;2023-10-02"
                    );
                    Files.write(filePath, sample, StandardOpenOption.WRITE);
                    for (String l : sample) {
                        Product p = Product.fromCsvLine(l);
                        if (p != null) products.add(p);
                    }
                    return;
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "No se pudo crear inventario.csv", e);
                }
            }
            loadFromFile();
        }

        void loadFromFile() {
            products.clear();
            try (BufferedReader br = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    Product p = Product.fromCsvLine(line);
                    if (p != null) products.add(p);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al leer inventario.csv", e);
            }
        }

        void saveToFile() {
            try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Product p : products) {
                    bw.write(p.toCsvLine());
                    bw.newLine();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al guardar inventario.csv", e);
            }
        }

        void addProduct(Product p) {
            products.add(p);
        }

        void printAggregatedByName() {
            Map<String, Aggregated> map = new LinkedHashMap<>();
            for (Product p : products) {
                Aggregated ag = map.getOrDefault(p.name.toLowerCase(), new Aggregated(p.name, p.brand, p.category));
                ag.totalQty += p.quantity;
                ag.sumPrice += p.price;
                ag.countPrices += 1;
                map.put(p.name.toLowerCase(), ag);
            }

            System.out.println("---- LISTA DE PRODUCTOS (agregados por nombre) ----");
            System.out.printf("%-20s\t%-15s\t%-10s\t%-8s\t%-8s\n", "Nombre", "MarcaEjemplo", "Categoría", "Cantidad", "PrecioProm");
            for (Aggregated ag : map.values()) {
                double avgPrice = ag.countPrices > 0 ? ag.sumPrice / ag.countPrices : 0.0;
                System.out.printf("%-20s\t%-15s\t%-10s\t%-8d\t%-8.2f\n",
                        ag.name, ag.brand, ag.category, ag.totalQty, avgPrice);
            }
        }

        List<Product> findByName(String name) {
            List<Product> res = new ArrayList<>();
            for (Product p : products) {
                if (p.name.equalsIgnoreCase(name.trim())) res.add(p);
            }
            return res;
        }

        boolean adjustStockByNameAggregated(String name, int delta) {
            List<Product> matches = findByName(name);
            if (matches.isEmpty()) return false;
            int total = matches.stream().mapToInt(p -> p.quantity).sum();
            if (delta < 0 && total < -delta) {
                LOGGER.log(Level.WARNING, "Stock insuficiente para vender: pedido=" + (-delta) + ", disponible=" + total);
                return false;
            }
            if (delta >= 0) {
                matches.get(0).quantity += delta;
                return true;
            } else {
                int toRemove = -delta;
                for (Product p : matches) {
                    if (toRemove <= 0) break;
                    int take = Math.min(p.quantity, toRemove);
                    p.quantity -= take;
                    toRemove -= take;
                }
                return toRemove == 0;
            }
        }

        boolean adjustStockByIndex(Product productRef, int delta) {
            try {
                if (!products.contains(productRef)) {
                    Optional<Product> found = products.stream().filter(p -> p.name.equalsIgnoreCase(productRef.name)
                            && p.brand.equalsIgnoreCase(productRef.brand)
                            && p.date.equals(productRef.date)).findFirst();
                    if (found.isPresent()) productRef = found.get();
                    else return false;
                }
                if (delta < 0 && productRef.quantity < -delta) {
                    LOGGER.log(Level.WARNING, "Stock insuficiente en registro seleccionado. Pedido=" + (-delta) + ", disponible=" + productRef.quantity);
                    return false;
                }
                productRef.quantity += delta;
                if (productRef.quantity < 0) productRef.quantity = 0;
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error ajustando stock", e);
                return false;
            }
        }

        void printByBrandAggregated(String brandQuery) {
            Map<String, Aggregated> map = new LinkedHashMap<>();
            for (Product p : products) {
                if (!p.brand.equalsIgnoreCase(brandQuery.trim())) continue;
                String key = (p.brand + "|" + p.name).toLowerCase();
                Aggregated ag = map.getOrDefault(key, new Aggregated(p.name, p.brand, p.category));
                ag.totalQty += p.quantity;
                ag.sumPrice += p.price * p.quantity; // ponderar por cantidad para promedio ponderado
                ag.countPrices += p.quantity;
                map.put(key, ag);
            }

            if (map.isEmpty()) {
                System.out.println("No se encontraron productos para la marca: " + brandQuery);
                return;
            }

            System.out.println("---- RESULTADOS POR MARCA: " + brandQuery + " ----");
            System.out.printf("%-20s\t%-10s\t%-8s\n", "Nombre", "Cantidad", "PrecioProm");
            for (Aggregated ag : map.values()) {
                double avgPrice = ag.countPrices > 0 ? (ag.sumPrice / ag.countPrices) : 0.0;
                System.out.printf("%-20s\t%-10d\t%-8.2f\n", ag.name, ag.totalQty, avgPrice);
            }
        }

        static class Aggregated {
            String name;
            String brand;
            String category;
            int totalQty = 0;
            double sumPrice = 0.0;
            int countPrices = 0;

            Aggregated(String name, String brand, String category) {
                this.name = name;
                this.brand = brand;
                this.category = category;
            }
        }
    }
}
