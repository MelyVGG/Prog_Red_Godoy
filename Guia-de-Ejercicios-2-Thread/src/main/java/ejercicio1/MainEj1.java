package ejercicio1;

import javax.swing.*;

public class MainEj1 {
	public static void main(String[] args) {
		while (true) {
			String input = JOptionPane.showInputDialog(null, "Ingrese tipo (1=numeros,2=letras) o Cancelar para salir:");
			if (input == null) System.exit(0);
			try {
				int tipo = Integer.parseInt(input.trim());
				if (tipo != 1 && tipo != 2) throw new NumberFormatException();
				
				HiloAlfanumerico hilo = new HiloAlfanumerico(tipo);
				Thread t = new Thread(hilo, "HiloAlfa-" + tipo);
				t.start();
				
				JOptionPane.showMessageDialog(null, "Hilo iniciado. Presione OK para detenerlo.");
				hilo.stop();
				t.interrupt();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Entrada invalida. Intente de nuevo.");
			}
		}
	}
}