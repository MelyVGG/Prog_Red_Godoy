package ejercicio7;

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class MainEj7 {
	public static void main(String[] args) {
		String s = JOptionPane.showInputDialog(null, "Cantidad de empleados a cargar:");
		if (s == null) return;
		int n;
		try { n = Integer.parseInt(s.trim()); }
		catch (Exception e){ JOptionPane.showMessageDialog(null,"Entrada inválida"); return; }
		
		List<Empleado> lista = new ArrayList<>();
		
		for (int i = 0; i < n; i++) {
			String nombre = JOptionPane.showInputDialog(null, "Nombre del empleado " + (i+1)); if (nombre==null) return;
			String dia = JOptionPane.showInputDialog(null, "Día (ej: 2025-12-03) para " + nombre); if (dia==null) return;
			String hora = JOptionPane.showInputDialog(null, "Hora de ingreso (HH:mm) para " + nombre); if (hora==null) return;
			try { LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm")); }
			catch (Exception e){ JOptionPane.showMessageDialog(null,"Hora inválida."); i--; continue; }
			Empleado emp = new Empleado(nombre, dia, hora);
			lista.add(emp);
			
			LocalTime ingreso = LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm"));
			LocalTime limite = LocalTime.of(8,0);
			String estado = ingreso.isAfter(limite) ? "Tarde" : "Temprano";
			JOptionPane.showMessageDialog(null, nombre + " llegó: " + estado);
		}
		JOptionPane.showMessageDialog(null, "Se cargaron todos los empleados. Saliendo...");
	}
}
