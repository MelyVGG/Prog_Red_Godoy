package ejercicio5;

import javax.swing.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class MainEj5 {
	public static void main(String[] args) throws Exception {
		String carpeta = JOptionPane.showInputDialog(null, "Ingrese carpeta a analizar (ruta absoluta o relativa):");
		if (carpeta == null) return;
		Path dir = Paths.get(carpeta);
		
		if (!Files.isDirectory(dir)) { JOptionPane.showMessageDialog(null, "Carpeta inválida."); return; }

		List<Path> archivos = new ArrayList<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.txt")) {
			for (Path p : ds) archivos.add(p);
		}

		if (archivos.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No se encontraron archivos .txt en la carpeta.");
			return;
		}

		List<ContadorLineas> contadores = new ArrayList<>();
		List<Thread> threads = new ArrayList<>();

		for (Path p : archivos) {
			ContadorLineas c = new ContadorLineas(p);
			Thread t = new Thread(c, "Contador-" + p.getFileName());
			contadores.add(c);
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) t.join();
		int totalLineas = 0;
		StringBuilder detalle = new StringBuilder();
		for (int i = 0; i < archivos.size(); i++) {
			String nombre = archivos.get(i).getFileName().toString();
			int l = contadores.get(i).getLineas();
			detalle.append(nombre).append(": ").append(l).append("renglones");
			totalLineas += l;
		}
		JOptionPane.showMessageDialog(null, String.format("Archivos: %d Hilos: %d Total renglones: %d Detalle:%s",
				archivos.size(), threads.size(), totalLineas, detalle.toString()));
	}
}