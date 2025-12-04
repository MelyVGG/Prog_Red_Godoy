package ejercicio6;

import java.util.List;
import java.util.Random;


public class Preceptor implements Runnable {
	private final List<Alumno> alumnos;
	public Preceptor(List<Alumno> alumnos) { this.alumnos = alumnos; }
	
	@Override
	public void run() {
		Random r = new Random();
		for (Alumno a : alumnos) {
			for (int i = 0; i < 9; i++) a.asistencia[i] = (r.nextInt(100) < 80) ? 1 : 0; // 80% chance presente
			a.esAlumnoRegular = a.porcentajeAsistencia() >= 75.0;
			try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
		}
	}
}
