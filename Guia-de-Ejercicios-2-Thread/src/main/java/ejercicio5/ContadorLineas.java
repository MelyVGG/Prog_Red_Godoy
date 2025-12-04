package ejercicio5;

import java.io.*;
import java.nio.file.*;

public class ContadorLineas implements Runnable {
	private final Path archivo;
	private int lineas = 0;

	public ContadorLineas(Path archivo) {
		this.archivo = archivo;
	}

	@Override
	public void run() {
		try (BufferedReader br = Files.newBufferedReader(archivo)) {
			int cnt = 0;
			while (br.readLine() != null) cnt++;
			lineas = cnt;
			System.out.println(archivo.getFileName() + ": " + lineas + " renglones");
		} catch (IOException e) {
			System.err.println("Error leyendo " + archivo + ": " + e.getMessage());
		}
	}

	public int getLineas() { return lineas; }
}
