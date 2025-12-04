package ejercicio2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainEj2 {
	public static void main(String[] args) throws InterruptedException {
		Random r = new Random();
		int cantidad = 3 + r.nextInt(8);
		List<Thread> threads = new ArrayList<>();
		
		for (int i = 1; i <= cantidad; i++) {
			int limite = 5 + r.nextInt(16);
			int delay = 200 + r.nextInt(801);
			Contador c = new Contador("C-" + i, limite, delay);
			Thread t = new Thread(c);
			threads.add(t);
			t.start();
		}
		
		for (Thread t : threads) t.join();
		JOptionPane.showMessageDialog(null, "Todos los contadores finalizaron.");
	}
}
