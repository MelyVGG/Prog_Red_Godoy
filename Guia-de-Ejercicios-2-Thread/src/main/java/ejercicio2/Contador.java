package ejercicio2;

import javax.swing.*;

public class Contador implements Runnable {
	private final String nombre;
	private final int limite;
	private final int delay;
	private int contador = 0;
	
	public Contador(String nombre, int limite, int delay) {
		this.nombre = nombre;
		this.limite = limite;
		this.delay = delay;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
			while (contador < limite) {
				Thread.sleep(delay);
				contador++;
			}
		} catch (InterruptedException e) { Thread.currentThread().interrupt(); }
		long elapsed = System.currentTimeMillis() - start;
		JOptionPane.showMessageDialog(null, String.format("Contador %s terminó en %d ms. Límite: %d", nombre, elapsed, limite));
	}
}