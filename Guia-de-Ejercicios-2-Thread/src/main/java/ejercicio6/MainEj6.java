package ejercicio6;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class MainEj6 {
	public static void main(String[] args) throws InterruptedException {
		List<Alumno> alumnos = new ArrayList<>();
		alumnos.add(new Alumno("Juan","Perez"));
		alumnos.add(new Alumno("Ana","Gomez"));
		alumnos.add(new Alumno("Luis","Diaz"));
		alumnos.add(new Alumno("Marta","Lopez"));
		
		Thread preceptor = new Thread(new Preceptor(alumnos));
		Thread docente = new Thread(new Docente(alumnos));
		
		preceptor.start();
		docente.start();
		
		preceptor.join();
		docente.join();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Alumnos eximidos (asistencia < 75%):\n");
		for (Alumno a : alumnos) {
			if (!a.esAlumnoRegular) sb.append(String.format("%s %s - Nota final: %.2f - Asist: %.1f%%\n", a.nombre,a.apellido,a.promedioNotas(),a.porcentajeAsistencia()));
			}
		JOptionPane.showMessageDialog(null, new JTextArea(sb.toString()));
	}
}