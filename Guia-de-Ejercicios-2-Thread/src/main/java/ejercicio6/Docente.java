package ejercicio6;

import java.util.List;
import java.util.Random;

public class Docente implements Runnable {
	private final List<Alumno> alumnos;
	public Docente(List<Alumno> alumnos) { this.alumnos = alumnos; }
	
	@Override
	public void run() {
		Random r = new Random();
		for (Alumno a : alumnos) {
			for (int i = 0; i < 3; i++) a.notas[i] = 1 + r.nextInt(10); // 1 a 10
			try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
		}
	}
}
