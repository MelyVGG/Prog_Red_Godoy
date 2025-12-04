package recuperatorioDic;

import java.io.BufferedReader;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class RecuperatorioDic {
	private static final String inventario_file = "inventario.csv";
	private static final String  log_file = "errores.log";
	private static final Logger logger = logger.getLogger("RecuperatorioLogger");
	
	static {
		try {
			logger.setUseParentHandlers(false);
			FileHandler fh = new FileHandler(log_file, true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		} catch (IOException e ) {
			System.err.println("No se pudo inicializar el logger: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Inventory inventory = new Inventory(inventario_file);
		try {
			inventory.loadOrCreateWithSample();
		} catch (Exception e) {
			logger.log(Level.severe, "Error al cargar/crear el archivo de inventario", e);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean running = true;
		
		while (running) {
			try {
				System.out.println();
				System.out.println("MENU INVENTARIO");
				System.out.println("1. Mostrar productos");
				System.out.println("2. Agregar productos");
				System.out.println("3. Comprar/Vender productos");
				System.out.println("4. Buscar por marca");
				System.out.println("5. Salir (guardar y salir)");
				System.out.println("Elija una opcion: ");
				String opt = reader.readLine();
				
				switch (opt) {
				case "1" -> inventory.printAggregateByName();
				case "2" -> addProductFlow(reader, inventory);
				case "3" -> buySellFlow(reader, inventory);
				case "4" -> brandSearchFlow(reader, inventory);
				case "5" ->{
					inventory.saveToFile();
					System.out.println("Cambios guardados. Saliendo...");
					running = false;
				}
				default -> System.out.println("Opcion invalida. Intentando nuevamente");
				}
			} catch (IOException e) {
				logger.log(Level.severe, "Error leyendo entrada de usuario", e);
				System.out.println("Ocurrio un error. Intente nuevamente.");
			}
		}
	}
	
	private static void addProductFlow(BufferedReader reader, Inventory inventory) {
		try {
			System.out.println("")
		}
	}
}
